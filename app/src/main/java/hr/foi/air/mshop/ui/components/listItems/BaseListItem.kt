package hr.foi.air.mshop.ui.components.listItems

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.ui.theme.MShopCard

@Composable
fun BaseListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    leadingContent: @Composable (() -> Unit)? = null,
    centerContent: @Composable RowScope.() -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MShopCard.shape,
        colors = MShopCard.elevatedColors(),
        elevation = MShopCard.elevatedElevation()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.lg)
        ) {
            if (leadingContent != null) {
                Box(
                    modifier = Modifier.size(Dimens.listThumb),
                    contentAlignment = Alignment.Center
                ) { leadingContent() }
            }

            centerContent(this)

            if (trailingContent != null) trailingContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BaseListItemPreview() {
    hr.foi.air.mshop.ui.theme.MShopTheme(darkTheme = false) {
        BaseListItem(
            onClick = {},
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(Dimens.listThumb)
                        .background(MaterialTheme.colorScheme.surface)
                )
            },
            centerContent = {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(Dimens.md)
                )
            },
            trailingContent = {
                Box(
                    modifier = Modifier
                        .size(Dimens.iconLg)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        )
    }
}
