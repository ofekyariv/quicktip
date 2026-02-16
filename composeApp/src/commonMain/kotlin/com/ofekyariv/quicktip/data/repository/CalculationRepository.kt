package com.ofekyariv.quicktip.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ofekyariv.quicktip.data.database.QuickTipDatabase
import com.ofekyariv.quicktip.data.models.RoundingMode
import com.ofekyariv.quicktip.data.models.TipCalculation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository for managing calculation history.
 * Handles all database operations for calculations.
 */
class CalculationRepository(private val database: QuickTipDatabase) {
    
    private val queries = database.quickTipDatabaseQueries
    
    /**
     * Get all calculations as a Flow (reactive).
     */
    fun getAllCalculations(): Flow<List<TipCalculation>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { calculations ->
                calculations.map { calc ->
                    TipCalculation(
                        id = calc.id,
                        billAmount = calc.bill_amount,
                        tipPercentage = calc.tip_percentage,
                        tipAmount = calc.tip_amount,
                        totalAmount = calc.total_amount,
                        numPeople = calc.num_people.toInt(),
                        perPersonAmount = calc.per_person_amount,
                        currency = calc.currency_code,
                        roundingMode = RoundingMode.valueOf(calc.rounding_mode),
                        timestamp = calc.timestamp
                    )
                }
            }
    }
    
    /**
     * Get recent calculations with limit.
     */
    fun getRecentCalculations(limit: Int): Flow<List<TipCalculation>> {
        return queries.selectRecent(limit.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { calculations ->
                calculations.map { calc ->
                    TipCalculation(
                        id = calc.id,
                        billAmount = calc.bill_amount,
                        tipPercentage = calc.tip_percentage,
                        tipAmount = calc.tip_amount,
                        totalAmount = calc.total_amount,
                        numPeople = calc.num_people.toInt(),
                        perPersonAmount = calc.per_person_amount,
                        currency = calc.currency_code,
                        roundingMode = RoundingMode.valueOf(calc.rounding_mode),
                        timestamp = calc.timestamp
                    )
                }
            }
    }
    
    /**
     * Get total calculation count.
     */
    suspend fun getCalculationCount(): Long = withContext(Dispatchers.IO) {
        queries.countAll().executeAsOne()
    }
    
    /**
     * Save a new calculation to the database.
     */
    suspend fun saveCalculation(calculation: TipCalculation) = withContext(Dispatchers.IO) {
        queries.insertCalculation(
            bill_amount = calculation.billAmount,
            tip_percentage = calculation.tipPercentage,
            tip_amount = calculation.tipAmount,
            total_amount = calculation.totalAmount,
            num_people = calculation.numPeople.toLong(),
            per_person_amount = calculation.perPersonAmount,
            currency_code = calculation.currency,
            rounding_mode = calculation.roundingMode.name,
            timestamp = calculation.timestamp
        )
    }
    
    /**
     * Delete a calculation by ID.
     */
    suspend fun deleteCalculation(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteById(id)
    }
    
    /**
     * Clear all calculation history.
     */
    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        queries.deleteAll()
    }
    
    /**
     * Keep only the N most recent calculations (delete older ones).
     */
    suspend fun keepRecentOnly(keepCount: Int) = withContext(Dispatchers.IO) {
        queries.deleteOldest(keepCount.toLong())
    }
}
