package hr.foi.air.mshop.imageloader

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import hr.foi.air.image_loader.interfaces.IImageLoader
import hr.foi.air.image_loader.interfaces.IPhotoListener

class GalleryLoaderModule : IImageLoader {
    override val name: String = "Galerija"
    override val icon: ImageVector = Icons.Outlined.PhotoLibrary
    private var listener: IPhotoListener? = null
    private var launchImagePicker: (() -> Unit)? = null

    @Composable
    override fun LoadImage(listener: IPhotoListener) {
        this.listener = listener
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {uri: Uri? ->
                if(uri != null){
                    this.listener?.onSuccess(uri)
                } else {
                    this.listener?.onFailure("Nije odabrana slika.")
                }
            }
        )

        LaunchedEffect(Unit) {
            launchImagePicker = {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    }

    override fun pickImage() {
        launchImagePicker?.invoke()
    }
}