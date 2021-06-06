package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.database.entities.Favorite
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

    // select all elections, results are ordered to ensure predictable behaviour
    @Query("SELECT * FROM election_table WHERE electionDay >= :date ORDER BY electionDay ASC")
    fun getAllElectionsAfterInclusive(date: Date): Flow<List<Election>>

    // select single election query
    @Query("SELECT * FROM election_table WHERE id = :id")
    fun getElectionById(id: Int): Flow<Election>

    //TODO: use custom query here and take Int as parameter?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markElectionAsFavorite(id: Favorite)

    @Query("DELETE FROM favorite_table WHERE id = :id")
    suspend fun unmarkElectionAsFavorite(id: Int)

    @Query("""SELECT * FROM election_table INNER JOIN favorite_table ON election_table.id = favorite_table.id ORDER BY electionDay ASC""")
    fun getAllFavoriteElections(): Flow<List<Election>>

    @Query("SELECT * FROM election_table INNER JOIN favorite_table ON election_table.id = favorite_table.id AND favorite_table.id = :id")
    fun getFavoriteElectionById(id: Int): Flow<Election>

    @Query("SELECT * FROM favorite_table WHERE id = :id")
    fun getFavoriteById(id: Int): Flow<Favorite?>
}
