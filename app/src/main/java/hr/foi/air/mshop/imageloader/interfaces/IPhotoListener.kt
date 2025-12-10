package hr.foi.air.mshop.imageloader.interfaces

import android.media.Image
import android.net.Uri

interface IPhotoListener {
    fun onSuccess(imageUri: Uri)
    fun onFailure(message: String)
}