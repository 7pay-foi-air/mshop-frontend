package hr.foi.air.mshop.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object MShopCard {

    val shape
        @Composable get() = RoundedCornerShape(Dimens.radiusCard)

    @Composable
    fun elevatedColors() = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )

    @Composable
    fun elevatedElevation() = CardDefaults.elevatedCardElevation(
        defaultElevation = 8.dp,
        pressedElevation = 12.dp
    )
}
