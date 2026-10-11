package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MangaRepository
import com.example.handsfree.HandsFreeController
import com.example.handsfree.NodGestureState
import com.example.model.Chapter
import com.example.model.HandsFreeConfig
import com.example.model.MangaTitle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab {
    HOME,
    CATALOG,
    READER,
    PROFILE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MangaRepository()
    val handsFreeController = HandsFreeController(application)

    val titles = repository.titles
    val telegramFeed = repository.telegramFeed
    val chapters = repository.chapters
    val handsFreeConfig = repository.handsFreeConfig
    val stats = repository.stats
    val cacheSizeBytes = repository.cacheSizeBytes

    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _selectedTitle = MutableStateFlow<MangaTitle?>(null)
    val selectedTitle: StateFlow<MangaTitle?> = _selectedTitle.asStateFlow()

    private val _currentChapter = MutableStateFlow<Chapter?>(null)
    val currentChapter: StateFlow<Chapter?> = _currentChapter.asStateFlow()

    private val _isBingeMode = MutableStateFlow(false)
    val isBingeMode: StateFlow<Boolean> = _isBingeMode.asStateFlow()

    private val _isAiCleanEnabled = MutableStateFlow(true)
    val isAiCleanEnabled: StateFlow<Boolean> = _isAiCleanEnabled.asStateFlow()

    private val _selectedGenreFilter = MutableStateFlow("Все")
    val selectedGenreFilter: StateFlow<String> = _selectedGenreFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _scrollTriggerFlow = MutableSharedFlow<Int>(extraBufferCapacity = 5)
    val scrollTriggerFlow: SharedFlow<Int> = _scrollTriggerFlow.asSharedFlow()

    val isHandsFreeTracking = handsFreeController.isTracking
    val isAutoScrolling = handsFreeController.isAutoScrolling
    val currentPitch = handsFreeController.currentPitch

    init {
        // Listen to nod gestures
        viewModelScope.launch {
            handsFreeController.nodEvents.collect { state ->
                when (state) {
                    NodGestureState.SCROLLING_ACTIVE -> {
                        repository.incrementHeadNodCount()
                        _scrollTriggerFlow.tryEmit(handsFreeConfig.value.scrollStepPx)
                    }
                    NodGestureState.PAUSED -> {
                        // Pause auto-scroll
                    }
                    else -> Unit
                }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun openTitleDetails(title: MangaTitle) {
        _selectedTitle.value = title
    }

    fun closeTitleDetails() {
        _selectedTitle.value = null
    }

    fun openChapter(chapter: Chapter, title: MangaTitle, isBinge: Boolean = false) {
        _selectedTitle.value = title
        _currentChapter.value = chapter
        _isBingeMode.value = isBinge
        _currentTab.value = AppTab.READER
    }

    fun startBingeQueue() {
        val queueTitles = titles.value.filter { it.inQueue }
        if (queueTitles.isNotEmpty()) {
            val firstTitle = queueTitles.first()
            val titleChapters = chapters.value[firstTitle.id]
            val unreadChapter = titleChapters?.find { !it.isRead } ?: titleChapters?.firstOrNull()
            if (unreadChapter != null) {
                openChapter(unreadChapter, firstTitle, isBinge = true)
            }
        }
    }

    fun closeReader() {
        handsFreeController.stopTracking()
        _currentTab.value = AppTab.HOME
    }

    fun nextChapter() {
        val current = _currentChapter.value ?: return
        val currentTitleId = current.titleId
        val chapterList = chapters.value[currentTitleId] ?: return
        val currentIndex = chapterList.indexOfFirst { it.id == current.id }
        if (currentIndex > 0) { // Chapters are sorted descending (latest first)
            val nextCh = chapterList[currentIndex - 1]
            _selectedTitle.value?.let { title ->
                openChapter(nextCh, title, _isBingeMode.value)
            }
        } else if (_isBingeMode.value) {
            // Find next title in queue
            val queueTitles = titles.value.filter { it.inQueue }
            val nextTitle = queueTitles.find { it.id != currentTitleId }
            if (nextTitle != null) {
                val nextChList = chapters.value[nextTitle.id]
                val nextCh = nextChList?.lastOrNull()
                if (nextCh != null) {
                    openChapter(nextCh, nextTitle, isBinge = true)
                }
            }
        }
    }

    fun prevChapter() {
        val current = _currentChapter.value ?: return
        val currentTitleId = current.titleId
        val chapterList = chapters.value[currentTitleId] ?: return
        val currentIndex = chapterList.indexOfFirst { it.id == current.id }
        if (currentIndex < chapterList.size - 1 && currentIndex >= 0) {
            val prevCh = chapterList[currentIndex + 1]
            _selectedTitle.value?.let { title ->
                openChapter(prevCh, title, _isBingeMode.value)
            }
        }
    }

    fun toggleAiClean() {
        _isAiCleanEnabled.update { !it }
    }

    fun toggleHandsFree() {
        val currentEnabled = handsFreeConfig.value.enabled
        val newEnabled = !currentEnabled
        repository.updateHandsFreeConfig { it.copy(enabled = newEnabled) }
        if (newEnabled) {
            handsFreeController.startTracking()
        } else {
            handsFreeController.stopTracking()
        }
    }

    fun triggerSimulatorNod() {
        handsFreeController.triggerManualNod()
    }

    fun updateGenreFilter(genre: String) {
        _selectedGenreFilter.value = genre
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleQueue(titleId: String) {
        repository.toggleQueue(titleId)
        if (_selectedTitle.value?.id == titleId) {
            _selectedTitle.update { it?.copy(inQueue = !it.inQueue) }
        }
    }

    fun toggleFavorite(titleId: String) {
        repository.toggleFavorite(titleId)
        if (_selectedTitle.value?.id == titleId) {
            _selectedTitle.update { it?.copy(isFavorite = !it.isFavorite) }
        }
    }

    fun updateHandsFreeSensitivity(sensitivity: Float) {
        repository.updateHandsFreeConfig { it.copy(sensitivity = sensitivity) }
    }

    fun updateHandsFreeStep(stepPx: Int) {
        repository.updateHandsFreeConfig { it.copy(scrollStepPx = stepPx) }
    }

    fun toggleBatterySaver8Fps() {
        repository.updateHandsFreeConfig { it.copy(batterySaver8Fps = !it.batterySaver8Fps) }
    }

    fun clearCache() {
        repository.clearCache()
    }

    override fun onCleared() {
        super.onCleared()
        handsFreeController.stopTracking()
    }
}
