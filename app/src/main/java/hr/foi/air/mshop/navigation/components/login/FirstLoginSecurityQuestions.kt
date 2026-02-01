package hr.foi.air.mshop.navigation.components.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.foi.air.mshop.ui.components.buttons.NextArrow
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.utils.AppMessageManager
import hr.foi.air.mshop.utils.AppMessageType
import hr.foi.air.mshop.viewmodels.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FirstLoginSecurityQuestions(
    onNext: () -> Unit,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            if (message.isNotBlank()) {
                AppMessageManager.show(message, AppMessageType.ERROR)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.screenHPadding, vertical = Dimens.screenVPadding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.xxxl))

        Text(
            text = "mShop",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 48.sp,
                lineHeight = 52.sp
            ),
            modifier = Modifier.padding(top = Dimens.lg, bottom = Dimens.lg)
        )

        Text(
            text = "Sigurnosna pitanja",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = Dimens.xxl)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.xl),
            horizontalAlignment = Alignment.Start
        ) {
            SecurityQuestionItem(
                question = viewModel.securityQuestion1,
                answer = viewModel.securityAnswer1,
                onAnswerChange = { viewModel.securityAnswer1 = it }
            )

            SecurityQuestionItem(
                question = viewModel.securityQuestion2,
                answer = viewModel.securityAnswer2,
                onAnswerChange = { viewModel.securityAnswer2 = it }
            )

            SecurityQuestionItem(
                question = viewModel.securityQuestion3,
                answer = viewModel.securityAnswer3,
                onAnswerChange = { viewModel.securityAnswer3 = it }
            )
        }

        Spacer(modifier = Modifier.height(Dimens.xxl))

        NextArrow(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp, bottom = 48.dp),
            size = Dimens.fab,
            onClick = { viewModel.saveFinalAccountSetup(context, onComplete = onNext) }
        )
    }
}

@Composable
fun SecurityQuestionItem(
    question: String,
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Vaš odgovor") }
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun FirstLoginSecurityQuestionsPreview() {
    FirstLoginSecurityQuestions(onNext = {}, viewModel = LoginViewModel())
}