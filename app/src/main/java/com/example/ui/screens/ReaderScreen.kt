package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Chapter
import com.example.model.ChapterPage
import com.example.model.MangaTitle
import com.example.ui.MainViewModel
import com.example.ui.components.FastLoadBadge
import com.example.ui.components.TelegramDeliveryBadge
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledBlack
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReaderScreen(
    chapter: Chapter,
    title: MangaTitle,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.closeReader()
    }

    var showHud by remember { mutableStateOf(false) }
    val isAiClean by viewModel.isAiCleanEnabled.collectAsState()
    val handsFreeConfig by viewModel.handsFreeConfig.collectAsState()
    val isTracking by viewModel.isHandsFreeTracking.collectAsState()
    val isAutoScrolling by viewModel.isAutoScrolling.collectAsState()
    val currentPitch by viewModel.currentPitch.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Smooth auto-scroll listener from ViewModel
    LaunchedEffect(Unit) {
        viewModel.scrollTriggerFlow.collect { stepPx ->
            coroutineScope.launch {
                listState.animateScrollBy(stepPx.toFloat())
            }
        }
    }

    // Continuous auto-scroll loop when scrolling is active
    LaunchedEffect(isAutoScrolling) {
        if (isAutoScrolling) {
            while (isAutoScrolling) {
                listState.animateScrollBy(260f)
                delay(1200L)
            }
        }
    }

    val totalPages = chapter.pages.size
    val currentVisiblePage by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex + 1).coerceIn(1, totalPages)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        // Seamless Webtoon Scroll: Zero-gap vertical strip
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showHud = !showHud
                }
                .testTag("webtoon_scroll_container")
        ) {
            itemsIndexed(chapter.pages) { index, page ->
                WebtoonPagePanel(
                    page = page,
                    isAiClean = isAiClean,
                    pageIndex = index,
                    totalPages = totalPages
                )
            }

            // Chapter Footer
            item {
                ChapterFooterBanner(
                    chapter = chapter,
                    onNextChapter = { viewModel.nextChapter() },
                    onPrevChapter = { viewModel.prevChapter() }
                )
            }
        }

        // Floating Hands-Free Status Banner (Visible when active)
        if (handsFreeConfig.enabled) {
            HandsFreeActiveOverlay(
                isAutoScrolling = isAutoScrolling,
                batterySaver8Fps = handsFreeConfig.batterySaver8Fps,
                currentPitch = currentPitch,
                onManualNod = { viewModel.triggerSimulatorNod() },
                onToggleHud = { showHud = !showHud },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 10.dp)
            )
        }

        // Corner Telegram Badge indicator
        if (!showHud) {
            TelegramDeliveryBadge(
                teamName = chapter.tgTeam,
                timeAgo = chapter.releaseTime,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 8.dp, end = 12.dp)
            )
        }

        // Tap HUD Overlay
        AnimatedVisibility(
            visible = showHud,
            enter = fadeIn() + slideInVertically { -it / 2 },
            exit = fadeOut() + slideOutVertically { -it / 2 },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            ReaderTopBar(
                titleName = title.title,
                chapterTitle = chapter.title,
                r2LatencyMs = chapter.r2LatencyMs,
                onClose = { viewModel.closeReader() }
            )
        }

        AnimatedVisibility(
            visible = showHud,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ReaderBottomBar(
                currentPage = currentVisiblePage,
                totalPages = totalPages,
                isAiClean = isAiClean,
                isHandsFree = handsFreeConfig.enabled,
                onSliderChange = { pageIndex ->
                    coroutineScope.launch {
                        listState.scrollToItem((pageIndex - 1).coerceAtLeast(0))
                    }
                },
                onToggleAiClean = { viewModel.toggleAiClean() },
                onToggleHandsFree = { viewModel.toggleHandsFree() },
                onPrev = { viewModel.prevChapter() },
                onNext = { viewModel.nextChapter() }
            )
        }
    }
}

