package com.stardust.cetccalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_items")
data class TestItem(
    @PrimaryKey val id: Int,
    val category: String,
    val name: String,
    val details: String,
    val unitPrice: Double,
    val unit: String,
    val minQty: Int?,
    val minNote: String
)

@Entity(tableName = "db_meta")
data class DbMeta(
    @PrimaryKey val key: String,
    val value: String
)
