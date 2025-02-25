package com.example.composeplayground.features.browser.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen() {
    val focusManager = LocalFocusManager.current

    var textFieldInput by remember { mutableStateOf("https://google.com") }
    var browserUrl by remember { mutableStateOf("") }
    var browserLoadingProgress by remember { mutableFloatStateOf(0F) }

    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
        }
    }

    webView.webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            browserLoadingProgress = 0F
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            browserLoadingProgress = 1F

//            val jsScript = "alert('This is an alert!')"
//            view?.evaluateJavascript(jsScript, null)
        }
    }
    webView.webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            browserLoadingProgress = newProgress / 100F
        }

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            return super.onJsAlert(view, url, message, result)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(top = 40.dp)) {
        OutlinedTextField(
            value = textFieldInput,
            onValueChange = { value ->
                textFieldInput = value
            },
            placeholder = { Text("Start your search") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = {
                focusManager.clearFocus()
                browserUrl = textFieldInput
            }),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        LinearProgressIndicator(progress = { browserLoadingProgress }, modifier = Modifier.fillMaxWidth())

        AndroidView(factory = { webView }, modifier = Modifier.weight(1f)) { _ ->
            webView.loadUrl(browserUrl)
        }
    }
}