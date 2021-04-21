package com.intuit.catlist.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Breed(
    val id: String,
    val name: String,
    val temperament: String,
    val origin: String,
    val description: String,
    @SerializedName("life_span")
    val lifeSpan: String,
) : Parcelable