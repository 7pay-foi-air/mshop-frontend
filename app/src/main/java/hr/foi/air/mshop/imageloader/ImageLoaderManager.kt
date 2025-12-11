package hr.foi.air.mshop.imageloader

import hr.foi.air.image_loader.interfaces.IImageLoader
import hr.foi.air.cameraloader.CameraLoaderModule
class ImageLoaderManager {
    val imageLoaders: List<IImageLoader> = listOf(
        GalleryLoaderModule(),
        CameraLoaderModule()
    )
}