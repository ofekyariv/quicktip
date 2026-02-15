package com.ofekyariv.quicktip

import com.ofekyariv.quicktip.data.TipCalculator
import com.ofekyariv.quicktip.data.models.CurrencyInfo
import com.ofekyariv.quicktip.data.models.RoundingMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for TipCalculator core logic.
 * Tests all calculation functions and rounding modes.
 */
class TipCalculatorTest {
    
    private val usdCurrency = CurrencyInfo("USD", "$", "US Dollar", 2, "North America")
    private val jpyCurrency = CurrencyInfo("JPY", "¥", "Japanese Yen", 0, "Asia-Pacific")
    
    @Test
    fun testCalculateTip_basic() {
        val tip = TipCalculator.calculateTip(100.0, 18.0)
        assertEquals(18.0, tip, 0.01)
    }
    
    @Test
    fun testCalculateTip_zeroPercent() {
        val tip = TipCalculator.calculateTip(100.0, 0.0)
        assertEquals(0.0, tip, 0.01)
    }
    
    @Test
    fun testCalculateTip_decimal() {
        val tip = TipCalculator.calculateTip(50.75, 20.0)
        assertEquals(10.15, tip, 0.01)
    }
    
    @Test
    fun testCalculateTotal_basic() {
        val total = TipCalculator.calculateTotal(100.0, 18.0)
        assertEquals(118.0, total, 0.01)
    }
    
    @Test
    fun testCalculateSplit_twopeople() {
        val perPerson = TipCalculator.calculateSplit(100.0, 2)
        assertEquals(50.0, perPerson, 0.01)
    }
    
    @Test
    fun testCalculateSplit_oddAmount() {
        val perPerson = TipCalculator.calculateSplit(100.0, 3)
        assertEquals(33.333, perPerson, 0.01)
    }
    
    @Test
    fun testCalculateSplit_onePerson() {
        val perPerson = TipCalculator.calculateSplit(100.0, 1)
        assertEquals(100.0, perPerson, 0.01)
    }
    
    @Test
    fun testRoundingMode_noRounding() {
        val amount = 23.47
        val rounded = TipCalculator.applyRounding(amount, RoundingMode.NO_ROUNDING, 2)
        assertEquals(23.47, rounded, 0.01)
    }
    
    @Test
    fun testRoundingMode_roundUpWhole() {
        val amount = 23.47
        val rounded = TipCalculator.applyRounding(amount, RoundingMode.ROUND_UP_WHOLE, 2)
        assertEquals(24.0, rounded, 0.01)
    }
    
    @Test
    fun testRoundingMode_roundNearestHalf_up() {
        val amount = 23.47
        val rounded = TipCalculator.applyRounding(amount, RoundingMode.ROUND_NEAREST_HALF, 2)
        assertEquals(23.5, rounded, 0.01)
    }
    
    @Test
    fun testRoundingMode_roundNearestHalf_down() {
        val amount = 23.23
        val rounded = TipCalculator.applyRounding(amount, RoundingMode.ROUND_NEAREST_HALF, 2)
        assertEquals(23.0, rounded, 0.01)
    }
    
    @Test
    fun testRoundingMode_roundNearestTenth() {
        val amount = 127.34
        val rounded = TipCalculator.applyRounding(amount, RoundingMode.ROUND_NEAREST_TENTH, 2)
        assertEquals(127.3, rounded, 0.01)
    }
    
    @Test
    fun testRoundingMode_roundNearestFive() {
        val amount = 23.47
        val rounded = TipCalculator.applyRounding(amount, RoundingMode.ROUND_NEAREST_FIVE, 2)
        assertEquals(25.0, rounded, 0.01)
    }
    
    @Test
    fun testCurrencyDecimalHandling_usd() {
        val calculation = TipCalculator.calculate(
            billAmount = 50.0,
            tipPercentage = 18.0,
            numPeople = 1,
            currencyInfo = usdCurrency,
            roundingMode = RoundingMode.NO_ROUNDING
        )
        
        assertEquals(50.0, calculation.billAmount, 0.01)
        assertEquals(9.0, calculation.tipAmount, 0.01)
        assertEquals(59.0, calculation.totalAmount, 0.01)
    }
    
    @Test
    fun testCurrencyDecimalHandling_jpy() {
        val calculation = TipCalculator.calculate(
            billAmount = 5000.0,
            tipPercentage = 10.0,
            numPeople = 1,
            currencyInfo = jpyCurrency,
            roundingMode = RoundingMode.NO_ROUNDING
        )
        
        assertEquals(5000.0, calculation.billAmount, 0.01)
        assertEquals(500.0, calculation.tipAmount, 0.01)
        assertEquals(5500.0, calculation.totalAmount, 0.01)
    }
    
    @Test
    fun testEdgeCase_zeroAmount() {
        val calculation = TipCalculator.calculate(
            billAmount = 0.0,
            tipPercentage = 18.0,
            numPeople = 1,
            currencyInfo = usdCurrency,
            roundingMode = RoundingMode.NO_ROUNDING
        )
        
        assertEquals(0.0, calculation.totalAmount, 0.01)
    }
    
    @Test
    fun testEdgeCase_largeAmount() {
        val calculation = TipCalculator.calculate(
            billAmount = 10000.0,
            tipPercentage = 20.0,
            numPeople = 10,
            currencyInfo = usdCurrency,
            roundingMode = RoundingMode.NO_ROUNDING
        )
        
        assertEquals(12000.0, calculation.totalAmount, 0.01)
        assertEquals(1200.0, calculation.perPersonAmount, 0.01)
    }
    
    @Test
    fun testCompleteCalculation_withRounding() {
        val calculation = TipCalculator.calculate(
            billAmount = 47.83,
            tipPercentage = 18.0,
            numPeople = 2,
            currencyInfo = usdCurrency,
            roundingMode = RoundingMode.ROUND_UP_WHOLE
        )
        
        // 47.83 * 1.18 = 56.4394 → round up to 57
        assertEquals(57.0, calculation.totalAmount, 0.01)
        // 57 / 2 = 28.50
        assertEquals(28.50, calculation.perPersonAmount, 0.01)
    }
    
    @Test
    fun testCompleteCalculation_splitThree() {
        val calculation = TipCalculator.calculate(
            billAmount = 100.0,
            tipPercentage = 20.0,
            numPeople = 3,
            currencyInfo = usdCurrency,
            roundingMode = RoundingMode.NO_ROUNDING
        )
        
        assertEquals(120.0, calculation.totalAmount, 0.01)
        assertEquals(40.0, calculation.perPersonAmount, 0.01)
    }
    
    @Test
    fun testRoundToPrecision_twoDecimals() {
        val rounded = TipCalculator.roundToPrecision(23.456, 2)
        assertEquals(23.46, rounded, 0.01)
    }
    
    @Test
    fun testRoundToPrecision_zeroDecimals() {
        val rounded = TipCalculator.roundToPrecision(23.456, 0)
        assertEquals(23.0, rounded, 0.01)
    }
}
