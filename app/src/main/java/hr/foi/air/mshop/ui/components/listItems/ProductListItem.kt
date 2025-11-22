package hr.foi.air.mshop.ui.components.listItems

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.core.models.Article
import hr.foi.air.mshop.ui.components.QuantitySelector

@Composable
fun ProductListItem(
    product: Article,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onClick: () -> Unit,
    showRemoveButton: Boolean = false,
    onRemove: (() -> Unit)? = null
) {
    BaseListItem(
        onClick = onClick, // could be used later for accessing product details
        leadingContent = {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Image(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Slika artikla",
                    modifier = Modifier.size(35.dp),
                    contentScale = ContentScale.Crop
                )
            }
        },
        centerContent = {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.articleName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "€${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        trailingContent = {
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                QuantitySelector(
                    quantity = quantity,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement
                )

                if (showRemoveButton && onRemove != null) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Ukloni iz košarice",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    )
}