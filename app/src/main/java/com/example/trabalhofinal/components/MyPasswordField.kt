package com.example.trabalhofinal.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun MyPasswordField(
    label: String,
    value: String,
    errorMessage: String = "",
    onValueChange: (String) -> Unit
) {
    var isTouched = remember {
        mutableStateOf(false)
    }

    var focusRequester = remember {
        FocusRequester()
    }

    var isPasswordVisibility = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = value ,
        onValueChange = {
            isTouched.value = true
            onValueChange(it)
        },
        singleLine = true,
        label = {
            Text(text = label)
        },
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.hasFocus)
                    isTouched.value = true
            },
        trailingIcon = {
            IconButton(onClick = {
                isPasswordVisibility.value = !isPasswordVisibility.value
            }) {
                Icon(
                    imageVector =  if (isPasswordVisibility.value)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff,
                    contentDescription = "Show/Hide password")
            }
        },
        visualTransformation = if (isPasswordVisibility.value)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        isError = errorMessage.isNotBlank(),
        supportingText = {
            if (errorMessage.isNotBlank()) {
                Text(text = errorMessage)
            }
        }
    )
}
