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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import kotlinx.coroutines.launch

enum class SearchEngine(val name: String, val baseUrl: String, val icon: String) {
    GOOGLE("Google", "https://www.google.com/search?q=", "G"),
    BING("Bing", "https://www.bing.com/search?q=", "B"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "D"),
    YAHOO("Yahoo", "https://search.yahoo.com/search?p=", "Y")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserApp() {
    var url by remember { mutableStateOf("https://www.google.com") }
    var inputUrl by remember { mutableStateOf("https://www.google.com") }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var pageTitle by remember { mutableStateOf("") }
    
    val historyList = remember { mutableStateListOf<String>() }
    val bookmarksList = remember { mutableStateListOf<String>() }
    
    var showHistory by remember { mutableStateOf(false) }
    var showBookmarks by remember { mutableStateOf(false) }
    var showDownloads by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showSearchEngineDialog by remember { mutableStateOf(false) }
    var showMenuDropdown by remember { mutableStateOf(false) }
    var downloadsEnabled by remember { mutableStateOf(true) }
    var selectedSearchEngine by remember { mutableStateOf(SearchEngine.GOOGLE) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    BackHandler(enabled = showHistory || showBookmarks || showSettings || showSearchEngineDialog || canGoBack) {
        when {
            showHistory -> showHistory = false
            showBookmarks -> showBookmarks = false
            showSettings -> showSettings = false
            showSearchEngineDialog -> showSearchEngineDialog = false
            canGoBack -> webViewRef?.goBack()
        }
    }

    // History Bottom Sheet
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { 
                        historyList.clear()
                        Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear History", tint = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
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
                                        inputUrl = historyUrl
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

    // Bookmarks Bottom Sheet
    if (showBookmarks) {
        ModalBottomSheet(
            onDismissRequest = { showBookmarks = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Bookmarks",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = {
                        if (!bookmarksList.contains(url)) {
                            bookmarksList.add(url)
                            Toast.makeText(context, "Bookmark added", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            if (bookmarksList.contains(url)) Icons.Filled.Bookmarks else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Add Bookmark",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (bookmarksList.isEmpty()) {
                    Text(
                        text = "No bookmarks yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(bookmarksList.reversed()) { bookmarkUrl ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        url = bookmarkUrl
                                        inputUrl = bookmarkUrl
                                        showBookmarks = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = bookmarkUrl,
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

    // Settings Bottom Sheet
    if (showSettings) {
        ModalBottomSheet(
            onDismissRequest = { showSettings = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Downloads Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column {
                        Text(
                            text = "Enable Downloads",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Allow downloading files from websites",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    androidx.compose.material3.Switch(
                        checked = downloadsEnabled,
                        onCheckedChange = { downloadsEnabled = it },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                
                // Search Engine Selection
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable { showSearchEngineDialog = true }
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Search Engine",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = selectedSearchEngine.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showSettings = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done")
                }
            }
        }
    }

    // Search Engine Dialog
    if (showSearchEngineDialog) {
        AlertDialog(
            onDismissRequest = { showSearchEngineDialog = false },
            title = { Text("Select Search Engine") },
            text = {
                Column {
                    SearchEngine.entries.forEach { engine ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSearchEngine = engine
                                    showSearchEngineDialog = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedSearchEngine == engine,
                                onClick = {
                                    selectedSearchEngine = engine
                                    showSearchEngineDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(engine.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchEngineDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
                        // Top row - URL bar and main controls
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
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = if (canGoBack) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            IconButton(
                                onClick = { webViewRef?.goForward() },
                                enabled = canGoForward,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Forward",
                                    tint = if (canGoForward) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
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
                                        url = processUrl(inputUrl, selectedSearchEngine)
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
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            // Menu dropdown
                            Box {
                                IconButton(
                                    onClick = { showMenuDropdown = true },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                                        .size(40.dp)
                                ) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "Menu",
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                DropdownMenu(
                                    expanded = showMenuDropdown,
                                    onDismissRequest = { showMenuDropdown = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("History") },
                                        onClick = {
                                            showHistory = true
                                            showMenuDropdown = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.List, contentDescription = null)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Bookmarks") },
                                        onClick = {
                                            showBookmarks = true
                                            showMenuDropdown = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Bookmarks, contentDescription = null)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Downloads") },
                                        onClick = {
                                            if (downloadsEnabled) {
                                                Toast.makeText(context, "Downloads enabled. Files will be saved to Downloads folder.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Downloads are disabled. Enable in Settings.", Toast.LENGTH_SHORT).show()
                                            }
                                            showMenuDropdown = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Download,
                                                contentDescription = null,
                                                tint = if (downloadsEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Settings") },
                                        onClick = {
                                            showSettings = true
                                            showMenuDropdown = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Settings, contentDescription = null)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Search Engine: ${selectedSearchEngine.name}") },
                                        onClick = {
                                            showSearchEngineDialog = true
                                            showMenuDropdown = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Search, contentDescription = null)
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Quick access icons row
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { showHistory = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = "History",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    if (!bookmarksList.contains(url)) {
                                        bookmarksList.add(url)
                                        Toast.makeText(context, "Bookmark added", Toast.LENGTH_SHORT).show()
                                    } else {
                                        bookmarksList.remove(url)
                                        Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Icon(
                                    if (bookmarksList.contains(url)) Icons.Filled.Bookmarks else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { showSettings = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    if (downloadsEnabled) {
                                        Toast.makeText(context, "Downloads: Enabled", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Downloads: Disabled", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (downloadsEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Download,
                                    contentDescription = "Downloads",
                                    tint = if (downloadsEnabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
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
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false

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
                                pageTitle = view?.title ?: ""
                                canGoBack = view?.canGoBack() ?: false
                                canGoForward = view?.canGoForward() ?: false
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                progress = newProgress / 100f
                            }
                            
                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                pageTitle = title ?: ""
                            }
                        }

                        setDownloadListener { downloadUrl, userAgent, contentDisposition, mimetype, contentLength ->
                            if (downloadsEnabled) {
                                downloadFile(ctx, downloadUrl, userAgent, contentDisposition, mimetype)
                            } else {
                                Toast.makeText(ctx, "Downloads are disabled. Enable in Settings.", Toast.LENGTH_LONG).show()
                            }
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

fun processUrl(input: String, searchEngine: SearchEngine = SearchEngine.GOOGLE): String {
    val trimmed = input.trim()
    return if (Patterns.WEB_URL.matcher(trimmed).matches() || trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }
    } else {
        "${searchEngine.baseUrl}${Uri.encode(trimmed)}"
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
        Toast.makeText(context, "Failed to download file: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
