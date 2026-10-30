package com.example.ui.util

import android.os.Build
import android.view.autofill.AutofillManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.LocalContext

enum class AutofillFieldType {
    Email,
    Username,
    Password,
    NewPassword
}

/**
 * Modifier to connect a Compose TextField with Android's Autofill framework
 * and third-party password managers (Google Password Manager, Bitwarden, 1Password, etc.).
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    types: List<AutofillFieldType>,
    onFill: (String) -> Unit
): Modifier = composed {
    val autofill = LocalAutofill.current
    val currentOnFill by rememberUpdatedState(onFill)

    val autofillTypes = remember(types) {
        types.map { type ->
            when (type) {
                AutofillFieldType.Email -> AutofillType.EmailAddress
                AutofillFieldType.Username -> AutofillType.Username
                AutofillFieldType.Password -> AutofillType.Password
                AutofillFieldType.NewPassword -> AutofillType.NewPassword
            }
        }
    }

    val autofillNode = remember(autofillTypes) {
        AutofillNode(
            autofillTypes = autofillTypes,
            onFill = { currentOnFill(it) }
        )
    }

    LocalAutofillTree.current += autofillNode

    this
        .onGloballyPositioned { coordinates ->
            autofillNode.boundingBox = coordinates.boundsInWindow()
        }
        .onFocusChanged { focusState ->
            if (focusState.isFocused) {
                autofill?.requestAutofillForNode(autofillNode)
            } else {
                autofill?.cancelAutofillForNode(autofillNode)
            }
        }
}

/**
 * Returns a helper function to commit autofill (e.g. after successful submission),
 * which signals password managers to prompt the user to save/update the credentials.
 */
@Composable
fun rememberAutofillCommit(): () -> Unit {
    val context = LocalContext.current
    return remember(context) {
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    val manager = context.getSystemService(AutofillManager::class.java)
                    manager?.commit()
                } catch (_: Exception) {
                    // Ignore autofill exceptions on devices without autofill service
                }
            }
        }
    }
}
