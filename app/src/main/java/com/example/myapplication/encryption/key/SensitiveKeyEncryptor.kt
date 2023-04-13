package com.example.myapplication.encryption.key

import android.util.Base64
import android.util.Log
import com.example.myapplication.encryption.Constants
import java.io.File
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

internal class SensitiveKeyEncryptor(
    private val publicRsaKey: String,
) {

    private val rsaCipher: Cipher

    init {
        rsaCipher = Cipher.getInstance(Constants.RSA_MODE).apply {
            init(Cipher.ENCRYPT_MODE, clearRsaHeadersAndGeneratePublicKey(publicRsaKey))
        }
    }

    fun encrypt(secretKey: SecretKey): File? {
        return try {
            encryptData(secretKey)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchPaddingException,
                is InvalidKeyException,
                is IllegalStateException,
                is IllegalBlockSizeException,
                is BadPaddingException,
                is KeyStoreException,
                -> {
                    Log.e("LogsKeyEncryptor", e.localizedMessage, )
                    null
                }
                else -> throw e
            }
        }
    }

    private fun encryptData(secretKey: SecretKey): File? {
        return File.createTempFile(TEMP_ENCRYPTED_FILE_NAME, TEMP_ENCRYPTED_FILE_EXT).apply {
            writeBytes(rsaCipher.doFinal(secretKey.encoded))
        }
    }

    private fun clearRsaHeadersAndGeneratePublicKey(key: String): PublicKey? {
        val publicKeyPEM = key
            .replace(PUBLIC_KEY_HEADER, "")
            .replace(System.lineSeparator().toRegex(), "")
            .replace(PUBLIC_KEY_FOOTER, "")
        val encoded: ByteArray = Base64.decode(publicKeyPEM, Base64.DEFAULT)
        val keyFactory: KeyFactory = KeyFactory.getInstance(KEY_FACTORY)
        val keySpec = X509EncodedKeySpec(encoded)
        return keyFactory.generatePublic(keySpec)
    }

    private companion object {
        const val KEY_FACTORY = "RSA"
        const val PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----"
        const val PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----"
        const val TEMP_ENCRYPTED_FILE_NAME = "encryptedKey"
        const val TEMP_ENCRYPTED_FILE_EXT = ".enc"
    }
}
