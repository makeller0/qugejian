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
        "韵达", "顺丰", "京东", "柜", "提取码", "货架", "请凭", "取件码",
        "提货码", "自提码", "开箱码", "开柜码", "取包码", "格口", "快递柜",
        "自提点", "取货", "提件", "代收点", "菜鸟"
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
        val hasShelfPattern = Pattern.compile("(?<!\\d)(?:[A-Za-z]|\\d{1,3})-\\d{1,3}-\\d{1,5}(?!\\d)").matcher(clean).find()
        val hasCodePattern = Pattern.compile(
            "(?:取件码|提货码|提件码|取货码|提取码|自提码|开箱码|开柜码|格口码|取包码|凭码|取件密码|提货密码|密码|凭取件码|验证码)[:：\\s为是]*[A-Za-z0-9\\-#]+"
        ).matcher(clean).find()
        val hasPingPattern = Pattern.compile("(?:请凭|凭)[A-Za-z0-9\\-\\s#]{3,10}(?:到|至|在|取件|自提|提件|提货|取货)").matcher(clean).find()
        val hasCourierSign = COURIER_MAPPING.any { clean.contains(it.first) }
        return hasKeyword || hasShelfPattern || hasCodePattern || hasPingPattern || hasCourierSign
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

    /**
     * Checks if a candidate string is a date, time, year, courier hotline, or phone number.
     * Prevents dates (e.g. 09-06, 2026-09-06, 9-6) from being mistakenly parsed as pickup codes.
     */
    fun isDateOrTimeOrPhone(candidate: String, fullText: String): Boolean {
        val clean = candidate.trim()
        if (clean.isEmpty()) return true

        // 1. Full date format: 2026-09-06, 2024/05/12, 2025.10.01, 2026-9-6
        if (Pattern.compile("^(?:19|20)\\d{2}[-/.]\\d{1,2}[-/.]\\d{1,2}$").matcher(clean).matches()) {
            return true
        }

        // 2. Year-Month: 2026-09, 2024/05
        if (Pattern.compile("^(?:19|20)\\d{2}[-/.]\\d{1,2}$").matcher(clean).matches()) {
            return true
        }

        // 3. Month-Day format: 09-06, 9-6, 12-31, 05-01, 09/06, 9/6, 09.06
        val mdMatcher = Pattern.compile("^(\\d{1,2})[-/.](\\d{1,2})$").matcher(clean)
        if (mdMatcher.matches()) {
            val m = mdMatcher.group(1)?.toIntOrNull() ?: 0
            val d = mdMatcher.group(2)?.toIntOrNull() ?: 0
            if (m in 1..12 && d in 1..31) {
                if (clean.startsWith("0") || clean.contains("-0") || clean.contains("/0") || clean.contains(".0")) {
                    return true
                }
                val idx = fullText.indexOf(clean)
                if (idx >= 0) {
                    val start = (idx - 8).coerceAtLeast(0)
                    val end = (idx + clean.length + 8).coerceAtMost(fullText.length)
                    val context = fullText.substring(start, end)
                    if (context.contains(Regex("[月日号点分时年前后至于在送达到达超时截止营业有效期失效]"))) {
                        return true
                    }
                }
                return true
            }
        }

        // 4. Time format: 14:30, 08:00, 18:00:00, or time range 08:00-21:00
        if (clean.contains(":") || clean.contains("：")) {
            return true
        }

        // 5. Standalone 4-digit Year: 2024, 2025, 2026, 2027, 1999
        if (Pattern.compile("^(?:19\\d{2}|20\\d{2})$").matcher(clean).matches()) {
            return true
        }

        // 6. Common courier customer hotlines & phone numbers (e.g. 95338, 95543, 400xxxxxxx)
        if (Pattern.compile("^(?:95\\d{3,4}|400\\d{7}|800\\d{7}|1[3-9]\\d{9})$").matcher(clean).matches()) {
            return true
        }

        return false
    }

    private fun extractPickupCode(text: String, trackingNumber: String): String {
        val labeledPrefixes = "(?:取件码|提货码|提件码|取货码|提取码|自提码|开箱码|开柜码|格口码|取包码|凭码|凭取件码|取件密码|提货密码|密码|验证码|箱门码)"

        // Priority 1.1: Explicitly labeled bracketed / quoted codes (e.g. 取件码【849201】, 自提码“A802”)
        val labeledBracketPattern = Pattern.compile(
            "$labeledPrefixes[:：\\s为是]*[【“\"\\[]([A-Za-z0-9\\-\\s#]{2,12})[】”\"\\]]"
        )
        val lbMatcher = labeledBracketPattern.matcher(text)
        if (lbMatcher.find()) {
            val candidate = cleanCode(lbMatcher.group(1) ?: "")
            if (candidate.isNotBlank() && candidate != trackingNumber && !isDateOrTimeOrPhone(candidate, text)) {
                return normalizeCode(candidate)
            }
        }

        // Priority 1.2: Explicitly labeled shelf format with space (e.g. 取件码 3 2 104)
        val labeledSpacePattern = Pattern.compile(
            "$labeledPrefixes[:：\\s为是]*([A-Za-z0-9]{1,3}\\s+\\d{1,3}\\s+\\d{1,5})"
        )
        val lspMatcher = labeledSpacePattern.matcher(text)
        if (lspMatcher.find()) {
            val candidate = cleanCode(lspMatcher.group(1) ?: "")
            if (candidate.isNotBlank()) {
                return normalizeCode(candidate)
            }
        }

        // Priority 1.3: Explicitly labeled code (WITH or WITHOUT hyphen! e.g. 849201, 3-2-104, 982143, A802, 491023)
        val labeledGeneralPattern = Pattern.compile(
            "$labeledPrefixes[:：\\s为是]*([A-Za-z0-9\\-#]{3,12})"
        )
        val lgMatcher = labeledGeneralPattern.matcher(text)
        if (lgMatcher.find()) {
            val candidate = cleanCode(lgMatcher.group(1) ?: "")
            if (candidate.isNotBlank() && candidate != trackingNumber && !isDateOrTimeOrPhone(candidate, text)) {
                return normalizeCode(candidate)
            }
        }

        // Priority 1.4: Short label "码: 849201", "码为 849201", "码是 982143"
        val maPattern = Pattern.compile(
            "(?:^|[^A-Za-z0-9])码[:：为是\\s]+([A-Za-z0-9\\-#]{3,10})"
        )
        val maMatcher = maPattern.matcher(text)
        if (maMatcher.find()) {
            val candidate = cleanCode(maMatcher.group(1) ?: "")
            if (candidate.isNotBlank() && candidate != trackingNumber && !isDateOrTimeOrPhone(candidate, text)) {
                return normalizeCode(candidate)
            }
        }

        // Priority 2: Action-oriented "凭 XXXXX (到/至/在/取件/自提/提件/提货/取货/开箱)" or "请凭 XXXXX"
        val pingPatterns = listOf(
            Pattern.compile("(?:请凭|凭)[【“\"\\[]([A-Za-z0-9\\-\\s#]{2,12})[】”\"\\]]"),
            Pattern.compile("(?:请凭|凭)\\s*([A-Za-z0-9]{1,3}\\s+\\d{1,3}\\s+\\d{1,5})\\s*(?:到|至|在|取件|自提|提件|提货|取货|开箱)"),
            Pattern.compile("(?:请凭|凭)\\s*([A-Za-z0-9\\-#]{3,12})\\s*(?:到|至|在|取件|自提|提件|提货|取货|开箱)"),
            Pattern.compile("(?:请凭|凭)\\s*([A-Za-z0-9\\-#]{3,12})\\s*(?:于|在|请|及时|尽快)"),
            Pattern.compile("凭\\s*([A-Za-z0-9\\-#]{4,10})(?=[，,。！!\\s]|$)")
        )

        for (pattern in pingPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val candidate = cleanCode(matcher.group(1) ?: "")
                if (candidate.isNotBlank() && candidate != trackingNumber && !isDateOrTimeOrPhone(candidate, text)) {
                    return normalizeCode(candidate)
                }
            }
        }

        // Priority 3: Multi-segment Shelf format (3 segments: rack-layer-item e.g. 3-2-104, 08-2-4019, A-12-8)
        val shelf3Pattern = Pattern.compile("(?<!\\d)(?:[A-Za-z]|\\d{1,3})-\\d{1,3}-\\d{1,5}(?!\\d)")
        val s3Matcher = shelf3Pattern.matcher(text)
        while (s3Matcher.find()) {
            val candidate = cleanCode(s3Matcher.group(0) ?: "")
            if (candidate.isNotBlank() && candidate != trackingNumber && !isDateOrTimeOrPhone(candidate, text)) {
                return normalizeCode(candidate)
            }
        }

        // Priority 4: Two-segment Shelf format (e.g. A-102, B-201, 1-201)
        val letterShelfPattern = Pattern.compile("(?<![A-Za-z0-9])([A-Za-z]\\d{0,2}-\\d{1,5})(?![A-Za-z0-9])")
        val lsMatcher = letterShelfPattern.matcher(text)
        if (lsMatcher.find()) {
            val candidate = cleanCode(lsMatcher.group(1) ?: "")
            if (candidate.isNotBlank() && candidate != trackingNumber) {
                return normalizeCode(candidate)
            }
        }

        val numShelfPattern = Pattern.compile("(?<![A-Za-z0-9])(\\d{1,3}-\\d{1,5})(?![A-Za-z0-9])")
        val nsMatcher = numShelfPattern.matcher(text)
        while (nsMatcher.find()) {
            val candidate = cleanCode(nsMatcher.group(1) ?: "")
            if (candidate.isNotBlank() && candidate != trackingNumber && !isDateOrTimeOrPhone(candidate, text)) {
                return normalizeCode(candidate)
            }
        }

        // Priority 5: Letter + Number without hyphen (e.g. A802, B1204, C302, D105)
        val rackPattern = Pattern.compile("(?<![A-Za-z0-9])([A-Za-z]\\d{2,5})(?![A-Za-z0-9])")
        val rackMatcher = rackPattern.matcher(text)
        while (rackMatcher.find()) {
            val candidate = cleanCode(rackMatcher.group(1) ?: "")
            if (candidate.isNotBlank() && candidate != trackingNumber && !isDateOrTimeOrPhone(candidate, text)) {
                val upper = candidate.uppercase()
                if (!upper.startsWith("SF") && !upper.startsWith("JD") && !upper.startsWith("YT") && !upper.startsWith("JT")) {
                    return normalizeCode(candidate)
                }
            }
        }

        // Priority 6: Bracketed or quoted 4-8 digit codes in parcel context
        val bracketCodePattern = Pattern.compile("[【“\"\\[]([A-Za-z0-9\\-]{4,8})[】”\"\\]]")
        val bcMatcher = bracketCodePattern.matcher(text)
        while (bcMatcher.find()) {
            val candidate = cleanCode(bcMatcher.group(1) ?: "")
            if (candidate.isNotBlank() && candidate != trackingNumber && !isDateOrTimeOrPhone(candidate, text)) {
                val isBrand = COURIER_MAPPING.any { candidate.contains(it.first) }
                if (!isBrand) {
                    return normalizeCode(candidate)
                }
            }
        }

        // Priority 7: Standalone numeric sequence (4 to 8 digits) in parcel context without explicit label
        val digitPattern = Pattern.compile("(?:^|[^0-9A-Za-z])(\\d{4,8})(?:[^0-9A-Za-z]|$)")
        val digitMatcher = digitPattern.matcher(text)
        while (digitMatcher.find()) {
            val candidate = digitMatcher.group(1) ?: ""
            if (candidate.isNotBlank() && candidate != trackingNumber && !isDateOrTimeOrPhone(candidate, text)) {
                val idx = text.indexOf(candidate)
                val prefix = if (idx >= 4) text.substring(idx - 4, idx) else text.substring(0, idx)
                val suffix = if (idx + candidate.length + 4 <= text.length) {
                    text.substring(idx + candidate.length, idx + candidate.length + 4)
                } else {
                    text.substring(idx + candidate.length)
                }

                val isPhoneContext = prefix.contains("电") || prefix.contains("话") || prefix.contains("询") ||
                        prefix.contains("服") || prefix.contains("机") || prefix.contains("联系")
                val isAddressContext = suffix.contains("号") || suffix.contains("栋") || suffix.contains("楼") ||
                        suffix.contains("室") || suffix.contains("元") || suffix.contains("件")

                if (!isPhoneContext && !isAddressContext) {
                    return candidate
                }
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

    private fun normalizeCode(code: String): String {
        val cleaned = cleanCode(code)
        // Space-separated shelf code: "3 2 104" -> "3-2-104"
        if (cleaned.matches(Regex("^[A-Za-z0-9]{1,3}\\s+\\d{1,3}\\s+\\d{1,5}$"))) {
            return cleaned.replace(Regex("\\s+"), "-")
        }
        // Dot-separated shelf code: "3.2.104" -> "3-2-104"
        if (cleaned.matches(Regex("^[A-Za-z0-9]{1,3}\\.\\d{1,3}\\.\\d{1,5}$"))) {
            return cleaned.replace(".", "-")
        }
        return cleaned
    }

    private fun cleanCode(code: String): String {
        return code.trim(
            '【', '】', '[', ']', '“', '”', '"', '\'', '（', '）', '(', ')',
            '<', '>', '《', '》', '，', '。', '！', '!', ' ', ';', '；', '#', ',', '.'
        )
    }
}
