package com.example.android.politicalpreparedness.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class Favorite constructor(
    @PrimaryKey val id: Int
)
