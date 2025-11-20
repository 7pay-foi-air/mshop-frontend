package hr.foi.air.mshop.ui.components.ListItems

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.QuantitySelector
import hr.foi.air.mshop.viewmodels.Product

@Composable
fun ProductListItem(
    product: Product,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onClick: () -> Unit
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
                    text = product.name,
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
            QuantitySelector(
                quantity = quantity,
                onIncrement = onIncrement,
                onDecrement = onDecrement
            )
        }
    )
}