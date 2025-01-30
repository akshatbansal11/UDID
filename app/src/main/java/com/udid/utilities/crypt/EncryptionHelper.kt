package com.udid.utilities.crypt

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object EncryptionHelper {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "NLM_KEY"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    // Generate or retrieve the AES key
    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        return if (keyStore.containsAlias(KEY_ALIAS)) {
            (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    // Encrypt data
    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv // Initialization vector
        val encrypted = cipher.doFinal(data.toByteArray())
        val ivAndEncrypted = iv + encrypted
        return Base64.encodeToString(ivAndEncrypted, Base64.DEFAULT)
    }

    // Decrypt data
    fun decrypt(data: String): String {
        val ivAndEncrypted = Base64.decode(data, Base64.DEFAULT)
        val iv = ivAndEncrypted.sliceArray(0 until 12) // 12-byte IV
        val encrypted = ivAndEncrypted.sliceArray(12 until ivAndEncrypted.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        return String(cipher.doFinal(encrypted))
    }
}
