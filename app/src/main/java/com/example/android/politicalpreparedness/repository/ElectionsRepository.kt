package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.entities.Favorite
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class ElectionsRepository(
    private val electionDao: ElectionDao,
    private val civicsApiService: CivicsApiService
) {

    fun getUpcomingElections(): Flow<List<Election>> {
        val today = Calendar.getInstance().time
        return electionDao.getAllElectionsAfterInclusive(date = today)
    }

    fun getElectionById(id: Int): Flow<Election> = electionDao.getElectionById(id)

    suspend fun refreshUpcomingElections() {
        withContext(Dispatchers.IO) {
            try {
                val electionResponse = civicsApiService.getElections()
                electionDao.insertAllElections(electionResponse.elections)
            } catch (e: Exception) {
                Timber.e("Unable to query elections: $e")
                throw e
            }
        }
    }

    fun getFavoriteById(id: Int): Flow<Favorite?> = electionDao.getFavoriteById(id)

    suspend fun markElectionAsFavorite(id: Favorite) = electionDao.markElectionAsFavorite(id)

    suspend fun unmarkElectionAsFavorite(id: Int) = electionDao.unmarkElectionAsFavorite(id)

    fun getFavoriteElections(): Flow<List<Election>> {
        return electionDao.getAllFavoriteElections()
    }

    // this could/should be moved into voter info parser
    fun getVoterInfoAddressFromDivision(division: Division): String {
        // VIP test election (id=2000) provides only country code, no state code: voter info n.a.
        // some elections require an address in the form: "state country" e.g. "va us"
        if (division.state.isBlank()) {
            throw Exception("Unable to get address from division: state is not available")
        }
        if (division.country.isNotBlank()) {
            return division.state.plus(" ").plus(division.country)
        }
        return division.state
    }

    suspend fun getVoterInfo(electionId: Int, address: String): VoterInfoResponse {
        return withContext(Dispatchers.IO) {
            civicsApiService.getVoterInfo(electionId, address)
        }
    }
}
