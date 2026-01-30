package hr.foi.air.mshop.ui.theme

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable

object MShopSheetDefaults {

    @Composable
    fun containerColor() = MaterialTheme.colorScheme.background

    @Composable
    fun textFieldColors() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        disabledContainerColor = MaterialTheme.colorScheme.background,
        errorContainerColor = MaterialTheme.colorScheme.background,

        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        errorBorderColor = MaterialTheme.colorScheme.error,

        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
        errorLabelColor = MaterialTheme.colorScheme.error,

        cursorColor = MaterialTheme.colorScheme.primary
    )

    @Composable
    fun datePickerColors() = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background,

        titleContentColor = MaterialTheme.colorScheme.onBackground,
        headlineContentColor = MaterialTheme.colorScheme.onBackground,
        weekdayContentColor = MaterialTheme.colorScheme.onBackground,
        subheadContentColor = MaterialTheme.colorScheme.onBackground,
        navigationContentColor = MaterialTheme.colorScheme.onBackground,

        yearContentColor = MaterialTheme.colorScheme.onBackground,
        currentYearContentColor = MaterialTheme.colorScheme.primary,

        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,

        todayDateBorderColor = MaterialTheme.colorScheme.primary,
        todayContentColor = MaterialTheme.colorScheme.onBackground,

        dayContentColor = MaterialTheme.colorScheme.onBackground,
        disabledDayContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),

        dividerColor = MaterialTheme.colorScheme.outlineVariant
    )
}
