package com.example.data.dao

import androidx.room.*
import com.example.data.entity.CashRegisterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CashRegisterDao {
    @Query("SELECT * FROM cash_registers WHERE dateKey = :dateKey LIMIT 1")
    fun getCashRegisterByDate(dateKey: String): Flow<CashRegisterEntity?>

    @Query("SELECT * FROM cash_registers WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getCashRegisterByDateSync(dateKey: String): CashRegisterEntity?

    @Query("SELECT * FROM cash_registers ORDER BY openedAt DESC")
    fun getAllCashRegisters(): Flow<List<CashRegisterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cashRegister: CashRegisterEntity): Long

    @Update
    suspend fun update(cashRegister: CashRegisterEntity)

    @Query("UPDATE cash_registers SET countedCash = :countedCash, notes = :notes WHERE id = :id")
    suspend fun updateCashCount(id: Long, countedCash: Double, notes: String)

    @Query("UPDATE cash_registers SET suppliesAmount = suppliesAmount + :amount WHERE id = :id")
    suspend fun addSupply(id: Long, amount: Double)

    @Query("UPDATE cash_registers SET bleedingsAmount = bleedingsAmount + :amount WHERE id = :id")
    suspend fun addBleeding(id: Long, amount: Double)

    @Query("UPDATE cash_registers SET status = 'FECHADO', closedAt = :closedAt, countedCash = :countedCash, notes = :notes WHERE id = :id")
    suspend fun closeRegister(id: Long, closedAt: Long, countedCash: Double, notes: String)
}
