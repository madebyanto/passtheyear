package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.util.AutofillFieldType
import com.example.ui.util.autofill
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppTopBar
import com.example.ui.components.InfoBanner
import com.example.util.LocalAppLanguage
import com.example.util.getTranslations

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val lang = LocalAppLanguage.current.currentLanguage
    val strings = getTranslations(lang)
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = strings.resetPasswordTitle,
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                showLanguageSelector = true
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = strings.resetPasswordTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = strings.resetPasswordSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.error != null) {
                InfoBanner(
                    message = uiState.error!!,
                    isError = true,
                    onDismiss = { viewModel.clearMessages() }
                )
            }

            if (emailSent) {
                InfoBanner(
                    message = strings.resetEmailSent,
                    isError = false
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(strings.emailLabel) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.sendPasswordReset(email, strings) { emailSent = true }
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .autofill(
                        types = listOf(AutofillFieldType.Email),
                        onFill = { email = it }
                    )
                    .testTag("reset_email_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.sendPasswordReset(email, strings) { emailSent = true }
                },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("reset_password_submit_btn")
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = strings.sendResetLinkBtn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("reset_back_to_login_btn")
            ) {
                Text(
                    text = strings.backToLogin,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
