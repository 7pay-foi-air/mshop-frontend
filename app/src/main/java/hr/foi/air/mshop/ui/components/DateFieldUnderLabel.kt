package hr.foi.air.mshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DateFieldUnderLabel(
    modifier: Modifier = Modifier,
    caption: String,
    value: String,
    onOpenPicker: () -> Unit,
    isError: Boolean,
    errorText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        placeholder = { Text("Odaberi datum") },
        trailingIcon = {
            IconButton(onClick = onOpenPicker) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = "Odaberi datum")
            }
        },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.40f),
            focusedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.20f),
            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        supportingText = {
            val hasError = isError && !errorText.isNullOrBlank()
            Text(
                text = if (hasError) "$caption - $errorText" else caption,
                style = MaterialTheme.typography.labelSmall,
                color = if (hasError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
