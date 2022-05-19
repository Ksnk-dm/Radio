package com.ksnk.radio.data.entity


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(indices = [Index(value = ["name"], unique = true)])
data class RadioWave(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var name: String?,
    var image: String?,
    var url: String?,
    var fmFrequency: String?,
    var favorite: Boolean?,
    var custom:Boolean?,
    var countOpen:Int?
) : Serializable {
    constructor() : this(null, null, null, null, null, false, false, 0)
}