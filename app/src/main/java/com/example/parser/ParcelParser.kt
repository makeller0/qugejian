package com.example.parser

import java.util.regex.Pattern

data class ParsedParcel(
    val pickupCode: String,
    val trackingNumber: String = "",
    val courierName: String = "快递包裹",
    val location: String = "",
    val rawText: String = "",
    val isValid: Boolean = false
)

object ParcelParser {

    private val PARCEL_KEYWORDS = listOf(
        "取件", "快递", "单号", "驿站", "丰巢", "提货", "快件", "包裹",
        "自提", "凭码", "运单", "兔喜", "妈妈驿站", "极兔", "中通", "圆通",
        "韵达", "顺丰", "京东", "柜", "提取码", "货架", "请凭"
    )

    private val COURIER_MAPPING = listOf(
        "菜鸟驿站" to "菜鸟驿站",
        "菜鸟" to "菜鸟驿站",
        "丰巢快递柜" to "丰巢快递柜",
        "丰巢" to "丰巢快递柜",
        "兔喜生活" to "兔喜生活",
        "兔喜" to "兔喜生活",
        "妈妈驿站" to "妈妈驿站",
        "顺丰速运" to "顺丰速运",
        "顺丰快递" to "顺丰速运",
        "顺丰" to "顺丰速运",
        "京东快递" to "京东快递",
        "京东" to "京东快递",
        "中通快递" to "中通快递",
        "中通" to "中通快递",
        "圆通速递" to "圆通速递",
        "圆通快递" to "圆通速递",
        "圆通" to "圆通速递",
        "申通快递" to "申通快递",
        "申通" to "申通快递",
        "韵达速递" to "韵达速递",
        "韵达快递" to "韵达速递",
        "韵达" to "韵达速递",
        "极兔速递" to "极兔速递",
        "极兔" to "极兔速递",
        "拼多多" to "拼多多驿站",
        "多多驿站" to "拼多多驿站",
        "多多买菜" to "拼多多驿站",
        "天猫超市" to "天猫超市",
        "淘宝" to "淘宝包裹",
        "中国邮政" to "中国邮政",
        "EMS" to "中国邮政EMS",
        "德邦快递" to "德邦快递",
        "德邦" to "德邦快递"
    )

    /**
     * Checks if the text looks like a parcel-related SMS or notification.
     */
    fun isParcelRelated(text: String): Boolean {
        if (text.isBlank()) return false
        val clean = text.trim()
        val hasKeyword = PARCEL_KEYWORDS.any { clean.contains(it) }
        val hasShelfPattern = Pattern.compile("\\b\\d{1,3}-\\d{1,3}-\\d{1,5}\\b").matcher(clean).find()
        val hasCodePattern = Pattern.compile("(?:取件码|提货码|凭码)[:：\\s为是]*[A-Za-z0-9\\-]+").matcher(clean).find()
        return hasKeyword || hasShelfPattern || hasCodePattern
    }

    /**
     * Parses the SMS text and extracts parcel details.
     */
    fun parse(smsText: String): ParsedParcel {
        val text = smsText.trim()
        if (text.isEmpty()) {
            return ParsedParcel(pickupCode = "", rawText = smsText, isValid = false)
        }

        val courier = extractCourier(text)
        val tracking = extractTrackingNumber(text)
        val pickupCode = extractPickupCode(text, tracking)
        val location = extractLocation(text, courier)

        val isValid = pickupCode.isNotBlank()

        return ParsedParcel(
            pickupCode = pickupCode,
            trackingNumber = tracking,
            courierName = courier,
            location = location,
            rawText = smsText,
            isValid = isValid
        )
    }

    private fun extractCourier(text: String): String {
        // First check SMS signature e.g. 【菜鸟驿站】 or [丰巢]
        val bracketPattern = Pattern.compile("【([^】]+)】|\\[([^\\]]+)\\]")
        val bracketMatcher = bracketPattern.matcher(text)
        if (bracketMatcher.find()) {
            val bracketContent = (bracketMatcher.group(1) ?: bracketMatcher.group(2) ?: "").trim()
            for ((key, name) in COURIER_MAPPING) {
                if (bracketContent.contains(key, ignoreCase = true)) {
                    return name
                }
            }
            if (bracketContent.isNotEmpty() && bracketContent.length <= 8) {
                return bracketContent
            }
        }

        // Search body text
        for ((key, name) in COURIER_MAPPING) {
            if (text.contains(key, ignoreCase = true)) {
                return name
            }
        }

        return "快递包裹"
    }

