package com.example.sample.blesampleapplication.ui.component



import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ConfirmAlert(
    modifier: Modifier = Modifier,
    titleText: String,
    bodyText: String,
    confirmText: String = "확인",
    confirmHandle: () -> Unit,
    dismissText: String = "취소",
    dismissHandle: () -> Unit,
    onDismissRequest: () -> Unit) {

    AlertDialog(
        modifier = modifier,
        title = {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, text = titleText)
        },
        text = {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, softWrap = true, text = bodyText)
        },
        shape = RoundedCornerShape(size = 30f),
        confirmButton = { ConfirmButton(confirmText = confirmText, confirmHandle = confirmHandle) },
        dismissButton = { DismissButton(dismissText = dismissText, dismissHandle = dismissHandle) },
        onDismissRequest = onDismissRequest
    )
}

@Composable
fun Alert(
    modifier: Modifier = Modifier,
    titleText: String,
    bodyText: String,
    confirmText: String = "확인",
    confirmHandle: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        title = {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, text = titleText)
        },
        text = {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, softWrap = true, text = bodyText)
        },
        shape = RoundedCornerShape(size = 30f),
        confirmButton = { ConfirmButton(confirmText = confirmText, confirmHandle = confirmHandle) },
        onDismissRequest = onDismissRequest
    )
}


@Composable
fun ConfirmButton(
    modifier: Modifier = Modifier,
    confirmText: String = "확인",
    confirmHandle: () -> Unit) {

    Button(modifier = modifier, onClick = confirmHandle) {
        Text(text = confirmText)
    }
}

@Composable
fun DismissButton(
    modifier: Modifier = Modifier,
    dismissText: String = "취소",
    dismissHandle: () -> Unit) {
    Button(modifier = modifier, onClick = dismissHandle) {
        Text(text = dismissText)
    }
}


@Preview
@Composable
fun AlertPreview() {
    Alert(titleText = "타이틀", bodyText = "텍스트", confirmHandle = {}, onDismissRequest = {})
}

@Preview
@Composable
fun ConfirmAlertPreview() {
    ConfirmAlert(
        titleText = "타이틀",
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
