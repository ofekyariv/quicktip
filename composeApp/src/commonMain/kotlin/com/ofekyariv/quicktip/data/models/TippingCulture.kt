package com.ofekyariv.quicktip.data.models

/**
 * Cultural norms around tipping in different countries.
 */
enum class TippingCulture {
    /** Tipping is expected and part of service workers' income (e.g., USA) */
    EXPECTED,
    
    /** Tipping is appreciated but not mandatory (e.g., Canada, some EU countries) */
    APPRECIATED,
    
    /** Tipping is optional, service charge often included (e.g., UK, France) */
    OPTIONAL,
    
    /** Tipping is uncommon but not offensive (e.g., Australia, New Zealand) */
    UNCOMMON,
    
    /** Tipping is considered rude or insulting (e.g., Japan, South Korea) */
    RUDE;
    
    /**
     * Gets a human-readable description.
     */
    fun getDescription(): String = when (this) {
        EXPECTED -> "Tipping is expected and customary"
        APPRECIATED -> "Tipping is appreciated but optional"
        OPTIONAL -> "Tipping is optional, service charge often included"
        UNCOMMON -> "Tipping is uncommon but acceptable"
        RUDE -> "Tipping is considered rude or offensive"
    }
}
