package com.example.data

import com.example.R
import com.example.model.Chapter
import com.example.model.ChapterPage
import com.example.model.ChillStats
import com.example.model.HandsFreeConfig
import com.example.model.MangaTitle
import com.example.model.TelegramFeedItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MangaRepository {

    private val _titles = MutableStateFlow<List<MangaTitle>>(emptyList())
    val titles: StateFlow<List<MangaTitle>> = _titles.asStateFlow()

    private val _telegramFeed = MutableStateFlow<List<TelegramFeedItem>>(emptyList())
    val telegramFeed: StateFlow<List<TelegramFeedItem>> = _telegramFeed.asStateFlow()

    private val _chapters = MutableStateFlow<Map<String, List<Chapter>>>(emptyMap())
    val chapters: StateFlow<Map<String, List<Chapter>>> = _chapters.asStateFlow()

    private val _handsFreeConfig = MutableStateFlow(HandsFreeConfig())
    val handsFreeConfig: StateFlow<HandsFreeConfig> = _handsFreeConfig.asStateFlow()

    private val _stats = MutableStateFlow(ChillStats())
    val stats: StateFlow<ChillStats> = _stats.asStateFlow()

    private val _cacheSizeBytes = MutableStateFlow(412_500_000L) // ~412 MB
    val cacheSizeBytes: StateFlow<Long> = _cacheSizeBytes.asStateFlow()

    init {
        initSampleData()
    }

    private fun initSampleData() {
        val soloTitle = MangaTitle(
            id = "solo_monarch",
            title = "Монарх Теней: Возрождение",
            originalTitle = "그림자 군주: 헌터의 연대기",
            author = "Чхугон / DUBU (REDICE)",
            synopsis = "Спустя 10 лет после открытия таинственных Врат, охотник E-ранга Сон Джин-у сталкивается со смертельным испытанием в Двойном Подземелье. Получив скрытую Систему Тёмного Владыки, он начинает бесконечное восхождение к вершине мирового ранга.",
            status = "Онгоинг",
            genres = listOf("Манхва", "Рукопашный бой", "Фэнтези", "Система", "Экшен"),
            coverRes = R.drawable.img_cover_solo_monarch,
            rating = 4.96f,
            totalChapters = 184,
            readChapters = 142,
            tgTeams = listOf("⚡ ShadowScans TG", "⚡ Remanga FastPass", "⚡ AsuraGuild Official"),
            inQueue = true,
            isFavorite = true,
            latestChapter = "Глава 184: Зов Врат Бездны",
            updatedAt = "3 мин назад"
        )

        val cyberClawsTitle = MangaTitle(
            id = "cyber_fist",
            title = "Неоновый Кулак: Пробуждение 2077",
            originalTitle = "네온권사: 영웅의 각성",
            author = "Кан Хён / Studio CyberLine",
            synopsis = "Неоновый Сеул погрузился во тьму мегакорпораций и синдикатов Мурима. Молодой курьер Кан обретает древнее боевое искусство циркуляции энергии Ци сквозь кибернетические импланты. Никакого оружия — только взрывная мощь неонового кулака.",
            status = "Онгоинг",
            genres = listOf("Манхва", "Рукопашный бой", "Киберпанк", "Боевые искусства", "Сёнэн"),
            coverRes = R.drawable.img_cover_cyber_claws,
            rating = 4.91f,
            totalChapters = 68,
            readChapters = 45,
            tgTeams = listOf("⚡ CyberTeam Scans [TG]", "⚡ NeonRaw FastDrop"),
            inQueue = true,
            isFavorite = true,
            latestChapter = "Глава 68: Прорыв Сквозь Небоскребы",
            updatedAt = "12 мин назад"
        )

        val bladeTitle = MangaTitle(
            id = "blade_void",
            title = "Клинок Пустоты: Возвращение Мечника",
            originalTitle = "허공의 검: 검귀의 귀환",
            author = "Ли Сон / RedArt",
            synopsis = "Величайший мечник пал в битве с демонами Бездны и переродился 300 лет спустя в теле безнадежного наследника падшего клана. С ним осталась лишь память о первородном дыхании клинка.",
            status = "Онгоинг",
            genres = listOf("Манхва", "Рукопашный бой", "Перерождение", "Фэнтези"),
            coverRes = R.drawable.img_cover_solo_monarch,
            rating = 4.88f,
            totalChapters = 112,
            readChapters = 20,
            tgTeams = listOf("⚡ MangaBuff Team [TG]", "⚡ BladeTranslation TG"),
            inQueue = true,
            isFavorite = false,
            latestChapter = "Глава 112: Дыхание Черной Стали",
            updatedAt = "28 мин назад"
        )

        val romanceTitle = MangaTitle(
            id = "villainess_chill",
            title = "Злодейка хочет лишь спокойно почитать",
            originalTitle = "악녀는 그냥 쉬고 싶다",
            author = "Пак Хе / MoonLight",
            synopsis = "После 100 перерождений в роли трагической злодейки романтических новелл, герцогиня решает послать сюжет ко всем чертям. Она запирается в библиотеке с чаем и лучшей мангой империи, но главные герои упорно ломятся в её двери.",
            status = "Завершен",
            genres = listOf("Манхва", "Романтика", "Комедия", "Отрезок жизни"),
            coverRes = R.drawable.img_cover_cyber_claws,
            rating = 4.85f,
            totalChapters = 90,
            readChapters = 90,
            tgTeams = listOf("⚡ OtomeScans TG", "⚡ SweetDrop TG"),
            inQueue = false,
            isFavorite = true,
            latestChapter = "Экстра 4: Финал в Оранжерее",
            updatedAt = "1 день назад"
        )

        _titles.value = listOf(soloTitle, cyberClawsTitle, bladeTitle, romanceTitle)

        _telegramFeed.value = listOf(
            TelegramFeedItem(
                id = "tf_1",
                titleId = "solo_monarch",
                titleName = "Монарх Теней: Возрождение",
                chapterNumber = 184,
                tgChannel = "ShadowScans VIP",
                timeAgo = "3 мин назад",
                r2SyncSpeedSec = 0.4f,
                coverRes = R.drawable.img_cover_solo_monarch
            ),
            TelegramFeedItem(
                id = "tf_2",
                titleId = "cyber_fist",
                titleName = "Неоновый Кулак: Пробуждение 2077",
                chapterNumber = 68,
                tgChannel = "CyberTeam Scans",
                timeAgo = "12 мин назад",
                r2SyncSpeedSec = 0.35f,
                coverRes = R.drawable.img_cover_cyber_claws
            ),
            TelegramFeedItem(
                id = "tf_3",
                titleId = "blade_void",
                titleName = "Клинок Пустоты: Возвращение Мечника",
                chapterNumber = 112,
                tgChannel = "MangaBuff TG",
                timeAgo = "28 мин назад",
                r2SyncSpeedSec = 0.5f,
                coverRes = R.drawable.img_cover_solo_monarch
            )
        )

        // Prepopulate chapters for titles
        val soloChapters = (184 downTo 180).map { chNum ->
            Chapter(
                id = "solo_ch_$chNum",
                titleId = "solo_monarch",
                chapterNumber = chNum,
                title = "Глава $chNum: ${if (chNum == 184) "Зов Врат Бездны" else "Битва у Цитадели"}",
                releaseTime = if (chNum == 184) "3 мин назад" else "Вчера",
                tgTeam = "⚡ ShadowScans TG",
                pages = generateSoloPages(chNum),
                isRead = chNum < 184,
                readProgress = if (chNum < 184) 1.0f else 0.25f,
                r2LatencyMs = 12
            )
        }

        val cyberChapters = (68 downTo 64).map { chNum ->
            Chapter(
                id = "cyber_ch_$chNum",
                titleId = "cyber_fist",
                chapterNumber = chNum,
                title = "Глава $chNum: ${if (chNum == 68) "Прорыв Сквозь Небоскребы" else "Взлом Ядра Ци"}",
                releaseTime = if (chNum == 68) "12 мин назад" else "3 дня назад",
                tgTeam = "⚡ CyberTeam Scans [TG]",
                pages = generateCyberPages(chNum),
                isRead = chNum < 68,
                readProgress = if (chNum < 68) 1.0f else 0.0f,
                r2LatencyMs = 15
            )
        }

        _chapters.value = mapOf(
            "solo_monarch" to soloChapters,
            "cyber_fist" to cyberChapters
        )
    }

    private fun generateSoloPages(chapterNumber: Int): List<ChapterPage> {
        return listOf(
            ChapterPage(
                pageNumber = 1,
                drawableRes = R.drawable.img_cover_solo_monarch,
                panelTitle = "КРАСНЫЕ ВРАТА: УРОВЕНЬ БЕДСТВИЯ S",
                panelDialogue = "«Воздух вокруг дрожит... Система, отобрази статус подземелья!»",
                actionDescription = "Пурпурное свечение окутывает клинки монарха. Тени поднимаются из ледяной земли.",
                watermarkText = "⚡ TG @ShadowScans_VIP — Переведено за 12 мин!"
            ),
            ChapterPage(
                pageNumber = 2,
                drawableRes = null,
                panelTitle = "ВСПЛЕСК ЭНЕРГИИ ТЬМЫ",
                panelDialogue = "«— Командир Игрис, возьми левый фланг! Железный — круши барьер!»",
                actionDescription = "Грохот разрывает тишину. Сотни темных рыцарей срываются в атаку под рев ледяных чудовищ.",
                watermarkText = "⚡ Спонсорский баннер от команды перевода"
            ),
            ChapterPage(
                pageNumber = 3,
                drawableRes = R.drawable.img_cover_solo_monarch,
                panelTitle = "ПРИКАЗ ПОВЕЛИТЕЛЯ",
                panelDialogue = "«Восстаньте... Ни одна душа не покинет это поле боя без моего разрешения.»",
                actionDescription = "Глаза охотника вспыхивают неземным фиолетовым огнем. Руны светятся под ногами.",
                watermarkText = null
            ),
            ChapterPage(
                pageNumber = 4,
                drawableRes = null,
                panelTitle = "УДАР КЛИНКОВ БЕЗДНЫ",
                panelDialogue = "«— ВШУУУУХ! КХХХХ! Невозможно... Он движется быстрее звука?!»",
                actionDescription = "Двойное лезвие рассекает барьер босса подземелья в один миг.",
                watermarkText = "⚡ Эксклюзивно для канала @ShadowScans"
            ),
            ChapterPage(
                pageNumber = 5,
                drawableRes = R.drawable.img_cover_solo_monarch,
                panelTitle = "ПОБЕДА И НОВЫЙ РАНГ",
                panelDialogue = "«[Система: Получено 1,250,000 опыта. Уровень повышен до 150.]»",
                actionDescription = "Ледяная пещера озаряется светом очищения. Джин-у вкладывает клинки в ножны.",
                watermarkText = null
            )
        )
    }

    private fun generateCyberPages(chapterNumber: Int): List<ChapterPage> {
        return listOf(
            ChapterPage(
                pageNumber = 1,
                drawableRes = R.drawable.img_cover_cyber_claws,
                panelTitle = "СЕУЛ 2077: СЕКТОР НЕОНОВЫХ КРЫШ",
                panelDialogue = "«— Передай чип данных синдикату, иначе твои аугментации замкнут навсегда!»",
                actionDescription = "Дождь стекает по стеклянным небоскребам. Дроны корпорации окружают героя.",
                watermarkText = "⚡ Скан-команда CyberTeam [TG: @NeonManga]"
            ),
            ChapterPage(
                pageNumber = 2,
                drawableRes = null,
                panelTitle = "АКТИВАЦИЯ ИМПЛАНТА «НЕОНОВЫЙ КУЛАК»",
                panelDialogue = "«Синхронизация Ци: 100%. Наномашины в правой руке разогреваются до предела.»",
                actionDescription = "Изумрудные молнии пробегают по пальцам. Воздух наполняется ионизированным озоном.",
                watermarkText = "⚡ Промокод CHILLMANGA на скидку в шопе"
            ),
            ChapterPage(
                pageNumber = 3,
                drawableRes = R.drawable.img_cover_cyber_claws,
                panelTitle = "СОКРУШИТЕЛЬНЫЙ ВЫПАД",
                panelDialogue = "«— БУУУУМ! Стиль Зеленого Дракона: Сверхзвуковой Импульс!»",
                actionDescription = "Кибернетический боевой робот сминается в гармошку от прямого попадания кулака.",
                watermarkText = null
            ),
            ChapterPage(
                pageNumber = 4,
                drawableRes = null,
                panelTitle = "ПОБЕГ В НОЧЬ",
                panelDialogue = "««Держись крепче, следующая цель — верхние ярусы башни Ханган».»",
                actionDescription = "Кан спрыгивает на крышу летящего аэрокара и растворяется в неоновом дожде.",
                watermarkText = "⚡ Подпишись на @CyberTeam_Scans"
            )
        )
    }

    fun toggleQueue(titleId: String) {
        _titles.update { list ->
            list.map {
                if (it.id == titleId) it.copy(inQueue = !it.inQueue) else it
            }
        }
    }

    fun toggleFavorite(titleId: String) {
        _titles.update { list ->
            list.map {
                if (it.id == titleId) it.copy(isFavorite = !it.isFavorite) else it
            }
        }
    }

    fun updateHandsFreeConfig(update: (HandsFreeConfig) -> HandsFreeConfig) {
        _handsFreeConfig.update(update)
    }

    fun incrementHeadNodCount() {
        _stats.update {
            it.copy(headNodsDetected = it.headNodsDetected + 1)
        }
    }

    fun markChapterRead(titleId: String, chapterNumber: Int) {
        _chapters.update { currentMap ->
            val list = currentMap[titleId] ?: return@update currentMap
            val updated = list.map { ch ->
                if (ch.chapterNumber == chapterNumber) ch.copy(isRead = true, readProgress = 1.0f) else ch
            }
            currentMap + (titleId to updated)
        }
        _titles.update { titles ->
            titles.map { t ->
                if (t.id == titleId) {
                    val newRead = (t.readChapters + 1).coerceAtMost(t.totalChapters)
                    t.copy(readChapters = newRead)
                } else t
            }
        }
    }

    fun clearCache() {
        _cacheSizeBytes.value = 0L
    }
}
