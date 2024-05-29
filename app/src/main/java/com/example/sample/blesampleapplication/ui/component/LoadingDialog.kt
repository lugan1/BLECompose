package com.example.sample.blesampleapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    onDismissRequest : () -> Unit,
    modifier: Modifier = Modifier,
    text: String
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.background(Color.White)
            .border(1.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier.height(60.dp).width(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(10.dp), strokeWidth = 1.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = text)
        }
    }
}

@Preview
@Composable
fun LoadingDialogPreview() {
    LoadingDialog(
        onDismissRequest = {},
        text = "Loading..."
    )
}