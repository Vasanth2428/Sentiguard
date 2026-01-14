package com.sentiguard.app.system.security

import com.sentiguard.app.data.local.db.EvidenceEntity

class IntegrityVerifier {
    
    data class VerificationResult(
        val isClean: Boolean,
        val failedIndex: Int = -1,
        val failedId: String? = null,
        val totalVerified: Int = 0
    )

    fun verifyChain(events: List<EvidenceEntity>): VerificationResult {
        // Events are usually DESC (Newest First). We need ASC (Oldest First) to verify the chain.
        // Assuming the list passed in is sorted by something, let's sort by timestamp ASC just to be sure.
        val sortedEvents = events.sortedBy { it.timestamp }
        
        val chainBuilder = EvidenceChainBuilder()
        var expectedPrevHash = "GENESIS_HASH"
        
        sortedEvents.forEachIndexed { index, event ->
            // 1. Verify 'previousHash' pointer matches the actual previous hash
            if (event.previousHash != expectedPrevHash) {
                return VerificationResult(isClean = false, failedIndex = index, failedId = event.id)
            }
            
            // 2. Verify the hash itself (Data Integrity)
            // Re-calculate hash(data + prevHash) and check if it matches storage
            val calculatedHash = chainBuilder.buildEventHash(event, expectedPrevHash)
            if (calculatedHash != event.hash) {
                return VerificationResult(isClean = false, failedIndex = index, failedId = event.id)
            }
            
            // Move pointer
            expectedPrevHash = event.hash
        }
        
        return VerificationResult(isClean = true, totalVerified = sortedEvents.size)
    }
}
