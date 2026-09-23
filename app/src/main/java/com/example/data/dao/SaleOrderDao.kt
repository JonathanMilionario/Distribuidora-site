package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.SaleOrderEntity
import com.example.data.entity.SaleOrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleOrderDao {
    @Query("SELECT * FROM sale_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<SaleOrderEntity>>

    @Query("SELECT * FROM sale_orders WHERE id = :id")
    suspend fun getOrderById(id: Long): SaleOrderEntity?

    @Query("SELECT * FROM sale_order_items WHERE saleOrderId = :orderId")
    fun getItemsForOrder(orderId: Long): Flow<List<SaleOrderItemEntity>>

    @Query("SELECT * FROM sale_order_items WHERE saleOrderId = :orderId")
    suspend fun getItemsForOrderSync(orderId: Long): List<SaleOrderItemEntity>

    @Query("SELECT * FROM sale_order_items")
    fun getAllOrderItems(): Flow<List<SaleOrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: SaleOrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<SaleOrderItemEntity>)

    @Update
    suspend fun updateOrder(order: SaleOrderEntity)

    @Delete
    suspend fun deleteOrder(order: SaleOrderEntity)

    @Query("UPDATE sale_orders SET paymentStatus = :status WHERE id = :orderId")
    suspend fun updatePaymentStatus(orderId: Long, status: String)

    @Query("UPDATE sale_orders SET orderStatus = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)
}
