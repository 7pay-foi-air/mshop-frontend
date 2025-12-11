package hr.foi.air.cameraloader

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import hr.foi.air.image_loader.interfaces.IImageLoader
import hr.foi.air.image_loader.interfaces.IPhotoListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraLoaderModule : IImageLoader {
    override val name: String = "Kamera"
    override val icon: ImageVector = Icons.Outlined.CameraAlt
    private var listener: IPhotoListener? = null

    private var launchCamera: (() -> Unit)? = null

    private var lastImageUri: Uri? = null

    @Composable
    override fun LoadImage(listener: IPhotoListener) {
        /*this.listener = listener
        val context = LocalContext.current

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = {success ->
                if(success){
                    lastImageUri?.let { uri ->
                        this.listener?.onSuccess(uri)
                    }
                } else {
                    this.listener?.onFailure("Nije odabrana slika.")
                }
            }
        )

        LaunchedEffect(Unit) {
            launchCamera = {
                val uri = createImageUri(context)
                lastImageUri = uri
                launcher.launch(uri)
            }
        }*/
    }

    override fun pickImage() {
        launchCamera?.invoke()
    }

    private fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        val storageDir = context.externalCacheDir ?: context.cacheDir
        val imageFile = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }
}
