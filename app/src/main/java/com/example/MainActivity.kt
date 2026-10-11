package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.ChillBottomBar
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.TitleDetailScreen
import com.example.ui.theme.ChillReaderTheme
import com.example.ui.theme.OledBlack

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChillReaderTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedTitle by viewModel.selectedTitle.collectAsState()
    val currentChapter by viewModel.currentChapter.collectAsState()

    val isReaderActive = currentTab == AppTab.READER && currentChapter != null

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack),
        bottomBar = {
            // Hide bottom bar when reading for pure OLED canvas immersion
            if (!isReaderActive) {
                ChillBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        if (tab == AppTab.READER) {
                            // If user clicks reader tab, open last active or first unread chapter
                            val titles = viewModel.titles.value
                            val firstTitle = titles.firstOrNull()
                            val chapters = firstTitle?.let { viewModel.chapters.value[it.id] }
                            val targetChapter = chapters?.firstOrNull()
                            if (firstTitle != null && targetChapter != null) {
                                viewModel.openChapter(targetChapter, firstTitle)
                            } else {
                                viewModel.selectTab(tab)
                            }
                        } else {
                            viewModel.selectTab(tab)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isReaderActive) 0.dp else innerPadding.calculateBottomPadding())
        ) {
            when {
                // Core Reader Experience
                isReaderActive && currentChapter != null && selectedTitle != null -> {
                    ReaderScreen(
                        chapter = currentChapter!!,
                        title = selectedTitle!!,
                        viewModel = viewModel
                    )
                }

                // Title Details Screen
                selectedTitle != null -> {
                    TitleDetailScreen(
                        title = selectedTitle!!,
                        viewModel = viewModel
                    )
                }

                // Main Tab Screens
                else -> {
                    when (currentTab) {
                        AppTab.HOME -> HomeScreen(viewModel = viewModel)
                        AppTab.CATALOG -> CatalogScreen(viewModel = viewModel)
                        AppTab.READER -> {
                            // Fallback if reader tab opened without title selected
                            HomeScreen(viewModel = viewModel)
                        }
                        AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
