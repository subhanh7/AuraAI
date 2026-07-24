package com.example.auraai.domain.statemachine

import app.cash.turbine.test
import com.example.auraai.domain.model.ChatMessage
import com.example.auraai.domain.repository.ChatRepository
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConversationStateMachineTest {

    private val chatRepository = mockk<ChatRepository>(relaxed = true)
    private val stateMachine = ConversationStateMachine(chatRepository)

    @Test
    fun `happy path transitions correctly`() = runTest {
        stateMachine.state.test {
            assertEquals(ConversationState.Idle, awaitItem())

            stateMachine.onInputChanged("Hello")
            assertTrue(awaitItem() is ConversationState.Typing)

            stateMachine.dispatchAction("Hello", this)
            
            assertEquals(ConversationState.Validating, awaitItem())
            assertEquals(ConversationState.Processing, awaitItem())
            assertTrue(awaitItem() is ConversationState.Responding)
            assertEquals(ConversationState.Idle, awaitItem())
        }
    }

    @Test
    fun `dispatchAction cancels previous job and starts new one`() = runTest {
        stateMachine.state.test {
            assertEquals(ConversationState.Idle, awaitItem())

            // Start first action
            stateMachine.dispatchAction("First", this)
            assertEquals(ConversationState.Validating, awaitItem())
            assertEquals(ConversationState.Processing, awaitItem())

            // Immediately start second action
            stateMachine.dispatchAction("Second", this)
            // It should go back to Validating for the new message
            assertEquals(ConversationState.Validating, awaitItem())
            assertEquals(ConversationState.Processing, awaitItem())
            
            val responding = awaitItem() as ConversationState.Responding
            assertEquals("I've processed your request about 'Second'. How else can I help?", responding.message.content)
            
            assertEquals(ConversationState.Idle, awaitItem())
        }
    }

    @Test
    fun `blank input returns to idle`() = runTest {
        stateMachine.state.test {
            assertEquals(ConversationState.Idle, awaitItem())

            stateMachine.dispatchAction("   ", this)
            assertEquals(ConversationState.Validating, awaitItem())
            assertEquals(ConversationState.Idle, awaitItem())
        }
    }

    @Test
    fun `timeout leads to error state`() = runTest {
        val slowStateMachine = object : ConversationStateMachine(chatRepository) {
            override suspend fun performAiLogic(input: String): ChatMessage {
                delay(10000) // Delay longer than the 8s timeout
                return super.performAiLogic(input)
            }
        }

        slowStateMachine.state.test {
            assertEquals(ConversationState.Idle, awaitItem())

            slowStateMachine.dispatchAction("Slow Query", this)
            
            assertEquals(ConversationState.Validating, awaitItem())
            assertEquals(ConversationState.Processing, awaitItem())
            
            val errorState = awaitItem() as ConversationState.Error
            assertEquals("Aura is taking too long to respond.", errorState.message)
            assertEquals("Slow Query", errorState.lastInput)
        }
    }
}
