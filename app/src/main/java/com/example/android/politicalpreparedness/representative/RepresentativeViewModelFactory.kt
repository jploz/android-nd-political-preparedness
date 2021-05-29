package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.repository.RepresentativesRepository

class RepresentativeViewModelFactory(
    private val representativesRepository: RepresentativesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (RepresentativeViewModel(representativesRepository) as T)

}
