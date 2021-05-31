package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.PoliticalPreparednessApp
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.UserAddress
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*

const val REQUEST_ENABLE_DEVICE_LOCATION = 37

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel by viewModels<RepresentativeViewModel> {
        RepresentativeViewModelFactory(
            (requireContext().applicationContext as PoliticalPreparednessApp).representativesRepository
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Timber.d("Location permission granted")
                checkLocationSettingsAndGetLocation()

            } else {
                Timber.d("Location permission denied")
                // display a brief hint to let user know that `Use my location` will not work
                Toast.makeText(
                    requireContext(),
                    R.string.location_permission_denied_hint,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.states,
            android.R.layout.simple_spinner_dropdown_item
        )
            .also { adapter ->
                binding.state.adapter = adapter
            }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.representativesList.adapter = RepresentativeListAdapter()

        binding.buttonLocation.setOnClickListener {
            onClickButtonLocation()
        }
        binding.buttonSearch.setOnClickListener {
            onClickButtonSearch()
        }

        return binding.root
    }

    private fun onClickButtonLocation() {
        // location permissions are checked only if user wants to use this feature actually
        hideKeyboard()
        requestLocationPermissionAndGetLocation()
    }

    private fun onClickButtonSearch() {
        hideKeyboard()
        Timber.d("Search representatives")
        viewModel.loadRepresentatives()
    }

    private fun requestLocationPermissionAndGetLocation() {
        when {
            foregroundLocationPermissionApproved() -> {
                Timber.d("Location permission granted")
                checkLocationSettingsAndGetLocation()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.use_my_location)
                    .setMessage(R.string.location_permission_rationale)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .show()
            }

            else -> {
                Timber.d("Permission was not yet asked for")
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun foregroundLocationPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) || PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    /*
     *  Uses the Location Client to check the current state of location settings,
     *  and gives the user the opportunity to turn on location services within our app.
     */
    private fun checkLocationSettingsAndGetLocation(resolve: Boolean = true) {
        Timber.d("checkLocationSettingsAndGetLocation")
        // check that the device's location is on
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())

        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            Timber.w("locationSettingsResponseTask: onFailureListener: exception: $exception")
            handleLocationSettingsCheckFailure(exception, resolve)
        }

        locationSettingsResponseTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.d("locationSettingsResponseTask: onCompleteListener: was successful")
                getLocation()
            }
        }
    }

    private fun handleLocationSettingsCheckFailure(exception: Exception, resolve: Boolean = true) {
        if (exception is ResolvableApiException && resolve) {
            Timber.d("try to enable device location")
            try {
                @Suppress("DEPRECATION")
                startIntentSenderForResult(
                    exception.resolution.intentSender,
                    REQUEST_ENABLE_DEVICE_LOCATION,
                    null,
                    0,
                    0,
                    0,
                    null
                )
            } catch (sendEx: IntentSender.SendIntentException) {
                Timber.w("Error getting location settings resolution: ${sendEx.message}")
            }

        } else {
            Snackbar.make(
                requireView(),
                R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok) {
                checkLocationSettingsAndGetLocation(resolve = false)
            }.show()
        }
    }

    private fun getLocation() {
        // get location from LocationServices and query address for it
        // the geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        Timber.d("getLocation: location requested")
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { myLocation: Location? ->
                Timber.d("getLocation: addOnSuccessListener called")

                if (myLocation != null) {
                    Timber.d("Location query successful: $myLocation.latitude/$myLocation.longitude")
                    val myAddress = geoCodeLocation(myLocation)
                    Timber.d("My location: $myAddress")
                    viewModel.setAddress(myAddress)

                } else {
                    Timber.w("unable to get location")
                    Toast.makeText(
                        requireContext(),
                        "Unable to get current location. Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: SecurityException) {
            Timber.e("$e")
        }
    }

    private fun geoCodeLocation(location: Location): UserAddress {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                UserAddress(
                    address.thoroughfare ?: "",
                    address.subThoroughfare ?: "",
                    address.locality ?: "",
                    address.adminArea ?: "",
                    address.postalCode ?: ""
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}
