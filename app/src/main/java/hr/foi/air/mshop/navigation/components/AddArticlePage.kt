package hr.foi.air.mshop.navigation.components

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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import hr.foi.air.mshop.ui.components.StyledButton
import hr.foi.air.mshop.ui.components.UnderLabelTextField


@Composable
fun AddArticlePage() {

    var ean by remember { mutableStateOf("") }
    var articleName by remember { mutableStateOf("") }
    var articleDescription by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

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
            val lastSegment = uri.lastPathSegment
            val displayName = lastSegment?.substringAfterLast('/') ?: "slika"

            imagePath = displayName
        }
    }

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
            "Dodavanje novog artikla",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        UnderLabelTextField(
            caption = "Šifra proizvoda (ean)",
            value = ean,
            onValueChange = { ean = it },
            placeholder = "",
            isError = eanVisited && eanEmpty,
            errorText = if (eanVisited && eanEmpty) "Obavezno polje" else null,
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
            errorText = if (articleNameVisited && articleNameEmpty) "Obavezno polje" else null,
            modifier = Modifier.onFocusChanged { f ->
                if (f.isFocused) articleNameHadFocus = true
                if (!f.isFocused && articleNameHadFocus) articleNameVisited = true
            }
        )

        Spacer(Modifier.height(8.dp))

        UnderLabelTextField(
            caption = "Opis artikla",
            value = articleDescription,
            onValueChange = { articleDescription = it },
            placeholder = "",
            isError = false,
            errorText = null,
            singleLine = false,
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
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Preview slike
            Box(
                modifier = Modifier
                    .size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Odabrana slika",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {

                    Text("Nije odabrana slika")
                }
            }

            StyledButton(
                label = "DODAJ",
                enabled = allValid,
                onClick = {
                    // akcija
                },
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}





