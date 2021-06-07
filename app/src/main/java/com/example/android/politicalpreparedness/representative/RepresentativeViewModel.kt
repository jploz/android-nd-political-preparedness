package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.SingleLiveEvent
import com.example.android.politicalpreparedness.repository.RepresentativesRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.representative.model.UserAddress
import kotlinx.coroutines.launch
import timber.log.Timber

//TODO: display error messages to user using event or similar

class RepresentativeViewModel(
    private val representativesRepository: RepresentativesRepository
) : ViewModel() {

    val errorMessage = SingleLiveEvent<Int>()

    // establish live data for representatives and address
    private val _address = MutableLiveData(UserAddress())
    val address: LiveData<UserAddress> = _address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>> = _representatives

    // function to fetch representatives from API from a provided address
    fun loadRepresentatives() {
        val userAddress = address.value
        Timber.d("queryRepresentatives for address: $userAddress")
        if (userAddress != null) {
            viewModelScope.launch {
                try {
                    _representatives.value =
                        representativesRepository.getRepresentatives(userAddress)
                    Timber.d("Representatives: ${_representatives.value}")

                } catch (e: Exception) {
                    Timber.e("$e")
                    _representatives.value = emptyList()
                    errorMessage.value = R.string.error_get_representatives
                }
            }
        } else {
            Timber.w("Unable to load representatives: address is not set")
            errorMessage.value = R.string.error_get_representatives_no_address
        }
    }

    fun setAddress(userAddress: UserAddress) {
        _address.value = userAddress
    }
}
