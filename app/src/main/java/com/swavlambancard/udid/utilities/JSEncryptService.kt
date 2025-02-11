package com.swavlambancard.udid.utilities

import android.util.Base64
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object JSEncryptService {
    // Public key for encryption, stored in PEM format
    private val pubKey: String = """
        -----BEGIN PUBLIC KEY-----
        MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCn+gSMOvb+6oi2eWqmxlt/qoq4
        3S2j7yXrLhIhtS02NPE+t14ZAQxMiJd5YPKps5ZcT2JhIdTv75ZPFmnj2+E0MhL2
        XkCfkW6LEg4xVMr8TeV5cyVtRRjd8XkL+awA8niJKNYIJk8y/3112cT7TnrkB6Ct
        4LHrrn2FG2Y9nVn8hQIDAQAB
        -----END PUBLIC KEY-----
    """.trimIndent()

    // Private key for decryption, stored in PEM format
    private val privKey: String = """
        -----BEGIN RSA PRIVATE KEY-----
        MIICXAIBAAKBgQCn+gSMOvb+6oi2eWqmxlt/qoq43S2j7yXrLhIhtS02NPE+t14Z
        AQxMiJd5YPKps5ZcT2JhIdTv75ZPFmnj2+E0MhL2XkCfkW6LEg4xVMr8TeV5cyVt
        RRjd8XkL+awA8niJKNYIJk8y/3112cT7TnrkB6Ct4LHrrn2FG2Y9nVn8hQIDAQAB
        AoGAXK0GYVAPAinn77UhcI4z4UX4b3IoQjApnY23lz1cinG/QDjvA6CeZoNd/yvL
        9nEM3jU2NBz0XMS1C0F0frDLmJlZu3ko1EkPdN19bsDFKhNduOMugtT6s4w8WrrJ
        XonnDQClddC+XNieuY2tF7d8+RK4fjvailbzrMaZZN+xKikCQQDc0PKsXCXhAwDf
        1a+fzBdj+6mDBafe5p32pgXjCsBYzLk3A3UHsNPb3dQsXEmMLKQSGAe1w8W32Ugt
        bst5nuGHAkEAwr3A5T+6D8aeMpnXX8CrOMlQJqxICy26p/Iyiv8nD4dCO5RAv76w
        FdlN504QRf1Oj3FVuMtpifZ+7X+DhTykkwJAdz9fzjT1P86fakG71lAhUZ1Wrasg
        PP/NzqVaCIKF3W6xl0QGr2CPCO2C53HvRgPVlu/jOgW/gMmWcPKkb8mdgQJBAI2N
        SioY8VDISXN0eaXDMXIceqMxtUhS3At5tB1uq+DQq2cNMydtHycyhjrsdk25eyIu
        +mVRjgxXXRmbB2mQk/sCQD1JWmiBihJkMB7ujbWDdrmsXe3RT6vSQCVmtVF/u5iF
        t4FbIesVNmw1pOMe5v48E+utJ1ecOFGBWtcB395zmRU=
        -----END RSA PRIVATE KEY-----
    """.trimIndent()

    // Encrypts the input data using the public key
    fun encrypt(data: String): String? {
        return try {
            // Load the public key
            val publicKey = loadPublicKey(pubKey)
            // Initialize the cipher with RSA algorithm and PKCS1 padding
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            // Encrypt the data and encode it in Base64
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            // Print the stack trace for any exceptions
            e.printStackTrace()
            null
        }
    }

    // Decrypts the input data using the private key
    fun decrypt(data: String): String? {
        return try {
            // Load the private key
            val privateKey = loadPrivateKey(privKey)
            // Initialize the cipher with RSA algorithm and PKCS1 padding
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            // Decode the Base64 encoded input and decrypt it
            val decodedBytes = Base64.decode(data, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            // Convert the decrypted bytes to a string
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            // Print the stack trace for any exceptions
            e.printStackTrace()
            null
        }
    }

    // Converts the PEM format public key string into a PublicKey object
    private fun loadPublicKey(publicKeyPEM: String): PublicKey {
        val publicKeyPEM = publicKeyPEM
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")
        // Decode the Base64 encoded key
        val decoded = Base64.decode(publicKeyPEM, Base64.DEFAULT)
        val spec = X509EncodedKeySpec(decoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(spec)
    }

    // Converts the PEM format private key string into a PrivateKey object
    private fun loadPrivateKey(privateKeyPEM: String): PrivateKey {
        val privateKeyPEM = privateKeyPEM
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replace("\n", "")
        // Decode the Base64 encoded key
        val decoded = Base64.decode(privateKeyPEM, Base64.DEFAULT)
        val spec = PKCS8EncodedKeySpec(decoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(spec)
    }
}
