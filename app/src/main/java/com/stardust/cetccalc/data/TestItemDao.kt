package com.stardust.cetccalc.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TestItemDao {
    @Query("SELECT * FROM test_items ORDER BY id")
    fun observeAll(): Flow<List<TestItem>>

    @Query("SELECT COUNT(*) FROM test_items")
    suspend fun count(): Int

    @Query("SELECT MAX(id) FROM test_items")
    suspend fun maxId(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TestItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TestItem)

    @Update
    suspend fun update(item: TestItem)

    @Query("DELETE FROM test_items WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM test_items")
    suspend fun deleteAll()

    @Query("SELECT value FROM db_meta WHERE `key` = :key")
    fun observeMeta(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setMeta(meta: DbMeta)
}
