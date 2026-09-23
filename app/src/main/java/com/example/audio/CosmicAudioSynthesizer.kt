package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class CosmicAudioSynthesizer {

    private val sampleRate = 22050
    private var musicVolume: Float = 0.5f
    private var sfxVolume: Float = 0.7f
    private var isMusicEnabled: Boolean = true

    private var ambientTrack: AudioTrack? = null
    private var musicJob: Job? = null
    private val audioScope = CoroutineScope(Dispatchers.Default)

    fun setMusicVolume(percent: Int) {
        musicVolume = (percent.coerceIn(0, 100) / 100f)
        try {
            ambientTrack?.setVolume(musicVolume)
        } catch (_: Exception) {}
    }

    fun setSfxVolume(percent: Int) {
        sfxVolume = (percent.coerceIn(0, 100) / 100f)
    }

    fun toggleMusic(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            startInterstellarMusic()
        } else {
            stopInterstellarMusic()
        }
    }

    fun startInterstellarMusic() {
        if (!isMusicEnabled || musicJob?.isActive == true) return

        musicJob = audioScope.launch {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = maxOf(minBufferSize, sampleRate)

                ambientTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                ambientTrack?.setVolume(musicVolume)
                ambientTrack?.play()

                // Interstellar organ / deep cosmic drone chord (D-minor / Fifth ambient pad: 73.4Hz, 110Hz, 146.8Hz, 220Hz)
                val f1 = 73.416 // D2
                val f2 = 110.0  // A2
                val f3 = 146.83 // D3
                val f4 = 174.61 // F3

                val buffer = ShortArray(1024)
                var phase1 = 0.0
                var phase2 = 0.0
                var phase3 = 0.0
                var phase4 = 0.0
                var lfoPhase = 0.0

                while (isActive && isMusicEnabled) {
                    val currentVol = musicVolume
                    for (i in buffer.indices) {
                        // Gentle slow cosmic LFO modulation (0.2 Hz)
                        val lfo = (1.0 + 0.3 * sin(2.0 * PI * lfoPhase)) / 1.3
                        lfoPhase += 0.2 / sampleRate
                        if (lfoPhase > 1.0) lfoPhase -= 1.0

                        val s1 = sin(2.0 * PI * phase1) * 0.35
                        val s2 = sin(2.0 * PI * phase2) * 0.25
                        val s3 = sin(2.0 * PI * phase3) * 0.20
                        val s4 = sin(2.0 * PI * phase4) * 0.15

                        phase1 += f1 / sampleRate
                        if (phase1 > 1.0) phase1 -= 1.0
                        phase2 += f2 / sampleRate
                        if (phase2 > 1.0) phase2 -= 1.0
                        phase3 += f3 / sampleRate
                        if (phase3 > 1.0) phase3 -= 1.0
                        phase4 += f4 / sampleRate
                        if (phase4 > 1.0) phase4 -= 1.0

                        val mixed = (s1 + s2 + s3 + s4) * lfo * currentVol
                        buffer[i] = (mixed.coerceIn(-1.0, 1.0) * 32767).toInt().toShort()
                    }
                    ambientTrack?.write(buffer, 0, buffer.size)
                }
            } catch (_: Exception) {
                // Audio initialization gracefully handled
            } finally {
                stopAmbientTrack()
            }
        }
    }

    fun stopInterstellarMusic() {
        musicJob?.cancel()
        musicJob = null
        stopAmbientTrack()
    }

    private fun stopAmbientTrack() {
        try {
            ambientTrack?.pause()
            ambientTrack?.flush()
            ambientTrack?.release()
        } catch (_: Exception) {}
        ambientTrack = null
    }

    fun playTone(freq: Float, durationMs: Int = 180) {
        if (sfxVolume <= 0.01f) return

        audioScope.launch {
            try {
                val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
                val samples = ShortArray(numSamples)
                val currentSfxVol = sfxVolume

                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    // Smooth bell-curve envelope for chime
                    val envelope = sin(progress * PI)
                    val sampleVal = sin(2.0 * PI * freq * i / sampleRate) * envelope * currentSfxVol
                    samples[i] = (sampleVal.coerceIn(-1.0, 1.0) * 32767).toInt().toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()
                // Let static track finish and release
                kotlinx.coroutines.delay(durationMs + 60L)
                track.release()
            } catch (_: Exception) {}
        }
    }

    fun playClickSfx() {
        playTone(freq = 660f, durationMs = 80)
    }

    fun playCosmicPing() {
        playTone(freq = 880f, durationMs = 240)
    }

    fun release() {
        stopInterstellarMusic()
    }
}
