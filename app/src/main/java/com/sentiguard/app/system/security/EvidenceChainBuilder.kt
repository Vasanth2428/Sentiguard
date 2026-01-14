package com.sentiguard.app.system.security

import com.sentiguard.app.data.local.db.EvidenceEntity
import com.sentiguard.app.system.security.HashUtils

class EvidenceChainBuilder {

    /**
     * Calculates the hash for a new log entry based on its content and the previous block's hash.
     * This creates the "Blockchain" link.
     */
    fun buildEventHash(event: EvidenceEntity, previousHash: String): String {
        // Canonical String Format: ID|Timestamp|Type|Risk|PreviousHash
        val payload = "${event.id}|${event.timestamp}|${event.type}|${event.riskLevel}|${previousHash}"
        return HashUtils.sha256(payload)
    }

    /**
     * Verifies if a specific event's hash is valid given its predecessor.
     */
    fun verifyEvent(event: EvidenceEntity, previousHash: String): Boolean {
        if (event.hash == null) return false
        val calculated = buildEventHash(event, previousHash)
        return calculated == event.hash
    }
}
