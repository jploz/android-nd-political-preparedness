package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.repository.RepresentativesRepository

object ServiceLocator {

    private var electionsRepository: ElectionsRepository? = null
    private var representativesRepository: RepresentativesRepository? = null

    fun provideElectionsRepository(context: Context): ElectionsRepository {
        synchronized(this) {
            return electionsRepository ?: createElectionsRepository(context)
        }
    }

    fun provideRepresentativesRepository(context: Context): RepresentativesRepository {
        synchronized(this) {
            return representativesRepository ?: createRepresentativesRepository(context)
        }
    }

    private fun createElectionsRepository(context: Context): ElectionsRepository {
        val database = ElectionDatabase.getInstance(context)
        val civicsApi = CivicsApi.retrofitService
        val repository = ElectionsRepository(database.electionDao, civicsApi)
        electionsRepository = repository
        return repository
    }

    private fun createRepresentativesRepository(context: Context): RepresentativesRepository {
        val civicsApiService = CivicsApi.retrofitService
        val repository = RepresentativesRepository(civicsApiService)
        representativesRepository = repository
        return repository
    }
}
