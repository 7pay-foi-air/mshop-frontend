package hr.foi.air.mshop.imageloader.interfaces

import android.graphics.drawable.Icon
import android.media.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

interface IImageLoader {
    val name: String
    val icon: ImageVector
    @Composable
    fun LoadImage(listener: IPhotoListener)
    fun pickImage()
}