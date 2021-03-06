package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.repository.RepresentativesRepository
import timber.log.Timber

/**
 * An application that lazily provides a repository. Consider a Dependency Injection framework.
 *
 * Also, sets up Timber in the DEBUG Build setups.
 */
class PoliticalPreparednessApp : Application() {

    val electionsRepository: ElectionsRepository
        get() = ServiceLocator.provideElectionsRepository(this)

    val representativesRepository: RepresentativesRepository
        get() = ServiceLocator.provideRepresentativesRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
