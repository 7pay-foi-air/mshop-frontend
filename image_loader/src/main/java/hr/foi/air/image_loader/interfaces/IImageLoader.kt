package hr.foi.air.image_loader.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

interface IImageLoader {
    val name: String
    val icon: ImageVector
    @Composable
    fun LoadImage(listener: IPhotoListener)
    fun pickImage()
}