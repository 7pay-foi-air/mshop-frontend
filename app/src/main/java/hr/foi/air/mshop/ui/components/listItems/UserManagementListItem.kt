package hr.foi.air.mshop.ui.components.listItems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import hr.foi.air.mshop.core.models.User
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.utils.userRoleToHrLabel

@Composable
fun UserManagementListItem(
    user: User,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier,
    canModerate: Boolean = true,
) {
    BaseListItem(
        modifier = modifier,
        onClick = {  },
        centerContent = {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.firstName + " " + user.lastName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = userRoleToHrLabel(user.role),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                IconButton(
                    onClick = onEditClicked,
                    enabled = canModerate,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Uredi korisnicke podatke",
                        modifier = Modifier.size(20.dp),
                        tint = if (canModerate) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
                IconButton(
                    onClick = onDeleteClicked,
                    enabled = canModerate, 
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Obriši korisnika",
                        modifier = Modifier.size(20.dp),
                        tint = if (canModerate) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    )
}