package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.SingleLiveEvent
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.flow.collect
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

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly
     * related tohow elections are saved/removed from the database.
     */

    fun loadVoterInfo(electionId: Int, division: Division) {
        //TODO: improve check of passed arguments e.g. division.state? (not empty, valid, etc.)?

        viewModelScope.launch {
            try {
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
            electionsRepository.getElectionById(electionId).collect {
                _election.value = it
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