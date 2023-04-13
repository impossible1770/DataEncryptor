package com.example.myapplication.encryption.data

import java.io.File
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class SensitiveDataEncryptor {

    fun encrypt(source: File): EncryptedSensitiveData {
        val secretKey = generateKey()

        val raw: ByteArray = secretKey.encoded
        val skeySpec = SecretKeySpec(raw, ENCRYPTED_ALGORITHM_BITS)
        val cipher = Cipher.getInstance(CIPHER_CONFIG)
        val iv = IvParameterSpec(ByteArray(IV_SIZE_BYTES))
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        val encryptedData = File.createTempFile(ENCRYPTED_FILE_NAME, ENCRYPTED_FILE_EXT).apply {
            writeBytes(cipher.doFinal(source.readBytes()))
        }
        return EncryptedSensitiveData(
            key = secretKey,
            data = encryptedData
        )
    }

    private fun generateKey(): SecretKey {
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(ENCRYPTED_ALGORITHM_BITS)
        keyGenerator.init(KEY_SIZE_BITS) // AES is currently available in three key sizes:
        // 128, 192 and 256 bits.The design and strength of all key lengths of the AES
        // algorithm are sufficient to protect classified information up to the SECRET level
        return keyGenerator.generateKey()
    }

    private companion object {
        const val ENCRYPTED_ALGORITHM_BITS = "AES"
        const val KEY_SIZE_BITS = 128
        const val IV_SIZE_BYTES = 16
        const val CIPHER_CONFIG = "AES/CBC/PKCS5Padding"
        const val ENCRYPTED_FILE_NAME = "encryptedData"
        const val ENCRYPTED_FILE_EXT = ".aes"
    }
}
