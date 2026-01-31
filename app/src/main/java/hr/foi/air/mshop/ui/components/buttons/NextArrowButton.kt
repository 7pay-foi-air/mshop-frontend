package hr.foi.air.mshop.ui.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.theme.Dimens

@Composable
fun NextArrow(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    size: Dp = Dimens.iconLg,
    tint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String = "Nastavi"
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size)
        )
    }
}
