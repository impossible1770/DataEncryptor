package com.example.myapplication.encryption.data

import java.io.File
import javax.crypto.SecretKey

internal class EncryptedSensitiveData(
    val key: SecretKey,
    val data: File,
)
