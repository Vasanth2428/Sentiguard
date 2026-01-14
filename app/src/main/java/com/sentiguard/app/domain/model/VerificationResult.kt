package com.sentiguard.app.domain.model

data class VerificationResult(
    val isClean: Boolean,
    val failedIndex: Int = -1,
    val failedId: String? = null,
    val totalVerified: Int = 0
)
