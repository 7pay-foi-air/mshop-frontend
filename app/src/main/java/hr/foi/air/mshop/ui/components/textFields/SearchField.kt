package hr.foi.air.mshop.ui.components.textFields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search",
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "Search Icon"
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.40f),
            focusedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.20f),
            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}