package com.example.auraai.data.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AudioRecorder {
    val amplitude: Flow<Float>
    val isRecording: StateFlow<Boolean>
    fun start()
    fun stop()
}