    private fun extractPickupCode(text: String, trackingNumber: String): String {
        // Priority 1: Shelf format like 3-2-104 or 1-201 or A-12-8 or 08-2-4019
        val shelfPattern = Pattern.compile("(?:凭|取件码|提货码|码[：:]|码为|码是)?\\s*([A-Za-z0-9]{1,4}-\\d{1,4}(?:-\\d{1,5})?)")
        val shelfMatcher = shelfPattern.matcher(text)
        if (shelfMatcher.find()) {
            val code = shelfMatcher.group(1)?.trim() ?: ""
            if (code.isNotBlank()) return cleanCode(code)
        }

        // Priority 2: Labeled pickup code e.g. 取件码：849201 or 取件码为982143 or 提货码 8921 or 凭 8921 到
        val labeledPattern = Pattern.compile(
            "(?:取件码|提货码|提件码|取货码|提取码|凭码|凭取件码|密码|开箱码|格口码|自提码|验证码|取包码)[:：\\s为是]*([A-Za-z0-9\\-#]{3,12})"
        )
        val labeledMatcher = labeledPattern.matcher(text)
        if (labeledMatcher.find()) {
            val code = labeledMatcher.group(1)?.trim() ?: ""
            if (code.isNotBlank() && code != trackingNumber) {
                return cleanCode(code)
            }
        }

        // Priority 3: "凭 XXXXX 到" or "凭 XXXXX 取件"
        val pingPattern = Pattern.compile("凭\\s*([A-Za-z0-9\\-]{3,10})\\s*(?:到|至|在|取件|提件)")
        val pingMatcher = pingPattern.matcher(text)
        if (pingMatcher.find()) {
            val code = pingMatcher.group(1)?.trim() ?: ""
            if (code.isNotBlank() && code != trackingNumber) {
                return cleanCode(code)
            }
        }

        // Priority 4: Short numeric sequence (4 to 8 digits) if not identical to tracking
        val digitPattern = Pattern.compile("(?:^|[^0-9A-Za-z])(\\d{4,8})(?:[^0-9A-Za-z]|$)")
        val digitMatcher = digitPattern.matcher(text)
        while (digitMatcher.find()) {
            val candidate = digitMatcher.group(1) ?: ""
            if (candidate != trackingNumber && !candidate.startsWith("19") && !candidate.startsWith("202")) {
                // Not a phone number, year or tracking number
                return candidate
            }
        }

        return ""
    }

    private fun extractTrackingNumber(text: String): String {
        // Priority 1: Labeled tracking number e.g. 运单号: 773123456789 or 单号 SF1234567890
        val labeledPattern = Pattern.compile(
            "(?:运单号|快递单号|单号为|单号是|包裹号|运单编号|快递号|单号)[:：\\s为是]*([A-Za-z0-9]{8,26})"
        )
        val labeledMatcher = labeledPattern.matcher(text)
        if (labeledMatcher.find()) {
            val num = labeledMatcher.group(1)?.trim() ?: ""
            if (num.isNotBlank()) return num
        }

        // Priority 2: Specific carrier tracking patterns
        val carrierPatterns = listOf(
            Pattern.compile("\\b(SF\\d{12,15})\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(JD[A-Za-z0-9]{8,15})\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(YT\\d{12,15})\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(JT\\d{12,15})\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(7[3578]\\d{10,14})\\b"),
            Pattern.compile("\\b([34][136]\\d{10,14})\\b")
        )

        for (pattern in carrierPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val candidate = matcher.group(1) ?: ""
                if (candidate.isNotBlank()) return candidate
            }
        }

        // Priority 3: Long numeric sequence (12 to 18 digits)
        val longNumPattern = Pattern.compile("\\b(\\d{12,18})\\b")
        val longNumMatcher = longNumPattern.matcher(text)
        if (longNumMatcher.find()) {
            return longNumMatcher.group(1) ?: ""
        }

        return ""
    }

    private fun extractLocation(text: String, courier: String): String {
        // Extract location like: 到【阳光小区西门菜鸟驿站】取件 or 存放在 时代广场1号丰巢快递柜
        val locPattern = Pattern.compile("(?:请到|到|至|于|存放在|暂存至|暂存在)\\s*([^，。！？\n\r]{2,25}?)(?:取件|自提|自取|柜|店|驿站|超市|货架)")
        val locMatcher = locPattern.matcher(text)
        if (locMatcher.find()) {
            var loc = locMatcher.group(1)?.trim() ?: ""
            loc = loc.replace("【", "").replace("】", "").trim()
            if (loc.isNotBlank() && loc.length >= 2) {
                // If the courier name is already included at the end or start
                if (!loc.endsWith("驿站") && !loc.endsWith("柜") && !loc.endsWith("超市")) {
                    loc += if (courier.contains("柜")) "快递柜" else "自提点"
                }
                return loc
            }
        }

        // Check for rack or shelf mention: e.g. 2号架 or A区
        val shelfPattern = Pattern.compile("([A-Za-z0-9一二三四五六七八九十]+号(?:货架|架|柜))")
        val shelfMatcher = shelfPattern.matcher(text)
        if (shelfMatcher.find()) {
            return shelfMatcher.group(1) ?: ""
        }

        return ""
    }

    private fun cleanCode(code: String): String {
        return code.trimEnd(',', '.', '，', '。', '！', '!', ' ', ';', '；', '#')
    }
}
