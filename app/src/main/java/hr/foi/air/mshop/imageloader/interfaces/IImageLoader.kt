package hr.foi.air.mshop.imageloader.interfaces

import android.graphics.drawable.Icon
import android.media.Image

interface IImageLoader {
    fun getPhoto(listener: IPhotoListener)
    fun getName(name:  String): String
    fun getIcon(icon: Icon): Icon
}