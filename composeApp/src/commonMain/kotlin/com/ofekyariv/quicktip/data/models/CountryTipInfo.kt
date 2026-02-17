package com.ofekyariv.quicktip.data.models

/**
 * Contains tipping information for a specific country.
 */
data class CountryTipInfo(
    /** ISO 3166-1 alpha-2 country code (e.g., "US", "IL", "JP") */
    val countryCode: String,
    
    /** Full country name (e.g., "United States") */
    val countryName: String,
    
    /** Country flag emoji (e.g., "🇺🇸") */
    val flag: String,
    
    /** Overall tipping culture in this country */
    val culture: TippingCulture,
    
    /** Recommended tip percentages by service type (e.g., RESTAURANT -> 15..20) */
    val serviceTips: Map<ServiceType, IntRange>,
    
    /** Additional cultural notes or context */
    val notes: String
) {
    /**
     * Gets the recommended tip range for a specific service type.
     * Returns null if no specific guidance is available.
     */
    fun getTipRange(serviceType: ServiceType): IntRange? {
        return serviceTips[serviceType]
    }
    
    /**
     * Gets the midpoint tip percentage for a service type.
     * Returns null if no guidance is available.
     */
    fun getSuggestedTip(serviceType: ServiceType): Int? {
        val range = getTipRange(serviceType) ?: return null
        return (range.first + range.last) / 2
    }
    
    /**
     * Formats the tip range as a human-readable string (e.g., "15-20%").
     */
    fun formatTipRange(serviceType: ServiceType): String {
        val range = getTipRange(serviceType) ?: return "N/A"
        return if (range.first == range.last) {
            "${range.first}%"
        } else {
            "${range.first}-${range.last}%"
        }
    }
    
    /**
     * Gets the display name with flag.
     */
    fun getDisplayName(): String = "$flag $countryName"
}
