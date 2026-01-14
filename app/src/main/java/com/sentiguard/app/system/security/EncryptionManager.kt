package com.sentiguard.app.system.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class EncryptionManager {

    private val provider = "AndroidKeyStore"
    private val alias = "SentiguardMasterKey"
    private val cipherTransformation = "AES/GCM/NoPadding"
    private val tagLengthBit = 128

    init {
        createKeyIfNotExists()
    }

    private fun createKeyIfNotExists() {
        try {
            val keyStore = KeyStore.getInstance(provider)
            keyStore.load(null)
            if (!keyStore.containsAlias(alias)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, provider)
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(
                        alias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build()
                )
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(provider)
        keyStore.load(null)
        return keyStore.getKey(alias, null) as SecretKey
    }

    /**
     * Encrypts data and returns a combination of IV + EncryptedBytes
     */
    fun encrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(cipherTransformation)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data)
        
        // Combine IV and Encrypted Data (IV is needed for decryption)
        // Format: [IV Length (1 byte)][IV bytes][Encrypted Data]
        // Standard GCM IV size is usually 12 bytes.
        
        val combined = ByteArray(1 + iv.size + encryptedBytes.size)
        combined[0] = iv.size.toByte()
        System.arraycopy(iv, 0, combined, 1, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, 1 + iv.size, encryptedBytes.size)
        
        return combined
    }

    fun decrypt(combinedData: ByteArray): ByteArray {
        val ivSize = combinedData[0].toInt()
        val iv = ByteArray(ivSize)
        System.arraycopy(combinedData, 1, iv, 0, ivSize)
        
        val encryptedSize = combinedData.size - 1 - ivSize
        val encryptedBytes = ByteArray(encryptedSize)
        System.arraycopy(combinedData, 1 + ivSize, encryptedBytes, 0, encryptedSize)
        
        val cipher = Cipher.getInstance(cipherTransformation)
        val spec = GCMParameterSpec(tagLengthBit, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        
        return cipher.doFinal(encryptedBytes)
    }
}
