package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.SingleLiveEvent
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


// construct ViewModel and provide election datasource
class ElectionsViewModel(
    private val electionsRepository: ElectionsRepository
) : ViewModel() {

    val upcomingElections = electionsRepository.getUpcomingElections().asLiveData()

    val favoriteElections = electionsRepository.getFavoriteElections().asLiveData()

    val errorMessage = SingleLiveEvent<Int>()

    init {
        refreshUpcomingElections()
    }

    private fun refreshUpcomingElections() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                electionsRepository.refreshUpcomingElections()
            } catch (e: Exception) {
                Timber.e("$e")
                errorMessage.postValue(R.string.error_refreshing_elections)
            }
        }
    }
}
