package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.repository.ElectionsRepository

// factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(
    private val electionsRepository: ElectionsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (VoterInfoViewModel(electionsRepository) as T)

}