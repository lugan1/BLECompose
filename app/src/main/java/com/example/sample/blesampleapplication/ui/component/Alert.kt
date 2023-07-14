package com.example.sample.blesampleapplication.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

sealed class DialogState {
    object dismiss: DialogState()
    data class show(val title: String, val body: String, val onConfirm: () -> Unit, val onDismiss: () -> Unit): DialogState()
}

@Composable
fun Alert(
    titleTxt: String,
    bodyText: String,
    confirmText: String = "확인",
    confirmHandle: () -> Unit,
    dismissText: String = "취소",
    dismissHandle: () -> Unit,
    onDismissRequest: () -> Unit) {

    AlertDialog(
        title = {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, text = titleTxt)
        },
        text = {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, softWrap = true, text = bodyText)
        },
        shape = RoundedCornerShape(size = 30f),
        confirmButton = { ConfirmButton(confirmText, confirmHandle) },
        dismissButton = { DismissButton(dismissText, dismissHandle) },
        onDismissRequest = onDismissRequest)
}


@Composable
fun ConfirmButton(
    confirmText: String = "확인",
    confirmHandle: () -> Unit) {

    Button(onClick = confirmHandle) {
        Text(text = confirmText)
    }
}

@Composable
fun DismissButton(
    dismissText: String = "취소",
    dismissHandle: () -> Unit) {
    Button(onClick = dismissHandle) {
        Text(text = dismissText)
    }
}

@Preview
@Composable
fun AlertPreview() {
    Alert(
        titleTxt = "타이틀",
        bodyText = "텍스트",
        confirmHandle = {},
        dismissHandle = {},
        onDismissRequest = {})
}

@Preview
@Composable
fun ConfirmPreview() {
    ConfirmButton(confirmHandle = {})
}

@Preview
@Composable
fun DismissPreview() {
    DismissButton(dismissHandle = {})
}