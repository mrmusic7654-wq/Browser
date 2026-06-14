package com.example.ui

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserApp() {
    var url by remember { mutableStateOf("https://www.google.com") }
    var inputUrl by remember { mutableStateOf("https://www.google.com") }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    
    val historyList = remember { mutableStateListOf<String>() }
    var showHistory by remember { mutableStateOf(false) }

    val context = LocalContext.current

    BackHandler(enabled = showHistory || canGoBack) {
        if (showHistory) {
            showHistory = false
        } else {
            webViewRef?.goBack()
        }
    }

    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = { showHistory = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (historyList.isEmpty()) {
                    Text(
                        text = "No history yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(historyList.reversed()) { historyUrl ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        url = historyUrl
                                        showHistory = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = historyUrl,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { webViewRef?.goBack() },
                                enabled = canGoBack,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(20.dp))
                            }
                            
                            OutlinedTextField(
                                value = inputUrl,
                                onValueChange = { inputUrl = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                singleLine = true,
                                shape = CircleShape,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                ),
                                textStyle = TextStyle(fontSize = 14.sp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        url = processUrl(inputUrl)
                                    }
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                            
                            IconButton(
                                onClick = { webViewRef?.reload() },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                            }
                            
                            IconButton(
                                onClick = { showHistory = true },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.Default.List, contentDescription = "History", tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
                if (progress > 0f && progress < 1f) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true

                        webViewClient = object : WebViewClient() {
                            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                                super.doUpdateVisitedHistory(view, url, isReload)
                                if (url != null && !isReload) {
                                    if (historyList.isEmpty() || historyList.last() != url) {
                                        historyList.add(url)
                                    }
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                url?.let {
                                    inputUrl = it
                                }
                                canGoBack = view?.canGoBack() ?: false
                                canGoForward = view?.canGoForward() ?: false
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false // Let WebView handle the url
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                progress = newProgress / 100f
                            }
                        }

                        setDownloadListener { downloadUrl, userAgent, contentDisposition, mimetype, contentLength ->
                            downloadFile(ctx, downloadUrl, userAgent, contentDisposition, mimetype)
                        }
                    }
                },
                update = { webView ->
                    webViewRef = webView
                    if (webView.url != url) {
                        webView.loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun processUrl(input: String): String {
    val trimmed = input.trim()
    return if (Patterns.WEB_URL.matcher(trimmed).matches() || trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }
    } else {
        "https://www.google.com/search?q=${Uri.encode(trimmed)}"
    }
}

fun downloadFile(context: Context, url: String, userAgent: String, contentDisposition: String, mimeType: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setMimeType(mimeType)
        request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url))
        request.addRequestHeader("User-Agent", userAgent)
        request.setDescription("Downloading file...")
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType))
        
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        Toast.makeText(context, "Downloading File", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to download file.", Toast.LENGTH_LONG).show()
    }
}
