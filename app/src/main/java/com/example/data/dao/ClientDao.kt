package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: Long): ClientEntity?

    @Query("SELECT * FROM clients WHERE name LIKE '%' || :query || '%' OR tradeName LIKE '%' || :query || '%' OR document LIKE '%' || :query || '%'")
    fun searchClients(query: String): Flow<List<ClientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Delete
    suspend fun deleteClient(client: ClientEntity)

    @Query("UPDATE clients SET creditBalance = creditBalance + :amount WHERE id = :clientId")
    suspend fun adjustCreditBalance(clientId: Long, amount: Double)

    @Query("UPDATE clients SET returnableBottlesPending = returnableBottlesPending + :count WHERE id = :clientId")
    suspend fun adjustReturnables(clientId: Long, count: Int)
}
