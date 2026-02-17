package com.ofekyariv.quicktip.data.tipping

import com.ofekyariv.quicktip.data.models.ServiceType
import com.ofekyariv.quicktip.data.models.TippingCulture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for TippingDatabase.
 */
class TippingDatabaseTest {
    
    @Test
    fun testGetByCountryCode_US() {
        val usInfo = TippingDatabase.getByCountryCode("US")
        
        assertNotNull(usInfo, "US should be in database")
        assertEquals("US", usInfo.countryCode)
        assertEquals("United States", usInfo.countryName)
        assertEquals("🇺🇸", usInfo.flag)
        assertEquals(TippingCulture.EXPECTED, usInfo.culture)
        
        // Check restaurant tipping range
        val restaurantRange = usInfo.getTipRange(ServiceType.RESTAURANT)
        assertNotNull(restaurantRange, "US should have restaurant tipping guidance")
        assertTrue(restaurantRange.first >= 15, "US restaurant min tip should be at least 15%")
        assertTrue(restaurantRange.last >= 18, "US restaurant max tip should be at least 18%")
    }
    
    @Test
    fun testGetByCountryCode_Japan() {
        val jpInfo = TippingDatabase.getByCountryCode("JP")
        
        assertNotNull(jpInfo, "Japan should be in database")
        assertEquals("JP", jpInfo.countryCode)
        assertEquals("Japan", jpInfo.countryName)
        assertEquals("🇯🇵", jpInfo.flag)
        assertEquals(TippingCulture.RUDE, jpInfo.culture)
        
        // Check that all service types have 0% tipping
        ServiceType.entries.forEach { serviceType ->
            val range = jpInfo.getTipRange(serviceType)
            assertNotNull(range, "Japan should have guidance for $serviceType")
            assertEquals(0, range.first, "Japan ${serviceType.label} min tip should be 0%")
            assertEquals(0, range.last, "Japan ${serviceType.label} max tip should be 0%")
        }
    }
    
    @Test
    fun testGetByCountryCode_Israel() {
        val ilInfo = TippingDatabase.getByCountryCode("IL")
        
        assertNotNull(ilInfo, "Israel should be in database")
        assertEquals("IL", ilInfo.countryCode)
        assertEquals("Israel", ilInfo.countryName)
        assertEquals("🇮🇱", ilInfo.flag)
        assertEquals(TippingCulture.APPRECIATED, ilInfo.culture)
        
        // Check restaurant tipping range (should be 10-15%)
        val restaurantRange = ilInfo.getTipRange(ServiceType.RESTAURANT)
        assertNotNull(restaurantRange, "Israel should have restaurant tipping guidance")
        assertEquals(10, restaurantRange.first)
        assertEquals(15, restaurantRange.last)
    }
    
    @Test
    fun testGetByCountryCode_CaseInsensitive() {
        val usLower = TippingDatabase.getByCountryCode("us")
        val usUpper = TippingDatabase.getByCountryCode("US")
        val usMixed = TippingDatabase.getByCountryCode("Us")
        
        assertNotNull(usLower)
        assertNotNull(usUpper)
        assertNotNull(usMixed)
        assertEquals(usLower.countryCode, usUpper.countryCode)
        assertEquals(usUpper.countryCode, usMixed.countryCode)
    }
    
    @Test
    fun testGetByCountryCode_NotFound() {
        val unknownCountry = TippingDatabase.getByCountryCode("ZZ")
        assertNull(unknownCountry, "Unknown country code should return null")
    }
    
    @Test
    fun testGetByLocale_Default() {
        val defaultInfo = TippingDatabase.getByLocale()
        
        assertNotNull(defaultInfo, "Default locale should return a result")
        assertEquals("US", defaultInfo.countryCode, "Default should be US")
    }
    
    @Test
    fun testGetByLocale_ValidCountry() {
        val ukInfo = TippingDatabase.getByLocale("GB")
        
        assertNotNull(ukInfo)
        assertEquals("GB", ukInfo.countryCode)
        assertEquals("United Kingdom", ukInfo.countryName)
    }
    
    @Test
    fun testGetByLocale_UnknownFallback() {
        val unknownLocale = TippingDatabase.getByLocale("ZZ")
        
        assertNotNull(unknownLocale, "Unknown locale should fallback to US")
        assertEquals("US", unknownLocale.countryCode, "Fallback should be US")
    }
    
    @Test
    fun testGetAllCountries() {
        val allCountries = TippingDatabase.getAllCountries()
        
        assertTrue(allCountries.isNotEmpty(), "Database should contain countries")
        assertTrue(allCountries.size >= 50, "Database should contain at least 50 countries")
        
        // Verify no duplicate country codes
        val countryCodes = allCountries.map { it.countryCode }
        val uniqueCodes = countryCodes.toSet()
        assertEquals(
            countryCodes.size,
            uniqueCodes.size,
            "All country codes should be unique"
        )
    }
    
    @Test
    fun testGetAllCountriesSorted() {
        val sortedCountries = TippingDatabase.getAllCountriesSorted()
        
        assertTrue(sortedCountries.isNotEmpty())
        
        // Verify alphabetical order
        for (i in 0 until sortedCountries.size - 1) {
            assertTrue(
                sortedCountries[i].countryName <= sortedCountries[i + 1].countryName,
                "Countries should be sorted alphabetically by name"
            )
        }
    }
    
