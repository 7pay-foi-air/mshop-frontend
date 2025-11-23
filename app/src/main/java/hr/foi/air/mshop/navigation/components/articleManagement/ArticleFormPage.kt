package hr.foi.air.mshop.navigation.components.articleManagement

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.ui.components.buttons.StyledButton
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextField
import hr.foi.air.mshop.ui.components.textFields.UnderLabelTextFieldMultiline

@Composable
fun ArticleFormPage(
    articleToEdit: Article? = null,
    onSubmit: (Article) -> Unit,
    onCancel: () -> Unit
) {

    val isEditMode = articleToEdit != null

    var ean by remember(articleToEdit) { mutableStateOf(articleToEdit?.ean ?: "") }
    var articleName by remember(articleToEdit) { mutableStateOf(articleToEdit?.articleName ?: "") }
    var articleDescription by remember(articleToEdit) { mutableStateOf(articleToEdit?.description ?: "") }
    var price by remember(articleToEdit) { mutableStateOf(articleToEdit?.price?.toString() ?: "") }

    var imageUri by remember(articleToEdit) {
        mutableStateOf<Uri?>(
            articleToEdit?.imageUri?.let { Uri.parse(it) }   // ako postoji stara lokalna slika
        )
    }

    var imageUrl by remember(articleToEdit) { mutableStateOf(articleToEdit?.imageUrl) }

    // prikaz naziva slike u text fieldu (ako editaš i već postoji url)
    var imagePath by remember(articleToEdit) {
        mutableStateOf(articleToEdit?.imageUrl?.substringAfterLast('/') ?: "")
    }

    var eanVisited by remember { mutableStateOf(false) };
    var eanHadFocus by remember { mutableStateOf(false) }

    var articleNameVisited by remember { mutableStateOf(false) };
    var articleNameHadFocus by remember { mutableStateOf(false) }

    var priceVisited by remember { mutableStateOf(false) };
    var priceHadFocus by remember { mutableStateOf(false) }

    val eanEmpty = ean.isBlank()
    val articleNameEmpty = articleName.isBlank()
    val priceEmpty = price.isBlank()

    //pokusamo pretvoriti unesenu vrijednost u broj ako nije moguce unenesa vrijednost nije broj
    val eanNotNumeric = !eanEmpty && ean.toLongOrNull() == null
    val priceNotNumeric = !priceEmpty && price.toDoubleOrNull() == null

    val eanError = eanVisited && (eanEmpty || eanNotNumeric)
    val nameError = articleNameVisited && articleNameEmpty
    val priceError = priceVisited && (priceEmpty || priceNotNumeric)

    val allValid =
        ean.isNotBlank() &&
        articleName.isNotBlank() &&
        price.isNotBlank() &&
        price.toDoubleOrNull() != null


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            imageUrl = null  // nova lokalna slika ima prednost nad starom remote slikom

            val lastSegment = uri.lastPathSegment
            val displayName = lastSegment?.substringAfterLast('/') ?: "slika"
            imagePath = displayName
        }
    }

    val imageModel: Any? = imageUri ?: imageUrl


    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "mShop",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp)
        )

        Text(
            if (isEditMode) "Ažuriranje artikla" else "Dodavanje novog artikla",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        UnderLabelTextField(
            caption = "Šifra proizvoda (ean)",
            value = ean,
            onValueChange = { ean = it },
            placeholder = "",
            isError = eanError,
            errorText = when {
                !eanVisited -> null
                eanEmpty -> "Obavezno polje"
                eanNotNumeric -> "Mora biti broj"
                else -> null
            },
            //enabled = !isEditMode, //crveni se
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) eanHadFocus = true
                if (!f.isFocused && eanHadFocus) eanVisited = true
            }
        )

        Spacer(Modifier.height(8.dp))


        UnderLabelTextField(
            caption = "Naziv artikla",
            value = articleName,
            onValueChange = { articleName = it },
            placeholder = "",
            isError = nameError,
            errorText = if (nameError) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) articleNameHadFocus = true
                if (!f.isFocused && articleNameHadFocus) articleNameVisited = true
            }
        )

        Spacer(Modifier.height(8.dp))

        UnderLabelTextFieldMultiline(
            caption = "Opis artikla",
            value = articleDescription,
            onValueChange = { articleDescription = it },
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
            value = price,
            onValueChange = { price = it },
            placeholder = "",
            isError = priceError,
            errorText = when {
                !priceVisited -> null
                priceEmpty -> "Obavezno polje"
                priceNotNumeric -> "Mora biti broj (npr. 13.5 ili 13)"
                else -> null
            },
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) priceHadFocus = true
                if (!f.isFocused && priceHadFocus) priceVisited = true
            }
        )

        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Slika",
            value = imagePath,
            onValueChange = { /* read-only */ },
            placeholder = "",
            isError = false,
            errorText = null,
            trailingIcon = {
                IconButton(
                    onClick = {
                        imagePicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
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
                    label = if (isEditMode) "SPREMI" else "DODAJ",
                    enabled = allValid,
                    onClick = {
                        val newOrEdited = Article(
                            id = articleToEdit?.id,
                            ean = ean.trim(),
                            articleName = articleName.trim(),
                            description = articleDescription.trim(),
                            price = price.toDouble(),
                            imageUrl = imageUrl,
                            imageUri = imageUri?.toString(),
                            stockQuantity = 1
                        )
                        onSubmit(newOrEdited)
                    }
                )

                if (isEditMode) {
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





