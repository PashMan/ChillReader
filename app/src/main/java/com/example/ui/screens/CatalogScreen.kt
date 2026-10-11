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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MangaTitle
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OledBlack
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun CatalogScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val titles by viewModel.titles.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGenre by viewModel.selectedGenreFilter.collectAsState()

    var selectedStatus by remember { mutableStateOf("Все") }
    var selectedSort by remember { mutableStateOf("По популярности") }

    val genres = listOf("Все", "Манга", "Манхва", "Рукопашный бой", "Романтика", "Киберпанк", "Фэнтези")
    val statuses = listOf("Все", "Онгоинг", "Завершен")

    val filteredTitles = titles.filter { title ->
        val matchesGenre = selectedGenre == "Все" || title.genres.contains(selectedGenre)
        val matchesStatus = selectedStatus == "Все" || title.status == selectedStatus
        val matchesSearch = searchQuery.isBlank() ||
                title.title.contains(searchQuery, ignoreCase = true) ||
                title.author.contains(searchQuery, ignoreCase = true) ||
                title.genres.any { it.contains(searchQuery, ignoreCase = true) }
        matchesGenre && matchesStatus && matchesSearch
    }.sortedWith { a, b ->
        when (selectedSort) {
            "По рейтингу" -> b.rating.compareTo(a.rating)
            "По главам" -> b.totalChapters.compareTo(a.totalChapters)
            else -> b.rating.compareTo(a.rating)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        // Top Bar & Search
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "📚 Каталог тайтлов",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Тысячи глав любимой манги и манхвы",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Искать в каталоге...", color = TextTertiary, fontSize = 13.sp) },
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
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
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
                    .testTag("catalog_search_input"),
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

        // Genres Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genres.forEach { genre ->
                val isSelected = genre == selectedGenre
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateGenreFilter(genre) },
                    label = { Text(genre, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonPurple,
                        selectedLabelColor = OledBlack,
                        containerColor = DarkSurfaceVariant,
                        labelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Status Tabs & Results Counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                statuses.forEach { status ->
                    val isSelected = status == selectedStatus
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) CyberGreen.copy(alpha = 0.2f) else DarkSurfaceVariant)
                            .border(1.dp, if (isSelected) CyberGreen else DarkBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedStatus = status }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = status,
                            color = if (isSelected) CyberGreen else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Text(
                text = "${filteredTitles.size} тайтлов найдено",
                color = TextTertiary,
                fontSize = 11.sp
            )
        }

        // Grid of Titles
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredTitles) { title ->
                CatalogGridCard(
                    title = title,
                    onCardClick = { viewModel.openTitleDetails(title) },
                    onBookmarkClick = { viewModel.toggleFavorite(title.id) }
                )
            }
        }
    }
}

@Composable
private fun CatalogGridCard(
    title: MangaTitle,
    onCardClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .clickable { onCardClick() }
            .testTag("catalog_card_${title.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = title.coverRes),
                contentDescription = title.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Rating Pill
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(OledBlack.copy(alpha = 0.8f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${title.rating}",
                    color = Color(0xFFFFC107),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Favorite Button
            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(28.dp)
                    .background(OledBlack.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            ) {
                Icon(
                    imageVector = if (title.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "В закладки",
                    tint = if (title.isFavorite) NeonPurple else TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Status Badge
            Text(
                text = "${title.totalChapters} гл.",
                color = OledBlack,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CyberGreen)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            )
        }

        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title.title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = title.author,
                color = TextTertiary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title.genres.take(2).joinToString(" • "),
                color = NeonPurple,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
