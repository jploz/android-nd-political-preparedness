package com.example.android.politicalpreparedness.election

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.PoliticalPreparednessApp
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class VoterInfoFragment : Fragment() {

    private val viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(
            (requireContext().applicationContext as PoliticalPreparednessApp).electionsRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        arguments?.let { argsBundle ->
            val electionId = VoterInfoFragmentArgs.fromBundle(argsBundle).argElectionId
            val division = VoterInfoFragmentArgs.fromBundle(argsBundle).argDivision
            viewModel.loadVoterInfo(electionId, division)
        }

        // handle loading of URLs
        viewModel.openUrlInBrowserEvent.observe(
            viewLifecycleOwner,
            { uri -> openUrlInBrowser(uri) }
        )

        viewModel.errorMessage.observe(viewLifecycleOwner, { resId ->
            Snackbar.make(requireView(), resId, Snackbar.LENGTH_LONG).show()
        })

        return binding.root
    }

    // method to load URL intents
    private fun openUrlInBrowser(uri: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            requireContext().startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Timber.e("Unable to open link: $uri\n$e")
            viewModel.errorMessage.postValue(R.string.error_open_link)
        }
    }
}
