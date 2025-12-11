package hr.foi.air.image_loader.interfaces

import android.media.Image
import android.net.Uri

interface IPhotoListener {
    fun onSuccess(imageUri: Uri)
    fun onFailure(message: String)
}