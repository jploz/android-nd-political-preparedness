package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.PoliticalPreparednessApp
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.google.android.material.snackbar.Snackbar

class ElectionsFragment : Fragment() {

    // declare ViewModel, data is loaded when view model is created
    private val viewModel by viewModels<ElectionsViewModel> {
        ElectionsViewModelFactory(
            (requireContext().applicationContext as PoliticalPreparednessApp).electionsRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val upcomingElectionListAdapter =
            ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->
                this.findNavController()
                    .navigate(
                        ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                            election.id,
                            election.division
                        )
                    )
            })
        binding.upcomingElectionsList.adapter = upcomingElectionListAdapter

        viewModel.errorMessage.observe(viewLifecycleOwner, { resId ->
            Snackbar.make(requireView(), resId, Snackbar.LENGTH_LONG).show()
        })

        return binding.root
    }
}
