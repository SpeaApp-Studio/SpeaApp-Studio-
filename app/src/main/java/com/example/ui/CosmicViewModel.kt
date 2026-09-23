package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.audio.CosmicAudioSynthesizer
import com.example.data.model.AppLanguage
import com.example.data.model.CelestialCategory
import com.example.data.model.CelestialObject
import com.example.data.model.LanguageDictionary
import com.example.data.model.LocalizedUiText
import com.example.data.repository.CelestialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ActiveDialogType {
    FREE_CATALOG,
    PREMIUM_SUB,
    UNIVERSE_INFO,
    CREDITS,
    EXIT_CONFIRM,
    CELESTIAL_DETAIL
}

data class CosmicUiState(
    val language: AppLanguage = AppLanguage.ARABIC,
    val musicVolume: Int = 50,
    val sfxVolume: Int = 70,
    val isMusicPlaying: Boolean = true,
    val dailyExplorationCount: Int = 1,
    val searchQuery: String = "",
    val selectedCategory: CelestialCategory? = null,
    val selectedCelestialObject: CelestialObject? = null,
    val activeDialog: ActiveDialogType? = null,
    val isSupporterUnlocked: Boolean = false,
    val bookmarkedIds: Set<String> = emptySet()
) {
    val texts: LocalizedUiText
        get() = LanguageDictionary.getTexts(language)
}

class CosmicViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("light_universe_prefs", Context.MODE_PRIVATE)
    private val audioSynth = CosmicAudioSynthesizer()

    private val _uiState = MutableStateFlow(
        CosmicUiState(
            dailyExplorationCount = prefs.getInt("daily_visits", 1),
            isSupporterUnlocked = prefs.getBoolean("supporter_unlocked", false),
            bookmarkedIds = prefs.getStringSet("bookmarks", emptySet()) ?: emptySet()
        )
    )
    val uiState: StateFlow<CosmicUiState> = _uiState.asStateFlow()

    init {
        // Initialize daily exploration counter
        val currentVisits = prefs.getInt("daily_visits", 0) + 1
        prefs.edit().putInt("daily_visits", currentVisits).apply()
        _uiState.update { it.copy(dailyExplorationCount = currentVisits) }

        // Start ambient space music
        audioSynth.setMusicVolume(50)
        audioSynth.setSfxVolume(70)
        audioSynth.startInterstellarMusic()
    }

    fun changeLanguage(newLang: AppLanguage) {
        audioSynth.playClickSfx()
        _uiState.update { it.copy(language = newLang) }
    }

    fun setMusicVolume(volume: Int) {
        val clamped = volume.coerceIn(0, 100)
        audioSynth.setMusicVolume(clamped)
        _uiState.update { it.copy(musicVolume = clamped) }
    }

    fun setSfxVolume(volume: Int) {
        val clamped = volume.coerceIn(0, 100)
        audioSynth.setSfxVolume(clamped)
        _uiState.update { it.copy(sfxVolume = clamped) }
    }

    fun toggleMusic() {
        audioSynth.playClickSfx()
        val newState = !_uiState.value.isMusicPlaying
        audioSynth.toggleMusic(newState)
        _uiState.update { it.copy(isMusicPlaying = newState) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: CelestialCategory?) {
        audioSynth.playClickSfx()
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onCelestialObjectTapped(obj: CelestialObject) {
        audioSynth.playCosmicPing()
        audioSynth.playTone(obj.resonantFrequencyHz, durationMs = 280)
        _uiState.update {
            it.copy(
                selectedCelestialObject = obj,
                activeDialog = ActiveDialogType.CELESTIAL_DETAIL
            )
        }
    }

    fun playObjectFrequency(freq: Float) {
        audioSynth.playTone(freq, durationMs = 500)
    }

    fun openDialog(type: ActiveDialogType) {
        audioSynth.playClickSfx()
        _uiState.update { it.copy(activeDialog = type) }
    }

    fun closeDialog() {
        audioSynth.playClickSfx()
        _uiState.update { it.copy(activeDialog = null, selectedCelestialObject = null) }
    }

    fun toggleBookmark(id: String) {
        audioSynth.playCosmicPing()
        val current = _uiState.value.bookmarkedIds.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        prefs.edit().putStringSet("bookmarks", current).apply()
        _uiState.update { it.copy(bookmarkedIds = current) }
    }

    fun activateSupporterDemo() {
        audioSynth.playCosmicPing()
        prefs.edit().putBoolean("supporter_unlocked", true).apply()
        _uiState.update { it.copy(isSupporterUnlocked = true) }
    }

    fun getFilteredObjects(): List<CelestialObject> {
        val state = _uiState.value
        val query = state.searchQuery.trim().lowercase()
        return CelestialRepository.celestialObjects.filter { obj ->
            val matchesCategory = state.selectedCategory == null || obj.category == state.selectedCategory
            val matchesSearch = if (query.isEmpty()) true else {
                obj.nameAr.lowercase().contains(query) ||
                obj.nameEn.lowercase().contains(query) ||
                obj.nameDe.lowercase().contains(query) ||
                obj.nameEs.lowercase().contains(query) ||
                obj.descriptionAr.lowercase().contains(query) ||
                obj.descriptionEn.lowercase().contains(query)
            }
            matchesCategory && matchesSearch
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioSynth.release()
    }
}
