package com.example.myapplication.encryption

import com.example.myapplication.encryption.data.SensitiveDataEncryptor
import com.example.myapplication.encryption.key.SensitiveKeyEncryptor
import java.io.File

internal class SensitiveEncryptor(
    private val keyEncryptor: SensitiveKeyEncryptor,
    private val dataEncryptor: SensitiveDataEncryptor,
) {

    fun encrypt(source: File): EncryptedResult {
        val encryptedData = dataEncryptor.encrypt(source)
        val encryptedKey = keyEncryptor.encrypt(encryptedData.key)
        return EncryptedResult(
            key = requireNotNull(encryptedKey),
            data = encryptedData.data,
        )
    }
}
