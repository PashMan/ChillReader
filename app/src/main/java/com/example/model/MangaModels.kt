package com.example.model

data class MangaTitle(
    val id: String,
    val title: String,
    val originalTitle: String,
    val author: String,
    val synopsis: String,
    val status: String, // "Онгоинг" or "Завершен"
    val genres: List<String>,
    val coverRes: Int,
    val rating: Float,
    val totalChapters: Int,
    val readChapters: Int,
    val tgTeams: List<String>,
    val inQueue: Boolean = false,
    val isFavorite: Boolean = false,
    val latestChapter: String,
    val updatedAt: String
)

data class ChapterPage(
    val pageNumber: Int,
    val drawableRes: Int? = null,
    val panelTitle: String = "",
    val panelDialogue: String = "",
    val actionDescription: String = "",
    val watermarkText: String? = null
)

data class Chapter(
    val id: String,
    val titleId: String,
    val chapterNumber: Int,
    val title: String,
    val releaseTime: String,
    val tgTeam: String,
    val pages: List<ChapterPage>,
    val isRead: Boolean = false,
    val readProgress: Float = 0f,
    val r2LatencyMs: Int = 14,
    val r2Cached: Boolean = true
)

data class TelegramFeedItem(
    val id: String,
    val titleId: String,
    val titleName: String,
    val chapterNumber: Int,
    val tgChannel: String,
    val timeAgo: String,
    val r2SyncSpeedSec: Float = 0.4f,
    val coverRes: Int
)

data class HandsFreeConfig(
    val enabled: Boolean = false,
    val sensitivity: Float = 0.65f, // 0.1 to 1.0
    val scrollStepPx: Int = 480,
    val batterySaver8Fps: Boolean = true,
    val showCameraPip: Boolean = true,
    val autoPauseLookAway: Boolean = true,
    val invertDirection: Boolean = false
)

data class ChillStats(
    val hoursSaved: Int = 48,
    val chaptersRead: Int = 320,
    val headNodsDetected: Int = 1420,
    val r2BandwidthSavedGb: Float = 18.4f,
    val currentStreakDays: Int = 14
)
