package com.example.auraai.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.auraai.data.audio.AudioRecorder
import com.example.auraai.domain.model.ChatMessage
import com.example.auraai.domain.repository.ChatRepository
import com.example.auraai.domain.repository.UserRepository
import com.example.auraai.domain.statemachine.ConversationState
import com.example.auraai.domain.statemachine.ConversationStateMachine
import com.example.auraai.presentation.home.components.AuraState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val audioRecorder: AudioRecorder,
    private val stateMachine: ConversationStateMachine
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val conversationState: StateFlow<ConversationState> = stateMachine.state

    val pagedMessages: Flow<PagingData<ChatMessage>> = chatRepository
        .getPagedMessages()
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            userRepository.userProfile.collect { profile ->
                _uiState.update { it.copy(userName = profile.name) }
            }
        }

        viewModelScope.launch {
            audioRecorder.amplitude.collect { amp ->
                _uiState.update { it.copy(amplitude = amp) }
            }
        }
        
        viewModelScope.launch {
            combine(
                conversationState,
                audioRecorder.amplitude,
                audioRecorder.isRecording
            ) { state, amp, isRec ->
                Triple(state, amp, isRec)
            }.collect { (state, amp, isRec) ->
                val newAuraState = when {
                    state is ConversationState.Processing || state is ConversationState.Validating -> AuraState.PROCESSING
                    state is ConversationState.Responding -> AuraState.RESPONDING
                    isRec -> AuraState.LISTENING
                    else -> AuraState.IDLE
                }
                _uiState.update { it.copy(auraState = newAuraState, amplitude = amp) }
            }
        }
    }

    fun toggleMicrophone() {
        val currentState = _uiState.value.auraState
        if (currentState == AuraState.LISTENING) {
            audioRecorder.stop()
        } else {
            audioRecorder.start()
        }
    }

    fun toggleInputPanel() {
        _uiState.update { it.copy(isInputPanelVisible = !it.isInputPanelVisible) }
    }

    fun onInputChange(input: String) {
        _uiState.update { it.copy(currentInput = input) }
        stateMachine.onInputChanged(input)
    }

    fun sendMessage() {
        val message = _uiState.value.currentInput
        stateMachine.dispatchAction(message, viewModelScope)
        _uiState.update { it.copy(currentInput = "", isInputPanelVisible = false) }
    }

    fun retry() {
        stateMachine.retry(viewModelScope)
    }
}
