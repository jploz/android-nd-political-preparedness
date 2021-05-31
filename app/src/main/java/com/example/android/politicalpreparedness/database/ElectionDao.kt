package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ElectionDao {

    // insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElection(election: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllElections(elections: List<Election>)

    // select all election query
    // results are ordered to ensure predictable behaviour
    @Query("SELECT * FROM election_table WHERE electionDay >= :date ORDER BY electionDay ASC")
    fun getAllElectionsAfterInclusive(date: Date): Flow<List<Election>>

    // select single election query
    @Query("SELECT * FROM election_table WHERE id = :id")
    fun getElectionById(id: Int): Flow<Election>

    //TODO: Add delete query
    //TODO: Add clear query
}
