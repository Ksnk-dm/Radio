package com.ksnk.radio.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*
import kotlin.random.Random

@Entity
data class RadioWave(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var name: String?,
    var image: String?,
    var url: String?,
    var fmFrequency: String?,
    var favorite: Boolean?
)

    : Serializable {
    constructor() : this(null, null, null, null, null, false)


    override fun toString(): String {
        return "RadioWave(name='$name', image='$image', url='$url', fmFrequency='$fmFrequency')"
    }
}