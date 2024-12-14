package ru.gorbulevsv.composepraybook

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import ru.gorbulevsv.composepraybook.ui.theme.ComposePrayBookTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    var routes = mutableListOf<Pair<String, String>>(
        Pair("Молитвы утренние", "1.html"),
        Pair("Молитвы вечерние", "2.html"),
        Pair("Канон покаянный ко Господу Иисусу Христу", "3.html"),
        Pair("Канон молебный ко Пресвятой Богородице", "4.html"),
        Pair("Канон Ангелу Хранителю", "5.html"),
        Pair("Акафист Иисусу Христу", "6.html"),
        Pair("Акафист Божией Матери", "7.html"),
        Pair("Акафист святителю Николаю", "8.html"),
        Pair("Последование ко Святому Причащению", "9.html"),
        Pair("Часы Святой Пасхи", "10.html"),
    )
    var selectedRoute = mutableStateOf(routes[0])
    var isCatalogShow = mutableStateOf<Boolean>(false)

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposePrayBookTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            title = {
                                Text(
                                    text = selectedRoute.value.first,
                                    fontWeight = FontWeight.Bold
                                )
                            })
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            onClick = {
                            isCatalogShow.value = true
                        }) {
                            Icon(Icons.Filled.List, "Оглавление")
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    AndroidView(
                        modifier = Modifier.padding((innerPadding)),
                        factory = { context ->
                            return@AndroidView WebView(context).apply {
                                settings.javaScriptEnabled = true
                                webViewClient = WebViewClient()

                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true
                                settings.setSupportZoom(false)
                            }
                        },
                        update = {
                            it.loadUrl("file:///android_asset/" + selectedRoute.value.second)
                        }
                    )
                    BottomPanel(
                        title = "Оглавление",
                        isShow = isCatalogShow,
                        isOpenToFullScreen = true
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        ) {
                            for (item in routes) {
                                val i = routes.indexOf(item)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = {
                                            selectedRoute.value = routes[i]
                                            isCatalogShow.value = false
                                        })
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier
                                            .padding(5.dp, 5.dp)
                                            .fillMaxWidth()

                                    ) {
                                        Text(
                                            buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append((i + 1).toString())
                                                    append(". ")
                                                }
                                                append(item.first)
                                            },
                                            fontSize = 18.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomPanel(
    title: String = "",
    isShow: MutableState<Boolean>,
    isOpenToFullScreen: Boolean = false,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = isOpenToFullScreen,
    )
    if (isShow.value) {
        ModalBottomSheet(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            shape = RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp),
            modifier = modifier,
            sheetState = sheetState,
            onDismissRequest = { isShow.value = false }
        ) {
            Column(
                modifier = Modifier.padding(10.dp, 0.dp, 20.dp, 40.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = title.toUpperCase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp)
                )
                content()
            }
        }
    }
}