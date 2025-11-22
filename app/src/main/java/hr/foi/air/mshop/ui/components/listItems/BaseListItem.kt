package hr.foi.air.mshop.ui.components.listItems

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BaseListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    leadingContent: @Composable (() -> Unit)? = null,
    centerContent: @Composable RowScope.() -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (leadingContent != null) {
                Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                    leadingContent()
                }
            }

            centerContent(this)

            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

@Preview
@Composable
fun BaseListItemPreview() {
    BaseListItem(
        onClick = {},
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(width = 1.dp, color = androidx.compose.ui.graphics.Color.Gray)
            )
        },
        centerContent = {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .size(20.dp)
                    .border(width = 1.dp, color = androidx.compose.ui.graphics.Color.Gray)
            )
        },
        trailingContent = {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(width = 1.dp, color = androidx.compose.ui.graphics.Color.Gray)
            )
        }
    )
}