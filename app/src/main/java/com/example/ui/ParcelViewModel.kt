package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ParcelDatabase
import com.example.data.ParcelItem
import com.example.data.ParcelRepository
import com.example.parser.ParcelParser
import com.example.parser.ParsedParcel
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import android.content.Context
import android.content.Intent

enum class ParcelFilter(val label: String) {
    PENDING("待取件"),
    PICKED_UP("已取件"),
    ALL("全部")
}

data class ParcelUiState(
    val currentFilter: ParcelFilter = ParcelFilter.PENDING,
    val searchQuery: String = "",
    val detectedClipboardText: String? = null,
    val parsedCandidate: ParsedParcel? = null,
    val showClipboardPrompt: Boolean = false,
    val isManualAddDialogOpen: Boolean = false,
    val isPasteSmsDialogOpen: Boolean = false,
    val isThemeDialogOpen: Boolean = false,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val toastMessage: String? = null
)

class ParcelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ParcelRepository

    init {
        val db = ParcelDatabase.getDatabase(application)
        repository = ParcelRepository(application, db.parcelDao())
    }

    private val _uiState = MutableStateFlow(ParcelUiState())
    val uiState: StateFlow<ParcelUiState> = _uiState.asStateFlow()

    private var lastDismissedClipboard: String = ""

    val pendingCount: StateFlow<Int> = repository.pendingCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val displayedParcels: StateFlow<List<ParcelItem>> = combine(
        repository.allParcels,
        _uiState
    ) { allList, state ->
        val filteredByTab = when (state.currentFilter) {
            ParcelFilter.PENDING -> allList.filter { !it.isPickedUp }
            ParcelFilter.PICKED_UP -> allList.filter { it.isPickedUp }
            ParcelFilter.ALL -> allList
        }

        if (state.searchQuery.isBlank()) {
            filteredByTab
        } else {
            val q = state.searchQuery.trim().lowercase()
            filteredByTab.filter { item ->
                item.pickupCode.lowercase().contains(q) ||
                        item.courierName.lowercase().contains(q) ||
                        item.trackingNumber.lowercase().contains(q) ||
                        item.location.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Check clipboard content. If it's a parcel-related SMS and hasn't been imported or dismissed yet,
     * display the quick import prompt.
     */
    fun checkClipboardContent(text: String?) {
        if (text.isNullOrBlank()) return
        val trimmed = text.trim()
        if (trimmed == lastDismissedClipboard) return
        if (!ParcelParser.isParcelRelated(trimmed)) return

        val parsed = ParcelParser.parse(trimmed)
        if (parsed.isValid) {
            _uiState.value = _uiState.value.copy(
                detectedClipboardText = trimmed,
                parsedCandidate = parsed,
                showClipboardPrompt = true
            )
        }
    }

    fun dismissClipboardPrompt() {
        _uiState.value.detectedClipboardText?.let {
            lastDismissedClipboard = it
        }
        _uiState.value = _uiState.value.copy(
            showClipboardPrompt = false,
            parsedCandidate = null
        )
    }

    fun confirmImportCandidate() {
        val candidate = _uiState.value.parsedCandidate ?: return
        viewModelScope.launch {
            val (success, _) = repository.importFromSms(candidate.rawText)
            lastDismissedClipboard = candidate.rawText
            _uiState.value = _uiState.value.copy(
                showClipboardPrompt = false,
                parsedCandidate = null,
                toastMessage = if (success) "已识别并保存取件码：${candidate.pickupCode}" else "未能识别有效取件码"
            )
        }
    }

    fun importSmsText(sms: String) {
        viewModelScope.launch {
            val (success, item) = repository.importFromSms(sms)
            _uiState.value = _uiState.value.copy(
                isPasteSmsDialogOpen = false,
                toastMessage = if (success && item != null) "已提取：${item.pickupCode} (${item.courierName})" else "无法识别取件码，请检查短信内容"
            )
        }
    }

    fun addNewManualParcel(pickupCode: String, courier: String, tracking: String, location: String) {
        if (pickupCode.isBlank()) return
        viewModelScope.launch {
            val item = ParcelItem(
                pickupCode = pickupCode.trim(),
                courierName = if (courier.isBlank()) "快递包裹" else courier.trim(),
                trackingNumber = tracking.trim(),
                location = location.trim(),
                isPickedUp = false,
                createdAt = System.currentTimeMillis()
            )
            repository.insert(item)
            _uiState.value = _uiState.value.copy(
                isManualAddDialogOpen = false,
                toastMessage = "已添加取件码：${item.pickupCode}"
            )
        }
    }

    fun togglePickupStatus(item: ParcelItem) {
        viewModelScope.launch {
            if (item.isPickedUp) {
                repository.markAsPending(item.id)
                _uiState.value = _uiState.value.copy(toastMessage = "已恢复为待取件")
            } else {
                repository.markAsPickedUp(item.id)
                _uiState.value = _uiState.value.copy(toastMessage = "已标记为已取件")
            }
        }
    }

    fun deleteParcel(item: ParcelItem) {
        viewModelScope.launch {
            repository.delete(item)
            _uiState.value = _uiState.value.copy(toastMessage = "已删除记录")
        }
    }

    fun clearAllPickedUp() {
        viewModelScope.launch {
            repository.clearAllPickedUp()
            _uiState.value = _uiState.value.copy(toastMessage = "已清空所有已取件记录")
        }
    }

    fun setFilter(filter: ParcelFilter) {
        _uiState.value = _uiState.value.copy(currentFilter = filter)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun openManualAddDialog(open: Boolean) {
        _uiState.value = _uiState.value.copy(isManualAddDialogOpen = open)
    }

    fun openPasteSmsDialog(open: Boolean) {
        _uiState.value = _uiState.value.copy(isPasteSmsDialogOpen = open)
    }

    fun openThemeDialog(open: Boolean) {
        _uiState.value = _uiState.value.copy(isThemeDialogOpen = open)
    }

    fun setThemeMode(mode: AppThemeMode) {
        _uiState.value = _uiState.value.copy(
            themeMode = mode,
            isThemeDialogOpen = false,
            toastMessage = "主题已切换为：${mode.title}"
        )
    }

    /**
     * Called when a parcel card is swiped left:
     * Marks as picked up, smoothly auto-advancing to the next pickup code.
     */
    fun markPickedUpWithAutoAdvance(item: ParcelItem) {
        viewModelScope.launch {
            repository.markAsPickedUp(item.id)
            _uiState.value = _uiState.value.copy(
                toastMessage = "已取件：${item.pickupCode}，已切换至下一件"
            )
        }
    }

    fun clearToastMessage() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    /**
     * Preload standard realistic delivery SMS examples for quick preview/demo
     */
    fun loadSampleParcels() {
        viewModelScope.launch {
            val samples = listOf(
                "【菜鸟驿站】凭 3-2-104 到 阳光小区西门菜鸟驿站 2号货架取件，运单号 7731234567890，请及时取件。",
                "【丰巢】您的顺丰快件已暂存时代广场1号丰巢快递柜，取件码：849201，运单号：SF1492039102，请在24小时内取出。",
                "【兔喜生活】您的中通包裹已到站，凭取件码 A-12-8 到中通生活超市取件，单号 75423423423，请尽快提取。"
            )
            for (sms in samples) {
                repository.importFromSms(sms)
            }
            _uiState.value = _uiState.value.copy(toastMessage = "已导入示例快递取件短信")
        }
    }
}
