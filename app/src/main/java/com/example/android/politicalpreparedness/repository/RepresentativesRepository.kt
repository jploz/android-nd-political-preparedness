package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.representative.model.UserAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class RepresentativesRepository(
    private val civicsApiService: CivicsApiService
) {

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    suspend fun getRepresentatives(address: UserAddress): List<Representative> {
        return withContext(Dispatchers.IO) {
            val (offices, officials) = civicsApiService.getRepresentativeInfoByAddress(
                formatUserAddress(address)
            )
            offices.flatMap { office -> office.getRepresentatives(officials) }
        }
    }

    private fun formatUserAddress(address: UserAddress): String {
        var output = address.line1.plus("\n")
        if (address.line2.isNotEmpty()) output = output.plus(address.line2).plus("\n")
        output = output.plus("${address.city}, ${address.state} ${address.zip}")
        Timber.d("formatted user address for query: $output")
        return output
    }
}