@Composable
private fun WebtoonPagePanel(
    page: ChapterPage,
    isAiClean: Boolean,
    pageIndex: Int,
    totalPages: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OledBlack)
    ) {
        // High-res Image / Illustrated Webtoon Strip
        if (page.drawableRes != null) {
            Image(
                painter = painterResource(id = page.drawableRes),
                contentDescription = "Страница ${page.pageNumber}",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Atmospheric Narrative Strip (Simulating continuous seamless webtoon reading)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            OledBlack,
                            DarkSurfaceVariant,
                            OledBlack
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Panel SFX / Title Header
                Text(
                    text = page.panelTitle,
                    color = CyberGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Dialogue Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = page.panelDialogue,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = page.actionDescription,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Watermark Banner (Subject to AI Clean!)
                if (page.watermarkText != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    if (isAiClean) {
                        // AI Clean Active Notice
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurface)
                                .border(1.dp, CyberGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CyberGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "✨ Чистый вид: Реклама скрыта",
                                color = CyberGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Original Watermark
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonPink.copy(alpha = 0.15f))
                                .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(vertical = 6.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = page.watermarkText,
                                color = NeonPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Page numbering marker
                Text(
                    text = "— ${pageIndex + 1} / $totalPages —",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun ChapterFooterBanner(
    chapter: Chapter,
    onNextChapter: () -> Unit,
    onPrevChapter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎉 Вы прочитали ${chapter.title}",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Приятного чтения! Следующая глава доступна ниже.",
            color = CyberGreen,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onPrevChapter,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkSurfaceVariant,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.NavigateBefore, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Пред. глава", fontSize = 12.sp)
            }

            Button(
                onClick = onNextChapter,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberGreen,
                    contentColor = OledBlack
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("След. глава", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun HandsFreeActiveOverlay(
    isAutoScrolling: Boolean,
    batterySaver8Fps: Boolean,
    currentPitch: Float,
    onManualNod: () -> Unit,
    onToggleHud: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(OledBlack.copy(alpha = 0.85f))
            .border(1.dp, CyberGreen, RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Glowing sensor dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isAutoScrolling) CyberGreen else NeonPurple)
        )

        Column {
            Text(
                text = if (isAutoScrolling) "Авто-скролл активен" else "Кивните для скролла",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (batterySaver8Fps) "Экономный режим батареи" else "Стандартный режим",
                color = CyberGreen,
                fontSize = 9.sp
            )
        }

        // Test nod trigger button
        Button(
            onClick = onManualNod,
            modifier = Modifier.height(28.dp).testTag("btn_trigger_nod"),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAutoScrolling) NeonPink else CyberGreen,
                contentColor = OledBlack
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (isAutoScrolling) "Пауза" else "Кивок 👤",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun ReaderTopBar(
    titleName: String,
    chapterTitle: String,
    r2LatencyMs: Int,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface.copy(alpha = 0.95f))
            .border(1.dp, DarkBorder, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Закрыть читалку",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = titleName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = chapterTitle,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            FastLoadBadge()
        }
    }
}

@Composable
private fun ReaderBottomBar(
    currentPage: Int,
    totalPages: Int,
    isAiClean: Boolean,
    isHandsFree: Boolean,
    onSliderChange: (Int) -> Unit,
    onToggleAiClean: () -> Unit,
    onToggleHandsFree: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface.copy(alpha = 0.95f))
            .border(1.dp, DarkBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Page Progress Slider & Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Стр. $currentPage из $totalPages",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Плавное чтение",
                    color = CyberGreen,
                    fontSize = 11.sp
                )
            }
        }

        Slider(
            value = currentPage.toFloat(),
            onValueChange = { onSliderChange(it.toInt()) },
            valueRange = 1f..totalPages.toFloat().coerceAtLeast(1f),
            colors = SliderDefaults.colors(
                thumbColor = CyberGreen,
                activeTrackColor = CyberGreen,
                inactiveTrackColor = DarkSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Actions Row: Prev, AI Clean, AI Hands-Free, Next
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrev,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.NavigateBefore,
                    contentDescription = "Предыдущая глава",
                    tint = TextPrimary
                )
            }

            // AI Clean Button -> Чистый вид
            Button(
                onClick = onToggleAiClean,
                modifier = Modifier.testTag("btn_ai_clean"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAiClean) NeonPurple else DarkSurfaceVariant,
                    contentColor = if (isAiClean) OledBlack else TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAiClean) "Чистый вид: ВКЛ" else "Чистый вид",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Hands-Free Button -> Скролл кивком
            Button(
                onClick = onToggleHandsFree,
                modifier = Modifier.testTag("btn_hands_free"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isHandsFree) CyberGreen else DarkSurfaceVariant,
                    contentColor = if (isHandsFree) OledBlack else TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHandsFree) "Скролл кивком" else "Скролл кивком",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = "Следующая глава",
                    tint = TextPrimary
                )
            }
        }
    }
}
