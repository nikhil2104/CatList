package com.intuit.catlist.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cat")
data class Cat(
    @PrimaryKey
    var id: String,
    val url: String,
    @Embedded(prefix = "breed")
    var breed: Breed?,
    @Ignore
    val breeds: List<Breed>?,
) :Parcelable {
    constructor(id: String,
                url: String) : this(id, url, null,null)
}