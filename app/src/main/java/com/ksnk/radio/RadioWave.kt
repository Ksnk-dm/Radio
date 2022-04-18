package com.ksnk.radio

import java.io.Serializable

class RadioWave(
    var name: String = "",
    var image: String = "",
    var url: String = "",
    var fmFrequency: String = ""
) : Serializable {
    override fun toString(): String {
        return "RadioWave(name='$name', image='$image', url='$url', fmFrequency='$fmFrequency')"
    }
}