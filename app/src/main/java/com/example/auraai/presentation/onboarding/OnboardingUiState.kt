package com.example.auraai.presentation.onboarding

data class OnboardingUiState(
    val name: String = "",
    val age: String = "",
    val phone: String = "",
    val otp: String = "",
    val selectedTraits: Set<String> = emptySet(),
    val nameError: String? = null,
    val ageError: String? = null,
    val phoneError: String? = null,
    val otpError: String? = null,
    val isOtpVerified: Boolean = false,
    val isSaving: Boolean = false
) {
    val isProfileValid: Boolean = name.isNotBlank() && 
            (age.toIntOrNull() ?: 0) > 0 && 
            phone.length == 10 && 
            otp.length == 4

    val isPersonalityValid: Boolean = selectedTraits.size == 3
}
