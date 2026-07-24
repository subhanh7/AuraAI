package com.example.auraai.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auraai.domain.model.UserProfile
import com.example.auraai.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.userProfile.collect { profile ->
                _uiState.update { 
                    it.copy(
                        name = profile.name,
                        age = if (profile.age > 0) profile.age.toString() else "",
                        phone = profile.phone,
                        selectedTraits = profile.selectedTraits.toSet()
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onAgeChange(age: String) {
        if (age.all { it.isDigit() }) {
            _uiState.update { it.copy(age = age, ageError = null) }
        }
    }

    fun onPhoneChange(phone: String) {
        if (phone.length <= 10 && phone.all { it.isDigit() }) {
            _uiState.update { it.copy(phone = phone, phoneError = null) }
        }
    }

    fun onOtpChange(otp: String) {
        if (otp.length <= 4 && otp.all { it.isDigit() }) {
            _uiState.update { it.copy(otp = otp, otpError = null) }
        }
    }

    fun verifyOtpAndValidateProfile(): Boolean {
        val state = _uiState.value
        if (!state.isProfileValid) return false
        
        return if (state.otp == "1234") {
            _uiState.update { it.copy(isOtpVerified = true, otpError = null) }
            true
        } else {
            _uiState.update { it.copy(isOtpVerified = false, otpError = "Invalid OTP. Use 1234.") }
            false
        }
    }

    fun onTraitSelected(trait: String) {
        _uiState.update { state ->
            val newTraits = if (state.selectedTraits.contains(trait)) {
                state.selectedTraits - trait
            } else if (state.selectedTraits.size < 3) {
                state.selectedTraits + trait
            } else {
                state.selectedTraits
            }
            state.copy(selectedTraits = newTraits)
        }
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isPersonalityValid) {
            _uiState.update { it.copy(isSaving = true) }
            viewModelScope.launch {
                val profile = UserProfile(
                    name = state.name,
                    age = state.age.toIntOrNull() ?: 0,
                    phone = state.phone,
                    selectedTraits = state.selectedTraits.toList(),
                    isOnboardingCompleted = true
                )
                userRepository.saveUserProfile(profile)
                onSuccess()
            }
        }
    }
}
