package com.example

import com.example.parser.ParcelParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ParcelParserTest {

    @Test
    fun `test cainiao sms parsing`() {
        val sms = "【菜鸟驿站】凭 3-2-104 到 阳光小区西门菜鸟驿站 2号货架取件，运单号 7731234567890，请及时取件。"
        val parsed = ParcelParser.parse(sms)
        assertTrue(parsed.isValid)
        assertEquals("3-2-104", parsed.pickupCode)
        assertEquals("7731234567890", parsed.trackingNumber)
        assertEquals("菜鸟驿站", parsed.courierName)
    }

    @Test
    fun `test fcbox sms parsing`() {
        val sms = "【丰巢】您的快件已暂存至 时代广场1号丰巢快递柜，取件码：849201，运单号：SF1492039102，客服电话95338"
        val parsed = ParcelParser.parse(sms)
        assertTrue(parsed.isValid)
        assertEquals("849201", parsed.pickupCode)
        assertEquals("SF1492039102", parsed.trackingNumber)
        assertEquals("丰巢快递柜", parsed.courierName)
    }

    @Test
    fun `test tuxi sms parsing`() {
        val sms = "【兔喜生活】您的中通包裹已到站，凭取件码 A-12-8 到中通超市取件，单号 75423423423，请尽快提取"
        val parsed = ParcelParser.parse(sms)
        assertTrue(parsed.isValid)
        assertEquals("A-12-8", parsed.pickupCode)
        assertEquals("75423423423", parsed.trackingNumber)
        assertEquals("兔喜生活", parsed.courierName)
    }

    @Test
    fun `test sf express sms parsing`() {
        val sms = "【顺丰速运】您的包裹已到达自提点，取件码为982143，运单号SF98765432100"
        val parsed = ParcelParser.parse(sms)
        assertTrue(parsed.isValid)
        assertEquals("982143", parsed.pickupCode)
        assertEquals("SF98765432100", parsed.trackingNumber)
        assertEquals("顺丰速运", parsed.courierName)
    }

    @Test
    fun `test non parcel message returns false`() {
        val normalText = "明天下午两点在星巴克开会，请带上笔记本电脑。"
        assertFalse(ParcelParser.isParcelRelated(normalText))
    }
}
