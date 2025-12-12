package hr.foi.air.mshop.navigation.components.articleManagement

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.imageloader.ImageLoaderManager
import hr.foi.air.image_loader.interfaces.IPhotoListener
import hr.foi.air.image_loader.interfaces.IImageLoader
import hr.foi.air.mshop.ui.components.buttons.StyledButton
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextFieldMultiline
import hr.foi.air.mshop.ui.screens.LoaderPickerScreen
import hr.foi.air.mshop.viewmodels.articleManagement.ArticleFormViewModel

@Composable
fun ArticleFormPage(
    articleToEdit: Article? = null,
    viewModel: ArticleFormViewModel = viewModel(),
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = articleToEdit) {
        viewModel.initializeState(articleToEdit)
    }

    val imageLoaderManager = remember { ImageLoaderManager() }

    val photoListener = remember {
        object: IPhotoListener {
            override fun onSuccess(imageUri: Uri) {
                viewModel.onImageSelected(imageUri)
            }
            override fun onFailure(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    for(module in imageLoaderManager.imageLoaders) {
        module.LoadImage(listener = photoListener)
    }

    if (viewModel.isImagePickerVisible) {
        LoaderPickerScreen(
            imageLoaderManager = imageLoaderManager,
            onDismiss = { viewModel.hideImagePicker() },
            onModuleSelected = { selectedModule : IImageLoader ->
                selectedModule.pickImage()
            }
        )
    }


    val imageModel: Any? = viewModel.imageUri ?: viewModel.imageUrl


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp)
        )

        Text(
            if (viewModel.isEditMode) "Ažuriranje artikla" else "Dodavanje novog artikla",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        UnderLabelTextField(
            caption = "Šifra proizvoda (ean)",
            value = viewModel.ean,
            onValueChange = { viewModel.ean = it },
            placeholder = "",
            isError = viewModel.eanError,
            errorText = when {
                !viewModel.eanVisited -> null
                viewModel.eanEmpty -> "Obavezno polje"
                viewModel.eanNotNumeric -> "Mora biti broj"
                else -> null
            },
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) viewModel.eanVisited = true
            }
        )

        Spacer(Modifier.height(8.dp))


        UnderLabelTextField(
            caption = "Naziv artikla",
            value = viewModel.articleName,
            onValueChange = { viewModel.articleName = it },
            placeholder = "",
            isError = viewModel.nameError,
            errorText = if (viewModel.nameError) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) viewModel.articleNameVisited = true
            }
        )

        Spacer(Modifier.height(8.dp))

        UnderLabelTextFieldMultiline(
            caption = "Opis artikla",
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            placeholder = "",
            isError = false,
            errorText = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Jedinična cijena (€)",
            value = viewModel.price,
            onValueChange = { viewModel.price = it },
            placeholder = "",
            isError = viewModel.priceError,
            errorText = when {
                !viewModel.priceVisited -> null
                viewModel.priceEmpty -> "Obavezno polje"
                viewModel.priceNotNumeric -> "Mora biti broj (npr. 13.5 ili 13)"
                else -> null
            },
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) viewModel.priceVisited = true
            }
        )

        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Slika",
            value = viewModel.imagePath,
            onValueChange = { /* read-only */ },
            placeholder = "",
            isError = false,
            errorText = null,
            trailingIcon = {
                IconButton(
                    onClick = {
                        viewModel.showImagePicker()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Upload,
                        contentDescription = "Odaberi sliku",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Preview slike (prikaz stare ako je rijec o uredivanju inace prikaz nove lokalne)
            Box(
                modifier = Modifier
                    .size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageModel != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageModel),
                        contentDescription = "Slika artikla",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Nije odabrana slika")
                }
            }


            Column(modifier = Modifier.padding(start = 16.dp)) {

                StyledButton(
                    label = if (viewModel.isEditMode) "SPREMI" else "DODAJ",
                    enabled = viewModel.isFormValid,
                    onClick = {
                        viewModel.saveArticle(context)
                        onSubmit()
                    }
                )

                if (viewModel.isEditMode) {
                    Spacer(Modifier.height(8.dp))
                    StyledButton(
                        label = "ODUSTANI",
                        enabled = true,
                        onClick = onCancel
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArticleFormPagePreview() {
    ArticleFormPage(
        onSubmit = { },
        onCancel = { }
    )
}







