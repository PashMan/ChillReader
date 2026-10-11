package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChillStats
import com.example.model.HandsFreeConfig
import com.example.ui.MainViewModel
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

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.stats.collectAsState()
    val handsFreeConfig by viewModel.handsFreeConfig.collectAsState()
    val cacheSizeBytes by viewModel.cacheSizeBytes.collectAsState()

    val cacheMb = cacheSizeBytes / (1024 * 1024)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OledBlack),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // User Profile Header
        item {
            ProfileHeaderSection()
        }

        // Статистика чилла
        item {
            ChillStatsSection(stats = stats)
        }

        // Chill Pass Banner
        item {
            ChillPassSection()
        }

        // Настройки Hands-Free скролла
        item {
            HandsFreeSettingsSection(
                config = handsFreeConfig,
                onSensitivityChange = { viewModel.updateHandsFreeSensitivity(it) },
                onStepChange = { viewModel.updateHandsFreeStep(it) },
                onToggleBatterySaver = { viewModel.toggleBatterySaver8Fps() }
            )
        }

        // Хранилище и кэш
        item {
            StorageSection(
                cacheMb = cacheMb,
                onClearCache = { viewModel.clearCache() }
            )
        }

        // Telegram каналы
        item {
            TelegramPipelineSection()
        }
    }
}

@Composable
private fun ProfileHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(NeonPurple, CyberGreen))
                )
                .border(2.dp, CyberGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Аватар",
                tint = OledBlack,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Chill Master • Pasha",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "CHILL PASS ACTIVE ⚡",
                    color = CyberGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun ChillStatsSection(stats: ChillStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "📊 Статистика чилла",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Твои рекорды чтения в глубокой ночи",
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Сэкономлено времени",
                value = "${stats.hoursSaved} ч",
                accentColor = CyberGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Прочитано глав",
                value = "${stats.chaptersRead}",
                accentColor = NeonPurple,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Скроллов кивком",
                value = "${stats.headNodsDetected}",
                accentColor = CyberCyan,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Пролистано страниц",
                value = "1,840 стр.",
                accentColor = Color(0xFFFFB800),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = value,
                color = accentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ChillPassSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF1E0C30),
                        Color(0xFF0F1A1B)
                    )
                )
            )
            .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = CyberGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Chill Pass Premium",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = "PRO LIFETIME",
                    color = CyberGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PassPerkItem("🚀 Мгновенная загрузка страниц без задержек")
                PassPerkItem("📥 Оффлайн-скачивание глав целыми томами в 1 тап")
                PassPerkItem("✨ Чтение без рекламы и водяных знаков команд")
                PassPerkItem("⚡ Быстрое получение новых глав сразу после выхода")
            }
        }
    }
}

@Composable
private fun PassPerkItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = CyberGreen,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun HandsFreeSettingsSection(
    config: HandsFreeConfig,
    onSensitivityChange: (Float) -> Unit,
    onStepChange: (Int) -> Unit,
    onToggleBatterySaver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = CyberGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Настройки скролла кивком",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sensitivity slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Чувствительность кивков",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = "${(config.sensitivity * 100).toInt()}%",
                color = CyberGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = config.sensitivity,
            onValueChange = onSensitivityChange,
            valueRange = 0.2f..1.0f,
            colors = SliderDefaults.colors(
                thumbColor = CyberGreen,
                activeTrackColor = CyberGreen,
                inactiveTrackColor = DarkSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Battery saver toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Экономный режим батареи",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Телефон остается холодным, батарея не тает под одеялом",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            Switch(
                checked = config.batterySaver8Fps,
                onCheckedChange = { onToggleBatterySaver() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OledBlack,
                    checkedTrackColor = CyberGreen,
                    uncheckedThumbColor = TextTertiary,
                    uncheckedTrackColor = DarkSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun StorageSection(
    cacheMb: Long,
    onClearCache: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Память и кэш изображений",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (cacheMb > 0) "Занято на устройстве: $cacheMb МБ" else "Кэш чист (0 МБ)",
                    color = if (cacheMb > 0) TextSecondary else CyberGreen,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onClearCache,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkSurfaceVariant,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_clear_cache")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = NeonPink,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Очистить кэш", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TelegramPipelineSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = TelegramBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "⚡ Каналы переводчиков в Telegram",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "12 КАНАЛОВ НА СВЯЗИ",
                    color = CyberGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Приложение напрямую отслеживает публикации в каналах лучших команд перевода и сразу открывает новые главы.",
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}
