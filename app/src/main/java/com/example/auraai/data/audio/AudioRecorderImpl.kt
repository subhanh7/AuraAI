package com.example.auraai.data.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.sqrt

class AudioRecorderImpl @Inject constructor() : AudioRecorder {

    private val _amplitude = MutableStateFlow(0f)
    override val amplitude = _amplitude.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    override val isRecording = _isRecording.asStateFlow()

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    @SuppressLint("MissingPermission")
    override fun start() {
        if (audioRecord != null) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord?.startRecording()
        _isRecording.value = true

        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            val readSize = if (bufferSize > 0) bufferSize / 2 else 1024
            val buffer = ShortArray(readSize)
            var smoothedAmp = 0f
            val alpha = 0.4f // Low-pass filter weight

            while (isActive) {
                val read = audioRecord?.read(buffer, 0, readSize) ?: 0
                if (read > 0) {
                    var sum = 0.0
                    var maxSample = 0
                    for (i in 0 until read) {
                        val sample = abs(buffer[i].toInt())
                        if (sample > maxSample) maxSample = sample
                        sum += sample.toDouble() * sample.toDouble()
                    }
                    val rms = sqrt(sum / read)
                    
                    // Boost normalization scale for Android Mic input (RMS peak ~2500)
                    val rawAmplitude = (rms / 2500.0).toFloat().coerceIn(0f, 1f)
                    
                    // Low-pass exponential smoothing
                    smoothedAmp = smoothedAmp * (1f - alpha) + rawAmplitude * alpha
                    
                    _amplitude.value = smoothedAmp
                }
            }
        }
    }

    override fun stop() {
        recordingJob?.cancel()
        recordingJob = null
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        _isRecording.value = false
        _amplitude.value = 0f
    }
}
