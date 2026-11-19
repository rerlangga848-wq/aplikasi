package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Generates gentle ambient lo-fi chord pads in-app via AudioTrack so the MusicPlayer
 * delivers real playback without blocking the Main Thread.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AmbientMusicEngine {
    private val audioDispatcher = Dispatchers.Default.limitedParallelism(1)
    private val scope = CoroutineScope(SupervisorJob() + audioDispatcher)
    private var playJob: Job? = null

    @Volatile
    private var activeTrack: AudioTrack? = null

    fun play(baseFreqHz: Float) {
        playJob?.cancel()
        playJob = scope.launch {
            releaseActiveTrack()
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            val track = try {
                AudioTrack.Builder()
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
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (_: Exception) {
                return@launch
            }

            activeTrack = track
            try {
                track.play()
                val chunk = ShortArray(2048)
                var sampleIndex = 0L
                val chordIntervals = floatArrayOf(1f, 1.25f, 1.5f, 1.875f)
                val stepSamples = sampleRate * 2

                while (isActive) {
                    for (i in chunk.indices) {
                        val t = sampleIndex.toDouble() / sampleRate
                        val phaseInStep = (sampleIndex % stepSamples).toDouble() / stepSamples
                        val chordShift = ((sampleIndex / stepSamples) % 4).toInt()
                        val rootShift = when (chordShift) {
                            1 -> 1.122f
                            2 -> 0.89f
                            else -> 1.0f
                        }
                        val env = sin(PI * phaseInStep) * (0.65 + 0.35 * exp(-phaseInStep * 1.4))
                        var mix = 0.0
                        for (ratio in chordIntervals) {
                            mix += sin(2.0 * PI * baseFreqHz * rootShift * ratio * t)
                        }
                        val sample = (mix / chordIntervals.size) * env * 0.22
                        chunk[i] = (sample * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            .toShort()
                        sampleIndex++
                    }
                    track.write(chunk, 0, chunk.size)
                }
            } catch (_: Exception) {
            } finally {
                releaseTrackSafe(track)
                if (activeTrack === track) activeTrack = null
            }
        }
    }

    fun stop() {
        playJob?.cancel()
        playJob = null
        scope.launch {
            releaseActiveTrack()
        }
    }

    private fun releaseActiveTrack() {
        val current = activeTrack ?: return
        activeTrack = null
        releaseTrackSafe(current)
    }

    private fun releaseTrackSafe(track: AudioTrack) {
        try {
            track.pause()
            track.flush()
            track.stop()
            track.release()
        } catch (_: Exception) {
        }
    }
}
