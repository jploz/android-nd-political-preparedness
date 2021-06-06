package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.SingleLiveEvent
import com.example.android.politicalpreparedness.database.entities.Favorite
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(
    private val electionsRepository: ElectionsRepository
) : ViewModel() {

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election> = _election

    // voter info is not hold in live data: rather it is parsed and the required information
    // such as URLs and address are stored directly in its own live data

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    private val _votingLocationFinderUrl = MutableLiveData<String>()
    val votingLocationFinderUrl: LiveData<String> = _votingLocationFinderUrl

    private val _ballotInfoUrl = MutableLiveData<String>()
    val ballotInfoUrl: LiveData<String> = _ballotInfoUrl

    private val _openUrlInBrowserEvent = SingleLiveEvent<String>()
    val openUrlInBrowserEvent: SingleLiveEvent<String> = _openUrlInBrowserEvent

    //TODO: how to use favorite (as flow) directly as live data (how to set election id?)?
    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun toggleBookmarkElection() {
        viewModelScope.launch {
            election.value?.let { currentSelection ->
                val fav = electionsRepository.getFavoriteById(currentSelection.id).firstOrNull()
                Timber.d("Favorite from DB: $fav")
                if (fav != null) {
                    electionsRepository.unmarkElectionAsFavorite(fav.id)
                    _isFavorite.value = false
                } else {
                    electionsRepository.markElectionAsFavorite(Favorite(id = currentSelection.id))
                    _isFavorite.value = true
                }
            }
        }
    }

    //TODO: rename to `setupElectionInfo`
    fun loadVoterInfo(electionId: Int, division: Division) {
        //TODO: improve check of passed arguments e.g. division.state? (not empty, valid, etc.)?
        Timber.d("setupElectionInfo: election: $electionId")

        viewModelScope.launch {
            try {
                _election.value = electionsRepository.getElectionById(electionId).first()

                val fav = electionsRepository.getFavoriteById(electionId).firstOrNull()
                Timber.d("Favorite from DB: $fav")
                _isFavorite.value = fav != null

                val voterInfoResponse = electionsRepository.getVoterInfo(
                    electionId,
                    electionsRepository.getVoterInfoAddressFromDivision(division)
                )
                _address.value = getAddressFormatted(voterInfoResponse)
                _votingLocationFinderUrl.value = getVotingLocationFinderUrl(voterInfoResponse)
                _ballotInfoUrl.value = getBallotInfoUrl(voterInfoResponse)

            } catch (e: Exception) {
                Timber.w("Unable to fetch voter info: $e")
                //TODO: trigger display of error message
            }
        }
    }

    //TODO: refactor methods for parsing VoterInfoResponse into its own parser
    //TODO: improve handling of default values of each property, wrap them into a class
    private fun getAddressFormatted(voterInfo: VoterInfoResponse?): String {
        voterInfo?.state?.let { states ->
            if (states.isNotEmpty()) {
                // try to get correspondence address first, then try physical address
                states[0].electionAdministrationBody.correspondenceAddress?.let {
                    return it.toFormattedString()
                }
                states[0].electionAdministrationBody.physicalAddress?.let {
                    return it.toFormattedString()
                }
            }
        }
        return ""
    }

    private fun getVotingLocationFinderUrl(voterInfo: VoterInfoResponse?): String {
        voterInfo?.state?.let { states ->
            if (states.isNotEmpty()) {
                states[0].electionAdministrationBody.votingLocationFinderUrl?.let {
                    return it
                }
            }
        }
        return ""
    }

    private fun getBallotInfoUrl(voterInfo: VoterInfoResponse?): String {
        voterInfo?.state?.let { states ->
            if (states.isNotEmpty()) {
                states[0].electionAdministrationBody.ballotInfoUrl?.let {
                    return it
                }
            }
        }
        return ""
    }

    fun openUrlInBrowser(uri: String?) {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        if (!uri.isNullOrBlank()) {
            _openUrlInBrowserEvent.value = uri!!
        }
    }
}
