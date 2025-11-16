package com.example.lab_week_10.database

import androidx.room.*

@Dao
interface TotalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(total: Total)

    @Update
    fun update(total: Total)

    @Query("SELECT * FROM total WHERE id = :id")
    fun getTotal(id: Long): List<Total>
}

