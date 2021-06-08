package com.example.android.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoterInfoResponse(
    val election: Election,
    val pollingLocations: String? = null, // for future Use
    val contests: String? = null, // fo future Use
    val state: List<State>? = null,
    val electionElectionOfficials: List<ElectionOfficial>? = null // fo future Use
)
