package com.example.ui

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import com.example.BuildConfig
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ParcelThemeColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ParcelItem
import com.example.parser.ParcelParser
import com.example.parser.ParsedParcel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelMainScreen(
    viewModel: ParcelViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parcels by viewModel.displayedParcels.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingCount.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Handle toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openManualAddDialog(true) },
                containerColor = ParcelThemeColors.primaryActionBg,
                contentColor = ParcelThemeColors.primaryActionText,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("fab_add_parcel")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "手动添加", modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "手动添加",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bar (Clean Minimalism)
            HeaderSection(
                pendingCount = pendingCount,
                themeMode = uiState.themeMode,
                colorPalette = uiState.colorPalette,
                onPasteClipboardClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                    if (!clipText.isNullOrBlank()) {
                        viewModel.checkClipboardContent(clipText)
                        if (!ParcelParser.isParcelRelated(clipText)) {
                            viewModel.openPasteSmsDialog(true)
                        }
                    } else {
                        viewModel.openPasteSmsDialog(true)
                    }
                },
                onMoreMenuAction = { action ->
                    when (action) {
                        "clear_picked" -> viewModel.clearAllPickedUp()
                        "load_samples" -> viewModel.loadSampleParcels()
                        "paste_sms" -> viewModel.openPasteSmsDialog(true)
                        "open_theme" -> viewModel.openThemeDialog(true)
                        "about" -> showAboutDialog = true
                    }
                }
            )

            // Clipboard Detection Banner (Clean Minimalist Lilac Pill)
            AnimatedVisibility(
                visible = uiState.showClipboardPrompt && uiState.parsedCandidate != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                uiState.parsedCandidate?.let { candidate ->
                    ClipboardDetectionBanner(
                        candidate = candidate,
                        onConfirm = { viewModel.confirmImportCandidate() },
                        onDismiss = { viewModel.dismissClipboardPrompt() }
                    )
                }
            }

            // Filter Tabs Row
            FilterTabsRow(
                currentFilter = uiState.currentFilter,
                pendingCount = pendingCount,
                totalCount = parcels.size,
                onFilterSelected = { viewModel.setFilter(it) }
            )

            // Parcel List / Empty View
            if (parcels.isEmpty()) {
                EmptyStateView(
                    filter = uiState.currentFilter,
                    onPasteSmsClick = { viewModel.openPasteSmsDialog(true) },
                    onLoadSamplesClick = { viewModel.loadSampleParcels() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(parcels, key = { it.id }) { parcel ->
                        SwipeableParcelCard(
                            parcel = parcel,
                            onSwipeLeftCompleted = {
                                viewModel.markPickedUpWithAutoAdvance(parcel)
                            },
                            onToggleStatus = { viewModel.togglePickupStatus(parcel) },
                            onDelete = { viewModel.deleteParcel(parcel) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    // Manual Add Dialog
    if (uiState.isManualAddDialogOpen) {
        ManualAddDialog(
            onDismiss = { viewModel.openManualAddDialog(false) },
            onConfirm = { code, courier, tracking, loc ->
                viewModel.addNewManualParcel(code, courier, tracking, loc)
            }
        )
    }

    // Paste SMS Dialog
    if (uiState.isPasteSmsDialogOpen) {
        PasteSmsDialog(
            onDismiss = { viewModel.openPasteSmsDialog(false) },
            onConfirm = { smsText ->
                viewModel.importSmsText(smsText)
            }
        )
    }

    // Theme Selector Dialog
    if (uiState.isThemeDialogOpen) {
        ThemeSelectorDialog(
            currentMode = uiState.themeMode,
            currentPalette = uiState.colorPalette,
            onDismiss = { viewModel.openThemeDialog(false) },
            onSelectMode = { viewModel.setThemeMode(it) },
            onSelectPalette = { viewModel.setColorPalette(it) }
        )
    }

    // About & Version Dialog
    if (showAboutDialog) {
        AboutAppDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
}

@Composable
fun HeaderSection(
    pendingCount: Int,
    themeMode: AppThemeMode,
    colorPalette: AppColorPalette,
    onPasteClipboardClick: () -> Unit,
    onMoreMenuAction: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "待取件",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.5).sp,
                    color = ParcelThemeColors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (pendingCount > 0) "今天有 $pendingCount 个包裹在等您" else "您目前无包裹",
                    fontSize = 16.sp,
                    color = ParcelThemeColors.textSecondary,
                    fontWeight = FontWeight.Normal
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pending Count Pill Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (pendingCount > 0) ParcelThemeColors.accentBadgeBg else ParcelThemeColors.containerVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$pendingCount",
                        color = if (pendingCount > 0) ParcelThemeColors.accentBadgeText else ParcelThemeColors.textMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                // Quick Paste Action Button
                IconButton(
                    onClick = onPasteClipboardClick,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(ParcelThemeColors.containerVariant)
                        .size(44.dp)
                        .testTag("btn_paste_clipboard")
                ) {
                    Icon(
                        Icons.Default.ContentPaste,
                        contentDescription = "识别剪切板",
                        modifier = Modifier.size(20.dp),
                        tint = ParcelThemeColors.textPrimary
                    )
                }

                // More Options Menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(ParcelThemeColors.containerVariant)
                            .size(44.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "更多操作",
                            tint = ParcelThemeColors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(ParcelThemeColors.dialogSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("粘贴短信提取", fontSize = 14.sp, color = ParcelThemeColors.textPrimary) },
                            leadingIcon = { Icon(Icons.Default.ContentPaste, contentDescription = null, tint = ParcelThemeColors.textPrimary, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                menuExpanded = false
                                onMoreMenuAction("paste_sms")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("载入示例快递短信", fontSize = 14.sp, color = ParcelThemeColors.textPrimary) },
                            leadingIcon = { Icon(Icons.Default.LocalShipping, contentDescription = null, tint = ParcelThemeColors.textPrimary, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                menuExpanded = false
                                onMoreMenuAction("load_samples")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("主题与配色 (${colorPalette.title} · ${themeMode.title})", fontSize = 14.sp, color = ParcelThemeColors.textPrimary) },
                            leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, tint = ParcelThemeColors.textPrimary, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                menuExpanded = false
                                onMoreMenuAction("open_theme")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("关于与版本 (v${BuildConfig.VERSION_NAME})", fontSize = 14.sp, color = ParcelThemeColors.textPrimary) },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = ParcelThemeColors.textPrimary, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                menuExpanded = false
                                onMoreMenuAction("about")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("清空已取件记录", fontSize = 14.sp, color = ParcelThemeColors.textPrimary) },
                            leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = ParcelThemeColors.textMuted, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                menuExpanded = false
                                onMoreMenuAction("clear_picked")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClipboardDetectionBanner(
    candidate: ParsedParcel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = ParcelThemeColors.bannerBg),
        border = BorderStroke(1.dp, ParcelThemeColors.bannerBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = "剪切板新提醒",
                    color = ParcelThemeColors.bannerText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (candidate.pickupCode.isNotBlank()) "检测到取件码: ${candidate.pickupCode}" else "检测到新的取件信息",
                    color = ParcelThemeColors.bannerText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (candidate.courierName.isNotBlank() || candidate.location.isNotBlank()) {
                    Text(
                        text = listOf(candidate.courierName, candidate.location).filter { it.isNotBlank() }.joinToString(" · "),
                        color = ParcelThemeColors.bannerText.copy(alpha = 0.75f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("忽略", color = ParcelThemeColors.bannerText.copy(alpha = 0.7f), fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParcelThemeColors.bannerButton,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(percent = 50),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("存入", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun FilterTabsRow(
    currentFilter: ParcelFilter,
    pendingCount: Int,
    totalCount: Int,
    onFilterSelected: (ParcelFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ParcelFilter.values().forEach { filter ->
            val isSelected = currentFilter == filter
            val labelText = when (filter) {
                ParcelFilter.PENDING -> "待取件 ($pendingCount)"
                ParcelFilter.PICKED_UP -> "已取件"
                ParcelFilter.ALL -> "全部"
            }

            val bgColor by animateColorAsState(
                if (isSelected) ParcelThemeColors.accentBadgeBg else ParcelThemeColors.containerVariant,
                label = "tab_bg"
            )
            val textColor by animateColorAsState(
                if (isSelected) ParcelThemeColors.accentBadgeText else ParcelThemeColors.textMuted,
                label = "tab_text"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .then(
                        if (!isSelected) Modifier.border(1.dp, ParcelThemeColors.cardBorder, RoundedCornerShape(16.dp))
                        else Modifier
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labelText,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Wraps ParcelCard in a SwipeToDismissBox.
 * Swiping to the left marks the item as picked up and auto-advances.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableParcelCard(
    parcel: ParcelItem,
    onSwipeLeftCompleted: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onSwipeLeftCompleted()
                true
            } else {
                false
            }
        },
        positionalThreshold = { it * 0.35f }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = !parcel.isPickedUp,
        backgroundContent = {
            val isSwipingToDismiss = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val bgColor by animateColorAsState(
                if (isSwipingToDismiss) ParcelThemeColors.successAccent
                else ParcelThemeColors.successAccent.copy(alpha = 0.45f),
                label = "swipe_bg"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(32.dp))
                    .background(bgColor)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "取件完成",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "完成取件",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
        modifier = modifier
    ) {
        ParcelCard(
            parcel = parcel,
            onToggleStatus = onToggleStatus,
            onDelete = onDelete
        )
    }
}

@Composable
fun ParcelCard(
    parcel: ParcelItem,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_parcel_${parcel.id}"),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (parcel.isPickedUp) {
                ParcelThemeColors.cardBackground.copy(alpha = 0.75f)
            } else {
                ParcelThemeColors.cardBackground
            }
        ),
        border = BorderStroke(1.dp, ParcelThemeColors.cardBorder),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (parcel.isPickedUp) 0.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 22.dp)
        ) {
            // Top Row: Indicator Dot + Courier Location + Relative Time + subtle delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Small circular status indicator dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (parcel.isPickedUp) ParcelThemeColors.textMuted else ParcelThemeColors.unpickedDot)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val locationLabel = if (parcel.location.isNotBlank()) {
                        "${parcel.courierName} (${parcel.location})"
                    } else {
                        parcel.courierName
                    }
                    Text(
                        text = locationLabel,
                        color = ParcelThemeColors.textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val timeStr = remember(parcel.createdAt) {
                        SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(parcel.createdAt))
                    }
                    Text(
                        text = timeStr,
                        color = ParcelThemeColors.textMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "删除",
                            tint = ParcelThemeColors.textMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // THE GIANT PICKUP CODE: text-6xl font-black tracking-tighter
            Text(
                text = parcel.pickupCode,
                fontSize = 54.sp,
                lineHeight = 58.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.5).sp,
                color = if (parcel.isPickedUp) {
                    ParcelThemeColors.textMuted.copy(alpha = 0.45f)
                } else {
                    ParcelThemeColors.textPrimary
                },
                textDecoration = if (parcel.isPickedUp) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .testTag("text_pickup_code_${parcel.id}")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Action Row: Courier Subtitle / Tracking Tail + "已取" action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = parcel.courierName,
                        color = ParcelThemeColors.textSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                    if (parcel.trackingNumber.isNotBlank()) {
                        val suffix = parcel.trackingNumber.takeLast(4)
                        Row(
                            modifier = Modifier
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(parcel.trackingNumber))
                                    Toast.makeText(context, "单号已复制: ${parcel.trackingNumber}", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "尾号 $suffix",
                                fontSize = 13.sp,
                                color = ParcelThemeColors.textMuted
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "复制单号",
                                modifier = Modifier.size(12.dp),
                                tint = ParcelThemeColors.textMuted
                            )
                        }
                    } else {
                        Text(
                            text = "待签收",
                            fontSize = 13.sp,
                            color = ParcelThemeColors.textMuted
                        )
                    }

                    // Swipe hint
                    if (!parcel.isPickedUp) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = ParcelThemeColors.textMuted.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "向左划标记完成",
                                fontSize = 11.sp,
                                color = ParcelThemeColors.textMuted.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Clean Minimalism Action Button
                if (!parcel.isPickedUp) {
                    Button(
                        onClick = onToggleStatus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParcelThemeColors.containerVariant,
                            contentColor = ParcelThemeColors.textPrimary
                        ),
                        border = BorderStroke(1.dp, ParcelThemeColors.cardBorder),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 9.dp),
                        modifier = Modifier.testTag("btn_mark_picked_${parcel.id}")
                    ) {
                        Text(
                            text = "已取",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Button(
                        onClick = onToggleStatus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParcelThemeColors.accentBadgeBg,
                            contentColor = ParcelThemeColors.accentBadgeText
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = "恢复",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    filter: ParcelFilter,
    onPasteSmsClick: () -> Unit,
    onLoadSamplesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(ParcelThemeColors.containerVariant)
                .border(1.dp, ParcelThemeColors.cardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.LocalShipping,
                contentDescription = null,
                tint = ParcelThemeColors.textPrimary,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Exact match for user request: "若没有取件码 则显示您目前无包裹"
        Text(
            text = when (filter) {
                ParcelFilter.PENDING -> "您目前无包裹"
                ParcelFilter.PICKED_UP -> "暂无已取件记录"
                ParcelFilter.ALL -> "您目前无包裹"
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = ParcelThemeColors.textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (filter) {
                ParcelFilter.PENDING -> "所有快递取件码均已处理完毕，享受惬意生活"
                ParcelFilter.PICKED_UP -> "取件完成的包裹历史记录将保留在此处"
                ParcelFilter.ALL -> "复制短信或手动添加取件码，开启轻松管理"
            },
            textAlign = TextAlign.Center,
            color = ParcelThemeColors.textSecondary,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onPasteSmsClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = ParcelThemeColors.primaryActionBg,
                contentColor = ParcelThemeColors.primaryActionText
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("粘贴短信提取", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLoadSamplesClick,
            border = BorderStroke(1.dp, ParcelThemeColors.cardBorder),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text("快速载入测试短信", fontSize = 14.sp, color = ParcelThemeColors.textSecondary)
        }
    }
}

@Composable
fun PasteSmsDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var smsText by remember {
        mutableStateOf(clipboardManager.getText()?.text ?: "")
    }

    val sampleSmsList = listOf(
        "【菜鸟驿站】凭 3-2-104 到 阳光小区菜鸟驿站 取件，单号 7731234567890",
        "【丰巢】您的快件已存入 时代广场丰巢快递柜，取件码：849201，单号 SF1492039102",
        "【兔喜生活】您的中通包裹已到站，凭取件码 A-12-8 到中通超市取件，单号 75423423423"
    )

    val parsed = remember(smsText) {
        ParcelParser.parse(smsText)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = ParcelThemeColors.dialogSurface,
            border = BorderStroke(1.dp, ParcelThemeColors.cardBorder),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "粘贴短信识别",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = ParcelThemeColors.textPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = smsText,
                    onValueChange = { smsText = it },
                    placeholder = { Text("在此粘贴整条快递通知短信…", fontSize = 14.sp, color = ParcelThemeColors.textMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("input_sms_text"),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "快速填入示例：",
                    fontSize = 12.sp,
                    color = ParcelThemeColors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val titles = listOf("菜鸟", "丰巢", "兔喜")
                    sampleSmsList.forEachIndexed { idx, sample ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ParcelThemeColors.containerVariant)
                                .border(1.dp, ParcelThemeColors.cardBorder, RoundedCornerShape(10.dp))
                                .clickable { smsText = sample }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(titles[idx], fontSize = 12.sp, color = ParcelThemeColors.textPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (parsed.isValid) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ParcelThemeColors.accentBadgeBg),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "识别结果：",
                                fontSize = 12.sp,
                                color = ParcelThemeColors.accentBadgeText.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "取件码：${parsed.pickupCode}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = ParcelThemeColors.accentBadgeText
                            )
                            Text(
                                text = "驿站/快递：${parsed.courierName}  ${if (parsed.trackingNumber.isNotBlank()) "单号: ${parsed.trackingNumber}" else ""}",
                                fontSize = 12.sp,
                                color = ParcelThemeColors.accentBadgeText.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消", color = ParcelThemeColors.textMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(smsText) },
                        enabled = smsText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParcelThemeColors.primaryActionBg,
                            contentColor = ParcelThemeColors.primaryActionText
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("btn_confirm_import_sms")
                    ) {
                        Text("识别并提取", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ManualAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (code: String, courier: String, tracking: String, loc: String) -> Unit
) {
    var pickupCode by remember { mutableStateOf("") }
    var courierName by remember { mutableStateOf("菜鸟驿站") }
    var trackingNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val commonCouriers = listOf("菜鸟驿站", "丰巢快递柜", "兔喜生活", "顺丰速运", "京东快递", "申通快递")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = ParcelThemeColors.dialogSurface,
            border = BorderStroke(1.dp, ParcelThemeColors.cardBorder),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "手动添加取件码",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = ParcelThemeColors.textPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = pickupCode,
                    onValueChange = { pickupCode = it },
                    label = { Text("取件码 (必填，如 5-2-402)") },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_manual_pickup_code")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "快递 / 驿站：",
                    fontSize = 13.sp,
                    color = ParcelThemeColors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonCouriers.take(3).forEach { name ->
                        val isChosen = courierName == name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isChosen) ParcelThemeColors.accentBadgeBg else ParcelThemeColors.containerVariant)
                                .border(
                                    1.dp,
                                    if (isChosen) Color.Transparent else ParcelThemeColors.cardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { courierName = name }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                color = if (isChosen) ParcelThemeColors.accentBadgeText else ParcelThemeColors.textPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonCouriers.drop(3).forEach { name ->
                        val isChosen = courierName == name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isChosen) ParcelThemeColors.accentBadgeBg else ParcelThemeColors.containerVariant)
                                .border(
                                    1.dp,
                                    if (isChosen) Color.Transparent else ParcelThemeColors.cardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { courierName = name }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                color = if (isChosen) ParcelThemeColors.accentBadgeText else ParcelThemeColors.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = trackingNumber,
                    onValueChange = { trackingNumber = it },
                    label = { Text("快递单号 (选填)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("货架 / 存放位置 (选填，如 东门/2号柜)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消", color = ParcelThemeColors.textMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(pickupCode, courierName, trackingNumber, location)
                        },
                        enabled = pickupCode.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParcelThemeColors.primaryActionBg,
                            contentColor = ParcelThemeColors.primaryActionText
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("btn_confirm_manual_add")
                    ) {
                        Text("保存", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Dialog for selecting high-end artistic palettes (Morandi, Monet, etc.) and display mode.
 */
@Composable
fun ThemeSelectorDialog(
    currentMode: AppThemeMode,
    currentPalette: AppColorPalette,
    onDismiss: () -> Unit,
    onSelectMode: (AppThemeMode) -> Unit,
    onSelectPalette: (AppColorPalette) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = ParcelThemeColors.dialogSurface,
            border = BorderStroke(1.dp, ParcelThemeColors.cardBorder),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ParcelThemeColors.accentBadgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            tint = ParcelThemeColors.accentBadgeText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "主题与艺术配色",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ParcelThemeColors.textPrimary
                        )
                        Text(
                            text = "精选高阶调色方案，轻触即时切换预览",
                            fontSize = 12.sp,
                            color = ParcelThemeColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 1: Color Palettes
                Text(
                    text = "艺术配色方案",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ParcelThemeColors.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                AppColorPalette.values().forEach { palette ->
                    val isSelected = currentPalette == palette
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ParcelThemeColors.accentBadgeBg.copy(alpha = 0.45f) else ParcelThemeColors.containerVariant
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) ParcelThemeColors.primaryActionBg else ParcelThemeColors.containerVariantBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectPalette(palette) }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Three overlapping color dots swatch preview
                            Box(
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(26.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(palette.previewPrimary)
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(start = 12.dp)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(palette.previewAccent)
                                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(start = 24.dp)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(palette.previewBg)
                                        .border(1.dp, Color.Black.copy(alpha = 0.15f), CircleShape)
                                )
                            }

                            // Palette Details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = palette.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = ParcelThemeColors.textPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ParcelThemeColors.accentBadgeBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = palette.tag,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ParcelThemeColors.accentBadgeText
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = palette.subtitle,
                                    fontSize = 11.sp,
                                    color = ParcelThemeColors.textSecondary,
                                    lineHeight = 15.sp
                                )
                            }

                            // Selection Indicator Checkmark
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(ParcelThemeColors.primaryActionBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "已选择",
                                        tint = ParcelThemeColors.primaryActionText,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Display Mode (System / Light / Dark)
                Text(
                    text = "显示外观模式",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ParcelThemeColors.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.values().forEach { mode ->
                        val isModeSelected = currentMode == mode
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isModeSelected) ParcelThemeColors.primaryActionBg else ParcelThemeColors.containerVariant
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isModeSelected) ParcelThemeColors.primaryActionBg else ParcelThemeColors.containerVariantBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectMode(mode) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val modeIcon = when (mode) {
                                    AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                    AppThemeMode.LIGHT -> Icons.Default.LightMode
                                    AppThemeMode.DARK -> Icons.Default.DarkMode
                                }
                                Icon(
                                    imageVector = modeIcon,
                                    contentDescription = mode.title,
                                    tint = if (isModeSelected) ParcelThemeColors.primaryActionText else ParcelThemeColors.textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mode.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isModeSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isModeSelected) ParcelThemeColors.primaryActionText else ParcelThemeColors.textPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentPalette.title} · ${currentMode.title}",
                        fontSize = 12.sp,
                        color = ParcelThemeColors.textMuted
                    )
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParcelThemeColors.primaryActionBg,
                            contentColor = ParcelThemeColors.primaryActionText
                        )
                    ) {
                        Text("完成", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AboutAppDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ParcelThemeColors.dialogSurface),
            border = BorderStroke(1.dp, ParcelThemeColors.cardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ParcelThemeColors.accentBadgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = ParcelThemeColors.accentBadgeText
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "取件码助手",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParcelThemeColors.textPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ParcelThemeColors.containerVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "版本 v${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ParcelThemeColors.accentBadgeText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "智能识别快递短信取件码 · 大字号极简设计 · 桌面小组件",
                    fontSize = 13.sp,
                    color = ParcelThemeColors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParcelThemeColors.primaryActionBg,
                        contentColor = ParcelThemeColors.primaryActionText
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("我知道了", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

