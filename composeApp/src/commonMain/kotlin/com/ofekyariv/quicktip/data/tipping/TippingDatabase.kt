package com.ofekyariv.quicktip.data.tipping

import com.ofekyariv.quicktip.data.models.CountryTipInfo
import com.ofekyariv.quicktip.data.models.ServiceType
import com.ofekyariv.quicktip.data.models.TippingCulture

/**
 * Database of tipping norms and practices for ~50 countries worldwide.
 * Data sourced from cultural research and travel guides (2024-2025).
 */
object TippingDatabase {
    
    private val countries = listOf(
        // North America
        CountryTipInfo(
            countryCode = "US",
            countryName = "United States",
            flag = "🇺🇸",
            culture = TippingCulture.EXPECTED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 18..20,
                ServiceType.TAXI to 15..18,
                ServiceType.SALON to 18..20,
                ServiceType.HOTEL to 15..20,
                ServiceType.DELIVERY to 15..20,
                ServiceType.COUNTER to 0..15
            ),
            notes = "Tipping is essential; service workers rely on tips. 15% minimum, 20% standard for good service."
        ),
        CountryTipInfo(
            countryCode = "CA",
            countryName = "Canada",
            flag = "🇨🇦",
            culture = TippingCulture.EXPECTED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 15..20,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 15..20,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 0..10
            ),
            notes = "Similar to US customs. 15-20% is standard in restaurants and for personal services."
        ),
        CountryTipInfo(
            countryCode = "MX",
            countryName = "Mexico",
            flag = "🇲🇽",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10-15% is customary. Some tourist areas expect higher tips similar to US standards."
        ),
        
        // Western Europe
        CountryTipInfo(
            countryCode = "GB",
            countryName = "United Kingdom",
            flag = "🇬🇧",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..12,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Service charge often included. If not, 10-12% is appreciated. Check your bill first."
        ),
        CountryTipInfo(
            countryCode = "FR",
            countryName = "France",
            flag = "🇫🇷",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Service charge (15%) is included by law. Extra tip of 5-10% for exceptional service only."
        ),
        CountryTipInfo(
            countryCode = "DE",
            countryName = "Germany",
            flag = "🇩🇪",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Round up to next euro or add 5-10%. Say total amount when paying to include tip."
        ),
        CountryTipInfo(
            countryCode = "IT",
            countryName = "Italy",
            flag = "🇮🇹",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Coperto (cover charge) usually included. Small tip appreciated but not expected."
        ),
        CountryTipInfo(
            countryCode = "ES",
            countryName = "Spain",
            flag = "🇪🇸",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Not mandatory. Round up or leave small change. 5-10% for excellent service."
        ),
        CountryTipInfo(
            countryCode = "NL",
            countryName = "Netherlands",
            flag = "🇳🇱",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Service charge included but 5-10% extra is common for good service."
        ),
        CountryTipInfo(
            countryCode = "BE",
            countryName = "Belgium",
            flag = "🇧🇪",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Service included by law. Tips are appreciated but not expected."
        ),
        CountryTipInfo(
            countryCode = "CH",
            countryName = "Switzerland",
            flag = "🇨🇭",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Service charge included. Rounding up or 5-10% for exceptional service is appreciated."
        ),
        CountryTipInfo(
            countryCode = "AT",
            countryName = "Austria",
            flag = "🇦🇹",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Round up or add 5-10%. Tell waiter the total amount when paying."
        ),
        CountryTipInfo(
            countryCode = "SE",
            countryName = "Sweden",
            flag = "🇸🇪",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Service included. Tips appreciated but not expected. 5-10% for great service."
        ),
        CountryTipInfo(
            countryCode = "NO",
            countryName = "Norway",
            flag = "🇳🇴",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Service charge included. Tipping not required but 5-10% appreciated for good service."
        ),
        CountryTipInfo(
            countryCode = "DK",
            countryName = "Denmark",
            flag = "🇩🇰",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Service included by law. Tipping is a bonus for exceptional service only."
        ),
        
        // Eastern Europe
        CountryTipInfo(
            countryCode = "PL",
            countryName = "Poland",
            flag = "🇵🇱",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "10-15% is becoming standard, especially in cities. Round up for taxis."
        ),
        CountryTipInfo(
            countryCode = "CZ",
            countryName = "Czech Republic",
            flag = "🇨🇿",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "10% is common. Round up to nearest whole number. Pay directly to server."
        ),
        CountryTipInfo(
            countryCode = "RU",
            countryName = "Russia",
            flag = "🇷🇺",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "10-15% is increasingly expected in major cities and tourist areas."
        ),
        
        // Asia-Pacific
        CountryTipInfo(
            countryCode = "JP",
            countryName = "Japan",
            flag = "🇯🇵",
            culture = TippingCulture.RUDE,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 0..0,
                ServiceType.TAXI to 0..0,
                ServiceType.SALON to 0..0,
                ServiceType.HOTEL to 0..0,
                ServiceType.DELIVERY to 0..0,
                ServiceType.COUNTER to 0..0
            ),
            notes = "Tipping is considered offensive. Excellent service is standard and included in the price."
        ),
        CountryTipInfo(
            countryCode = "KR",
            countryName = "South Korea",
            flag = "🇰🇷",
            culture = TippingCulture.RUDE,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 0..0,
                ServiceType.TAXI to 0..0,
                ServiceType.SALON to 0..0,
                ServiceType.HOTEL to 0..0,
                ServiceType.DELIVERY to 0..0,
                ServiceType.COUNTER to 0..0
            ),
            notes = "Tipping is not practiced and can be seen as insulting. Service charge included."
        ),
        CountryTipInfo(
            countryCode = "CN",
            countryName = "China",
            flag = "🇨🇳",
            culture = TippingCulture.UNCOMMON,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 0..5,
                ServiceType.TAXI to 0..5,
                ServiceType.SALON to 0..5,
                ServiceType.HOTEL to 0..5,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..0
            ),
            notes = "Generally not expected except in Western hotels and upscale restaurants catering to tourists."
        ),
        CountryTipInfo(
            countryCode = "HK",
            countryName = "Hong Kong",
            flag = "🇭🇰",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "10% service charge usually included. Additional tip not required but appreciated."
        ),
        CountryTipInfo(
            countryCode = "SG",
            countryName = "Singapore",
            flag = "🇸🇬",
            culture = TippingCulture.UNCOMMON,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 0..10,
                ServiceType.TAXI to 0..5,
                ServiceType.SALON to 0..10,
                ServiceType.HOTEL to 0..10,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..0
            ),
            notes = "10% service charge included at most places. Additional tipping not expected."
        ),
        CountryTipInfo(
            countryCode = "TH",
            countryName = "Thailand",
            flag = "🇹🇭",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Not traditional but appreciated. 10% in upscale places, round up in casual spots."
        ),
        CountryTipInfo(
            countryCode = "VN",
            countryName = "Vietnam",
            flag = "🇻🇳",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "Not expected but increasingly common in tourist areas. 5-10% appreciated."
        ),
        CountryTipInfo(
            countryCode = "IN",
            countryName = "India",
            flag = "🇮🇳",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 0..5
            ),
            notes = "10% service charge sometimes included. Additional 5-10% tip is customary."
        ),
        CountryTipInfo(
            countryCode = "AU",
            countryName = "Australia",
            flag = "🇦🇺",
            culture = TippingCulture.UNCOMMON,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 0..10,
                ServiceType.TAXI to 0..5,
                ServiceType.SALON to 0..10,
                ServiceType.HOTEL to 0..10,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..0
            ),
            notes = "Not expected due to high minimum wages. 10% for exceptional service is appreciated."
        ),
        CountryTipInfo(
            countryCode = "NZ",
            countryName = "New Zealand",
            flag = "🇳🇿",
            culture = TippingCulture.UNCOMMON,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 0..10,
                ServiceType.TAXI to 0..5,
                ServiceType.SALON to 0..10,
                ServiceType.HOTEL to 0..10,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..0
            ),
            notes = "Not a tipping culture. 10% for outstanding service is appreciated but rare."
        ),
        
        // Middle East & Africa
        CountryTipInfo(
            countryCode = "IL",
            countryName = "Israel",
            flag = "🇮🇱",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 0..5
            ),
            notes = "10-15% is standard. Often expected in restaurants and for personal services."
        ),
        CountryTipInfo(
            countryCode = "AE",
            countryName = "United Arab Emirates",
            flag = "🇦🇪",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10-15% customary. Service charge often included but additional tip expected."
        ),
        CountryTipInfo(
            countryCode = "TR",
            countryName = "Turkey",
            flag = "🇹🇷",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10-15% is standard. Round up taxi fares. Tips appreciated in tourist areas."
        ),
        CountryTipInfo(
            countryCode = "EG",
            countryName = "Egypt",
            flag = "🇪🇬",
            culture = TippingCulture.EXPECTED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "Tipping (baksheesh) is ingrained in culture. 10-15% expected for most services."
        ),
        CountryTipInfo(
            countryCode = "ZA",
            countryName = "South Africa",
            flag = "🇿🇦",
            culture = TippingCulture.EXPECTED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10-15% is standard and expected. Service workers often depend on tips."
        ),
        CountryTipInfo(
            countryCode = "MA",
            countryName = "Morocco",
            flag = "🇲🇦",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "Small tips appreciated everywhere. 10% in restaurants, round up for taxis."
        ),
        
        // Latin America
        CountryTipInfo(
            countryCode = "BR",
            countryName = "Brazil",
            flag = "🇧🇷",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "10% service charge (taxa de serviço) usually included. Additional tip optional."
        ),
        CountryTipInfo(
            countryCode = "AR",
            countryName = "Argentina",
            flag = "🇦🇷",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10% is standard in restaurants. Cash preferred as credit card tips may not reach staff."
        ),
        CountryTipInfo(
            countryCode = "CL",
            countryName = "Chile",
            flag = "🇨🇱",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "10% is customary in restaurants. Not mandatory but widely expected."
        ),
        CountryTipInfo(
            countryCode = "CO",
            countryName = "Colombia",
            flag = "🇨🇴",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10% suggested tip often appears on bill. Additional tip for excellent service."
        ),
        CountryTipInfo(
            countryCode = "PE",
            countryName = "Peru",
            flag = "🇵🇪",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10% is standard. Check if service charge is included before adding tip."
        ),
        
        // Additional European Countries
        CountryTipInfo(
            countryCode = "PT",
            countryName = "Portugal",
            flag = "🇵🇹",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "5-10% appreciated but not mandatory. Round up or leave small change."
        ),
        CountryTipInfo(
            countryCode = "GR",
            countryName = "Greece",
            flag = "🇬🇷",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "5-10% is appreciated. Round up taxi fares. Leave cash on table."
        ),
        CountryTipInfo(
            countryCode = "IE",
            countryName = "Ireland",
            flag = "🇮🇪",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 0..5
            ),
            notes = "10-15% is customary for good service. Not mandatory but widely practiced."
        ),
        CountryTipInfo(
            countryCode = "FI",
            countryName = "Finland",
            flag = "🇫🇮",
            culture = TippingCulture.OPTIONAL,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..0
            ),
            notes = "Service included. Tipping not expected but 5-10% for great service is nice."
        ),
        
        // Additional Asian Countries
        CountryTipInfo(
            countryCode = "MY",
            countryName = "Malaysia",
            flag = "🇲🇾",
            culture = TippingCulture.UNCOMMON,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 0..10,
                ServiceType.TAXI to 0..5,
                ServiceType.SALON to 0..10,
                ServiceType.HOTEL to 0..10,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..0
            ),
            notes = "10% service charge often included. Additional tipping not expected."
        ),
        CountryTipInfo(
            countryCode = "PH",
            countryName = "Philippines",
            flag = "🇵🇭",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10% service charge often added. Additional 10-15% tip is appreciated."
        ),
        CountryTipInfo(
            countryCode = "ID",
            countryName = "Indonesia",
            flag = "🇮🇩",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 5..10,
                ServiceType.TAXI to 5..10,
                ServiceType.SALON to 5..10,
                ServiceType.HOTEL to 5..10,
                ServiceType.DELIVERY to 5..10,
                ServiceType.COUNTER to 0..5
            ),
            notes = "5-10% appreciated in tourist areas. Often service charge is included."
        ),
        CountryTipInfo(
            countryCode = "TW",
            countryName = "Taiwan",
            flag = "🇹🇼",
            culture = TippingCulture.UNCOMMON,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 0..0,
                ServiceType.TAXI to 0..5,
                ServiceType.SALON to 0..5,
                ServiceType.HOTEL to 0..10,
                ServiceType.DELIVERY to 0..5,
                ServiceType.COUNTER to 0..0
            ),
            notes = "Generally not expected. Service charge included at upscale venues."
        ),
        
        // Additional Middle Eastern Countries
        CountryTipInfo(
            countryCode = "SA",
            countryName = "Saudi Arabia",
            flag = "🇸🇦",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10-15% customary. Service charge often included but additional tip expected."
        ),
        CountryTipInfo(
            countryCode = "QA",
            countryName = "Qatar",
            flag = "🇶🇦",
            culture = TippingCulture.APPRECIATED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..15,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 10..15,
                ServiceType.HOTEL to 10..15,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "Similar to UAE. 10-15% standard, often on top of service charge."
        ),
        
        // Caribbean
        CountryTipInfo(
            countryCode = "JM",
            countryName = "Jamaica",
            flag = "🇯🇲",
            culture = TippingCulture.EXPECTED,
            serviceTips = mapOf(
                ServiceType.RESTAURANT to 10..18,
                ServiceType.TAXI to 10..15,
                ServiceType.SALON to 15..20,
                ServiceType.HOTEL to 15..20,
                ServiceType.DELIVERY to 10..15,
                ServiceType.COUNTER to 5..10
            ),
            notes = "10-18% expected. Some resorts include gratuity. Check before tipping extra."
        )
    )
    
    /**
     * Gets tipping information for a specific country by its ISO code.
     * 
     * @param code The 2-letter ISO 3166-1 alpha-2 country code (e.g., "US", "IL", "JP")
     * @return CountryTipInfo if found, null otherwise
     */
    fun getByCountryCode(code: String): CountryTipInfo? {
        return countries.find { it.countryCode.equals(code, ignoreCase = true) }
    }
    
    /**
     * Gets tipping information based on device locale.
     * Falls back to United States if locale country is not in database.
     * 
     * @param localeCountryCode Optional country code from locale (defaults to "US")
     * @return CountryTipInfo for the locale country or US as fallback
     */
    fun getByLocale(localeCountryCode: String = "US"): CountryTipInfo {
        return getByCountryCode(localeCountryCode) ?: getByCountryCode("US")!!
    }
    
    /**
     * Gets all countries in the database.
     * 
     * @return List of all CountryTipInfo entries
     */
    fun getAllCountries(): List<CountryTipInfo> {
        return countries
    }
    
    /**
     * Gets all countries sorted alphabetically by name.
     */
    fun getAllCountriesSorted(): List<CountryTipInfo> {
        return countries.sortedBy { it.countryName }
    }
    
    /**
     * Gets countries by tipping culture.
     */
    fun getCountriesByCulture(culture: TippingCulture): List<CountryTipInfo> {
        return countries.filter { it.culture == culture }
    }
    
    /**
     * Searches countries by name (case-insensitive partial match).
     */
    fun searchByName(query: String): List<CountryTipInfo> {
        return countries.filter { 
            it.countryName.contains(query, ignoreCase = true) ||
            it.countryCode.equals(query, ignoreCase = true)
        }
    }
}
