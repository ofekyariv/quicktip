package com.ofekyariv.quicktip.data.models

/**
 * Types of services that typically involve tipping.
 */
enum class ServiceType(val emoji: String, val label: String) {
    /** Restaurant, cafe, bar */
    RESTAURANT("🍽️", "Restaurant"),
    
    /** Taxi, rideshare, car service */
    TAXI("🚕", "Taxi/Rideshare"),
    
    /** Hair salon, barber, spa, beauty services */
    SALON("💇", "Hair/Salon"),
    
    /** Hotel, concierge, room service */
    HOTEL("🛎️", "Hotel"),
    
    /** Food delivery, package delivery */
    DELIVERY("🚚", "Delivery"),
    
    /** Coffee shop, quick service counter */
    COUNTER("☕", "Coffee/Counter");
    
    /**
     * Gets the display name with emoji.
     */
    fun getDisplayName(): String = "$emoji $label"
}
