package com.ksnk.radio.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*
import kotlin.random.Random

@Entity
data class RadioWave(
    @PrimaryKey
    val id: Int= 20,
    val name: String?="",
    val image: String?="",
    val url: String?="",
    val fmFrequency: String?=""
) : Serializable {
    override fun toString(): String {
        return "RadioWave(name='$name', image='$image', url='$url', fmFrequency='$fmFrequency')"
    }
}