    @Test
    fun testGetCountriesByCulture() {
        val expectedCulture = TippingDatabase.getCountriesByCulture(TippingCulture.EXPECTED)
        val rudeCulture = TippingDatabase.getCountriesByCulture(TippingCulture.RUDE)
        
        assertTrue(expectedCulture.isNotEmpty(), "Should have countries with EXPECTED culture")
        assertTrue(rudeCulture.isNotEmpty(), "Should have countries with RUDE culture")
        
        // Verify all returned countries have the correct culture
        expectedCulture.forEach {
            assertEquals(TippingCulture.EXPECTED, it.culture)
        }
        rudeCulture.forEach {
            assertEquals(TippingCulture.RUDE, it.culture)
        }
        
        // Verify specific countries
        assertTrue(expectedCulture.any { it.countryCode == "US" }, "US should be EXPECTED")
        assertTrue(rudeCulture.any { it.countryCode == "JP" }, "Japan should be RUDE")
    }
    
    @Test
    fun testSearchByName() {
        val searchResults = TippingDatabase.searchByName("United")
        
        assertTrue(searchResults.isNotEmpty(), "Search for 'United' should find results")
        assertTrue(
            searchResults.any { it.countryName.contains("United") },
            "Results should contain 'United' in name"
        )
        
        // Search by country code
        val usSearch = TippingDatabase.searchByName("US")
        assertTrue(usSearch.isNotEmpty(), "Search by code should work")
        assertTrue(usSearch.any { it.countryCode == "US" })
    }
    
    @Test
    fun testSearchByName_CaseInsensitive() {
        val lowerSearch = TippingDatabase.searchByName("japan")
        val upperSearch = TippingDatabase.searchByName("JAPAN")
        val mixedSearch = TippingDatabase.searchByName("Japan")
        
        assertEquals(lowerSearch.size, upperSearch.size)
        assertEquals(upperSearch.size, mixedSearch.size)
        assertTrue(lowerSearch.any { it.countryCode == "JP" })
    }
    
    @Test
    fun testCountryTipInfo_GetSuggestedTip() {
        val usInfo = TippingDatabase.getByCountryCode("US")!!
        val suggestedTip = usInfo.getSuggestedTip(ServiceType.RESTAURANT)
        
        assertNotNull(suggestedTip, "US should have suggested restaurant tip")
        assertTrue(suggestedTip >= 15, "Suggested tip should be reasonable")
        
        // Suggested tip should be midpoint of range
        val range = usInfo.getTipRange(ServiceType.RESTAURANT)!!
        assertEquals((range.first + range.last) / 2, suggestedTip)
    }
    
    @Test
    fun testCountryTipInfo_FormatTipRange() {
        val usInfo = TippingDatabase.getByCountryCode("US")!!
        val formatted = usInfo.formatTipRange(ServiceType.RESTAURANT)
        
        assertNotNull(formatted)
        assertTrue(formatted.contains("%"), "Formatted range should include %")
        assertTrue(formatted.contains("-") || formatted.matches(Regex("\\d+%")), 
            "Should be range (e.g., '15-20%') or single value (e.g., '10%')")
    }
    
    @Test
    fun testCountryTipInfo_GetDisplayName() {
        val usInfo = TippingDatabase.getByCountryCode("US")!!
        val displayName = usInfo.getDisplayName()
        
        assertTrue(displayName.contains("🇺🇸"), "Display name should include flag")
        assertTrue(displayName.contains("United States"), "Display name should include country name")
    }
    
    @Test
    fun testDatabaseCompleteness() {
        val allCountries = TippingDatabase.getAllCountries()
        
        // Verify all countries have all required fields
        allCountries.forEach { country ->
            assertTrue(country.countryCode.isNotEmpty(), "${country.countryName} should have country code")
            assertTrue(country.countryCode.length == 2, "${country.countryName} country code should be 2 chars")
            assertTrue(country.countryName.isNotEmpty(), "Country should have name")
            assertTrue(country.flag.isNotEmpty(), "${country.countryName} should have flag emoji")
            assertTrue(country.notes.isNotEmpty(), "${country.countryName} should have notes")
            assertTrue(country.serviceTips.isNotEmpty(), "${country.countryName} should have service tips")
            
            // Verify all service types are covered
            ServiceType.entries.forEach { serviceType ->
                assertNotNull(
                    country.getTipRange(serviceType),
                    "${country.countryName} should have tip range for ${serviceType.label}"
                )
            }
        }
    }
    
    @Test
    fun testSpecificCountries_SampleCheck() {
        // Verify a diverse sample of countries are in database
        val expectedCountries = listOf(
            "US", "CA", "GB", "FR", "DE", "JP", "AU", "IL", "BR", "MX",
            "IT", "ES", "CN", "IN", "ZA", "NZ", "SE", "CH", "NL", "SG"
        )
        
        expectedCountries.forEach { code ->
            assertNotNull(
                TippingDatabase.getByCountryCode(code),
                "Country $code should be in database"
            )
        }
    }
}
