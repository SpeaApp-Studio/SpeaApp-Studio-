package com.example.data.model

enum class CelestialCategory {
    STAR,
    PLANET,
    BLACK_HOLE,
    GALAXY,
    NEBULA
}

data class CelestialObject(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val nameDe: String,
    val nameEs: String,
    val category: CelestialCategory,
    val bubbleXPercent: Float, // Position within the 0.0..1.0 coordinate system of the bubble
    val bubbleYPercent: Float,
    val distanceAr: String,
    val distanceEn: String,
    val distanceDe: String,
    val distanceEs: String,
    val sizeAr: String,
    val sizeEn: String,
    val sizeDe: String,
    val sizeEs: String,
    val massAr: String,
    val massEn: String,
    val massDe: String,
    val massEs: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val descriptionDe: String,
    val descriptionEs: String,
    val whatIfAr: String,
    val whatIfEn: String,
    val whatIfDe: String,
    val whatIfEs: String,
    val pointColorHex: Long, // 0xFFFF9100 or 0xFF00E5FF or other vibrant tones
    val resonantFrequencyHz: Float,
    val isPremium: Boolean = false
) {
    fun getName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> nameAr
        AppLanguage.ENGLISH -> nameEn
        AppLanguage.GERMAN -> nameDe
        AppLanguage.SPANISH -> nameEs
    }

    fun getDistance(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> distanceAr
        AppLanguage.ENGLISH -> distanceEn
        AppLanguage.GERMAN -> distanceDe
        AppLanguage.SPANISH -> distanceEs
    }

    fun getSize(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> sizeAr
        AppLanguage.ENGLISH -> sizeEn
        AppLanguage.GERMAN -> sizeDe
        AppLanguage.SPANISH -> sizeEs
    }

    fun getMass(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> massAr
        AppLanguage.ENGLISH -> massEn
        AppLanguage.GERMAN -> massDe
        AppLanguage.SPANISH -> massEs
    }

    fun getDescription(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> descriptionAr
        AppLanguage.ENGLISH -> descriptionEn
        AppLanguage.GERMAN -> descriptionDe
        AppLanguage.SPANISH -> descriptionEs
    }

    fun getWhatIf(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> whatIfAr
        AppLanguage.ENGLISH -> whatIfEn
        AppLanguage.GERMAN -> whatIfDe
        AppLanguage.SPANISH -> whatIfEs
    }
}
