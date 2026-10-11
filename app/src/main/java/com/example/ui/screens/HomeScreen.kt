package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Chapter
import com.example.model.MangaTitle
import com.example.model.TelegramFeedItem
import com.example.ui.MainViewModel
import com.example.ui.components.TelegramDeliveryBadge
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledBlack
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val titles by viewModel.titles.collectAsState()
    val telegramFeed by viewModel.telegramFeed.collectAsState()
    val chaptersMap by viewModel.chapters.collectAsState()
    val selectedGenre by viewModel.selectedGenreFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val genres = listOf("Все", "Манга", "Манхва", "Рукопашный бой", "Романтика", "Киберпанк", "Фэнтези")

    val filteredTitles = titles.filter { title ->
        val matchesGenre = selectedGenre == "Все" || title.genres.contains(selectedGenre)
        val matchesSearch = searchQuery.isBlank() ||
                title.title.contains(searchQuery, ignoreCase = true) ||
                title.author.contains(searchQuery, ignoreCase = true)
        matchesGenre && matchesSearch
    }

    val queueTitles = titles.filter { it.inQueue }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Header Bar
        item {
            HeaderSection(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) }
            )
        }

        // Genre Filter Chips
        item {
            GenreFiltersRow(
                genres = genres,
                selectedGenre = selectedGenre,
                onGenreSelected = { viewModel.updateGenreFilter(it) }
            )
        }

        // Блок 1: Молниеносный свежак (Telegram Delivery)
        item {
            SectionHeader(
                title = "⚡ Молниеносный свежак",
                subtitle = "Новые выпуски от команд переводчиков прямо сейчас",
                badgeText = "СВЕЖИЕ ВЫПУСКИ",
                badgeColor = TelegramBlue
            )
        }

        item {
            TelegramFeedRow(
                feedItems = telegramFeed,
                onItemClick = { feedItem ->
                    val title = titles.find { it.id == feedItem.titleId }
                    val chapterList = chaptersMap[feedItem.titleId]
                    val chapter = chapterList?.find { it.chapterNumber == feedItem.chapterNumber }
                    if (title != null && chapter != null) {
                        viewModel.openChapter(chapter, title)
                    } else if (title != null) {
                        viewModel.openTitleDetails(title)
                    }
                }
            )
        }

        // Блок 2: Продолжить чтение (My Chill Queue) + Binge All
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "☕ Продолжить чтение",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Твоя очередь онгоингов (${queueTitles.size})",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { viewModel.startBingeQueue() },
                    modifier = Modifier.testTag("btn_binge_all"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberGreen,
                        contentColor = OledBlack
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Binge All",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Binge All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            ChillQueueRow(
                queueTitles = queueTitles,
                onTitleClick = { title ->
                    val chapterList = chaptersMap[title.id]
                    val unread = chapterList?.find { !it.isRead } ?: chapterList?.firstOrNull()
                    if (unread != null) {
                        viewModel.openChapter(unread, title)
                    } else {
                        viewModel.openTitleDetails(title)
                    }
                }
            )
        }

        // Блок 3: Популярное / Топ
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "🔥 Популярное в ChillReader",
                subtitle = "Самое читаемое и высоко оцененное",
                badgeText = "В ТОПЕ",
                badgeColor = NeonPurple
            )
        }

        items(filteredTitles) { title ->
            PopularTitleCard(
                title = title,
                onCardClick = { viewModel.openTitleDetails(title) },
                onBookmarkClick = { viewModel.toggleFavorite(title.id) }
            )
        }
    }
}

@Composable
private fun HeaderSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(listOf(NeonPurple, CyberGreen))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = "ChillReader",
                    tint = OledBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "CHILLREADER",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Поиск манги, манхвы или автора...", color = TextTertiary, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
    }
}

@Composable
private fun GenreFiltersRow(
    genres: List<String>,
    selectedGenre: String,
    onGenreSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        genres.forEach { genre ->
            val isSelected = genre == selectedGenre
            FilterChip(
                selected = isSelected,
                onClick = { onGenreSelected(genre) },
                label = {
                    Text(
                        text = genre,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonPurple,
                    selectedLabelColor = OledBlack,
                    containerColor = DarkSurfaceVariant,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) NeonPurple else DarkBorder
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Text(
            text = badgeText,
            color = badgeColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(badgeColor.copy(alpha = 0.12f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun TelegramFeedRow(
    feedItems: List<TelegramFeedItem>,
    onItemClick: (TelegramFeedItem) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(feedItems) { item ->
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                    .clickable { onItemClick(item) }
                    .testTag("tg_card_${item.id}")
            ) {
                Column {
                    // Cover Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                    ) {
                        Image(
                            painter = painterResource(id = item.coverRes),
                            contentDescription = item.titleName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, DarkCard)
                                    )
                                )
                        )
                        // Chapter Pill
                        Text(
                            text = "Гл. ${item.chapterNumber}",
                            color = OledBlack,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberGreen)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = item.titleName,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        TelegramDeliveryBadge(
                            teamName = item.tgChannel,
                            timeAgo = item.timeAgo
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChillQueueRow(
    queueTitles: List<MangaTitle>,
    onTitleClick: (MangaTitle) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(queueTitles) { title ->
            val progress = title.readChapters.toFloat() / title.totalChapters.coerceAtLeast(1)
            Row(
                modifier = Modifier
                    .width(280.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .border(1.dp, NeonPurple.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .clickable { onTitleClick(title) }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = title.coverRes),
                    contentDescription = title.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp, 76.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Прочитано ${title.readChapters} из ${title.totalChapters} глав",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = CyberGreen,
                        trackColor = DarkSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            color = CyberGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Читать ▶",
                            color = NeonPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PopularTitleCard(
    title: MangaTitle,
    onCardClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .clickable { onCardClick() }
            .padding(10.dp)
            .testTag("popular_card_${title.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = title.coverRes),
            contentDescription = title.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp, 96.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (title.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Избранное",
                        tint = if (title.isFavorite) NeonPurple else TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = title.originalTitle,
                color = TextTertiary,
                fontSize = 11.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Рейтинг",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${title.rating}",
                        color = Color(0xFFFFC107),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "• ${title.status}",
                    color = CyberGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "• ${title.totalChapters} глав",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                title.genres.take(2).forEach { genre ->
                    Text(
                        text = genre,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
