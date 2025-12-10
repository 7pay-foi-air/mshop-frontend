package hr.foi.air.mshop.imageloader

import hr.foi.air.mshop.imageloader.interfaces.IImageLoader

class ImageLoaderManager {
    val imageLoaders: List<IImageLoader> = listOf(
        GalleryLoaderModule()
    )
}