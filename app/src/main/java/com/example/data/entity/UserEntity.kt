package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val username: String,
    val password: String,
    val role: String, // "ADMIN" ou "FUNCIONARIO"
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
