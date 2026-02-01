package hr.foi.air.mshop.ui.components.textFields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.theme.MShopTextField

@Composable
fun UnderLabelPasswordField(
    modifier: Modifier = Modifier,
    caption: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Input",
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        modifier = modifier.fillMaxWidth(),
        shape = MShopTextField.shape,
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Sakrij lozinku" else "Prikaži lozinku"
                )
            }
        },
        colors = MShopTextField.colors(),
        supportingText = {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}