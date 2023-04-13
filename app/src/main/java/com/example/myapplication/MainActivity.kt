package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.encryption.SensitiveEncryptor
import com.example.myapplication.encryption.data.SensitiveDataEncryptor
import com.example.myapplication.encryption.key.SensitiveKeyEncryptor
import java.io.File

class MainActivity : AppCompatActivity() {

    private var viewBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(viewBinding?.root)
    }

    override fun onResume() {
        super.onResume()

        val publicRsaKey = readPublicRsaKey(this@MainActivity)
        val dataEncryptor = SensitiveDataEncryptor()
        val keyEncryptor = SensitiveKeyEncryptor(publicRsaKey)
        val sensitiveEncryptor = SensitiveEncryptor(keyEncryptor, dataEncryptor)


        viewBinding?.encrypt?.setOnClickListener {

            val dataToEncrypt = File(this.filesDir.absolutePath, "BigFile.txt")
            dataToEncrypt.writeText(getEnteredText())
            val encryptedFile = sensitiveEncryptor.encrypt(dataToEncrypt)
            encryptedFile.key.renameTo(File(this.filesDir.absolutePath, "EncryptedAESKey.enc"))
            encryptedFile.data.renameTo(File(this.filesDir.absolutePath, "EncryptedData.aes"))

            Log.d(
                "MainActivity",
                "Encrypted AES key with RSA - ${encryptedFile.key.absolutePath}\n" +
                        "Encrypted Data with AES - ${encryptedFile.data.absolutePath}"
            )
            Toast.makeText(this, "Data successfully encrypted", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getEnteredText() = viewBinding?.inputEt?.text.toString()

    private fun readPublicRsaKey(context: Context): String {
        return context.assets.open("public_rsa_key.txt")
            .bufferedReader()
            .readText()
    }

    override fun onDestroy() {
        viewBinding = null
        super.onDestroy()
    }
}