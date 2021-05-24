package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.repository.ElectionsRepository

// create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(
    private val electionsRepository: ElectionsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (ElectionsViewModel(electionsRepository) as T)

}
