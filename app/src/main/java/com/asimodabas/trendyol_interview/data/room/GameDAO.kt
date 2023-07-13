package com.asimodabas.trendyol_interview.data.room

import androidx.room.*
import com.asimodabas.trendyol_interview.domain.model.DetailLocal

@Dao
interface GameDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(gameDetail: DetailLocal)

    @Delete
    fun delete(gameDetail: DetailLocal)

    @Query("SELECT * FROM games")
    suspend fun getDetail(): List<DetailLocal>
}