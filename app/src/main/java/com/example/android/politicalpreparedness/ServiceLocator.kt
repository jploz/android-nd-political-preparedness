package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.repository.ElectionsRepository

object ServiceLocator {

    private var electionsRepository: ElectionsRepository? = null

    fun provideElectionsRepository(context: Context): ElectionsRepository {
        synchronized(this) {
            return electionsRepository ?: createElectionsRepository(context)
        }
    }

    private fun createElectionsRepository(context: Context): ElectionsRepository {
        val database = ElectionDatabase.getInstance(context)
        val civicsApi = CivicsApi.retrofitService
        val repository = ElectionsRepository(database.electionDao, civicsApi)
        electionsRepository = repository
        return repository
    }
}
