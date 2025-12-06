package hr.foi.air.mshop.imageloader.interfaces

import android.media.Image

interface IPhotoListener {
    fun onSuccess(): Image
    fun onFailure(): String
}