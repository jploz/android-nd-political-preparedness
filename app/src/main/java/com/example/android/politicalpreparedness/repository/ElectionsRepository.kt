package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
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
}
