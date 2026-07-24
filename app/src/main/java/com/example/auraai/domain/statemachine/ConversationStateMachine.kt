package com.example.auraai.domain.statemachine

import com.example.auraai.domain.model.ChatMessage
import com.example.auraai.domain.model.Sender
import com.example.auraai.domain.repository.ChatRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

open class ConversationStateMachine @Inject constructor(
    private val chatRepository: ChatRepository
) {
    private val _state = MutableStateFlow<ConversationState>(ConversationState.Idle)
    val state: StateFlow<ConversationState> = _state.asStateFlow()

    private var conversationJob: Job? = null

    fun onInputChanged(input: String) {
        if (_state.value is ConversationState.Idle || _state.value is ConversationState.Typing) {
            if (input.isEmpty()) {
                _state.value = ConversationState.Idle
            } else {
                _state.value = ConversationState.Typing(input)
            }
        }
    }

    fun dispatchAction(input: String, scope: CoroutineScope) {
        conversationJob?.cancel()
        conversationJob = scope.launch {
            try {
                // 1. Validating
                _state.value = ConversationState.Validating
                chatRepository.sendMessage(input) // Save user message
                delay(500) // UI visibility
                if (input.isBlank()) {
                    _state.value = ConversationState.Idle
                    return@launch
                }

                // 2. Processing
                _state.value = ConversationState.Processing
                
                // Simulate AI Request with 8s timeout
                val response = withTimeout(8000) {
                    performAiLogic(input)
                }

                // 3. Responding
                _state.value = ConversationState.Responding(response)
                chatRepository.saveAiResponse(response.content) // Save AI response

                delay(2000) // Time to read/see response

                // 4. Return to Idle
                _state.value = ConversationState.Idle
                
            } catch (e: TimeoutCancellationException) {
                _state.value = ConversationState.Error("Aura is taking too long to respond.", input)
            } catch (e: CancellationException) {
                // Ignore cancellation as it's intended
            } catch (e: Exception) {
                _state.value = ConversationState.Error(e.message ?: "Unknown error", input)
            }
        }
    }

    protected open suspend fun performAiLogic(input: String): ChatMessage {
        delay(2000) // Simulate processing time
        return ChatMessage(
            sender = Sender.AURA,
            content = "I've processed your request about '$input'. How else can I help?"
        )
    }

    fun retry(scope: CoroutineScope) {
        val currentState = _state.value
        if (currentState is ConversationState.Error) {
            dispatchAction(currentState.lastInput, scope)
        }
    }
}
