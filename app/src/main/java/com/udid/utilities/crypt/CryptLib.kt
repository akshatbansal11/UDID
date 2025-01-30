package com.udid.utilities.crypt

import android.util.Base64
import com.udid.utilities.CryptUniqueIdGenerator.getCryptUniqueId

import com.udid.utilities.cryptConstantCodes.CRYPT_IV
import com.udid.utilities.cryptConstantCodes.CRYPT_KEY

import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptLib {
    // cipher to be used for encryption and decryption
    var _cx: Cipher

    // encryption key and initialization vector
    var _key: ByteArray
    var _iv: ByteArray

    init {
        // initialize the cipher with transformation AES/CBC/PKCS5Padding
        //_cx = Cipher.getInstance("AES/CBC/PKCS5Padding");
        _cx = Cipher.getInstance("AES/CBC/PKCS7Padding")
        _key = ByteArray(32) //256 bit key space
        _iv = ByteArray(16) //128 bit IV
    }

    /**
     * @param _inputText     Text to be encrypted or decrypted
     * @param _encryptionKey Encryption key to used for encryption / decryption
     * @param _mode          specify the mode encryption / decryption
     * @param _initVector    Initialization vector
     * @return encrypted or decrypted string based on the mode
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        UnsupportedEncodingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    private fun encryptDecrypt(
        _inputText: String, _encryptionKey: String,
        _mode: EncryptMode, _initVector: String
    ): String {
        var _out = "" // output string
        //_encryptionKey = md5(_encryptionKey);
        //
        // .println("key="+_encryptionKey);
        var len =
            _encryptionKey.toByteArray(StandardCharsets.UTF_8).size // length of the key	provided
        if (_encryptionKey.toByteArray(StandardCharsets.UTF_8).size > _key.size) {
            len = _key.size
        }
        var ivlen = _initVector.toByteArray(StandardCharsets.UTF_8).size
        if (_initVector.toByteArray(StandardCharsets.UTF_8).size > _iv.size) {
            ivlen = _iv.size
        }
        System.arraycopy(_encryptionKey.toByteArray(StandardCharsets.UTF_8), 0, _key, 0, len)
        System.arraycopy(_initVector.toByteArray(StandardCharsets.UTF_8), 0, _iv, 0, ivlen)
        //KeyGenerator _keyGen = KeyGenerator.getInstance("AES");
        //_keyGen.init(128);
        val keySpec = SecretKeySpec(_key, "AES") // Create a new SecretKeySpec
        // for the
        // specified key
        // data and
        // algorithm
        // name.
        val ivSpec = IvParameterSpec(_iv) // Create a new
        // IvParameterSpec
        // instance with the
        // bytes from the
        // specified buffer
        // iv used as
        // initialization
        // vector.

        // encryption
        if (_mode == EncryptMode.ENCRYPT) {
            // Potentially insecure random numbers on Android 4.3 and older.
            // Read
            // https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html
            // for more info.
            _cx.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec) // Initialize this cipher instance
            val results = _cx.doFinal(_inputText.toByteArray(StandardCharsets.UTF_8)) // Finish
            // multi-part
            // transformation
            // (encryption)
            _out = Base64.encodeToString(results, Base64.NO_WRAP) // ciphertext
            // output
        }

        // decryption
        if (_mode == EncryptMode.DECRYPT) {
            _cx.init(Cipher.DECRYPT_MODE, keySpec, ivSpec) // Initialize this ipher instance
            val decodedValue = Base64.decode(
                _inputText.toByteArray(),
                Base64.NO_WRAP
            )
            val decryptedVal = _cx.doFinal(decodedValue) // Finish
            // multi-part
            // transformation
            // (decryption)
            _out = String(decryptedVal)
        }
        return _out // return encrypted/decrypted string
    }

    /***
     * This function encrypts the plain text to cipher text using the key
     * provided. You'll have to use the same key for decryption
     *
     * @param _plainText Plain text to be encrypted
     * @param _key       Encryption Key. You'll have to use the same key for decryption
     * @param _iv        initialization Vector
     * @return returns encrypted (cipher) text
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        InvalidKeyException::class,
        UnsupportedEncodingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(_plainText: String, _key: String, _iv: String): String {
        return encryptDecrypt(_plainText, _key, EncryptMode.ENCRYPT, _iv)
    }

    @JvmOverloads
    @Throws(
        InvalidKeyException::class,
        UnsupportedEncodingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        NoSuchAlgorithmException::class
    )
    fun encryptString(_plainText: String, _cryptUniqueId: String = getCryptUniqueId()): String {
        var k: String = CRYPT_KEY
        var i: String = CRYPT_IV
        k = _cryptUniqueId
        k = SHA256(k, 32) //32 bytes = 256 bit
        i = SHA256(i, 16)
        return encrypt(_plainText, k, i)
    }

    @JvmOverloads
    @Throws(
        InvalidKeyException::class,
        UnsupportedEncodingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        NoSuchAlgorithmException::class
    )
    fun decryptString(_plainText: String, _cryptUniqueId: String = getCryptUniqueId()): String {
        var i: String = CRYPT_IV
        var k = _cryptUniqueId
        k = SHA256(k, 32) //32 bytes = 256  bit
        i = SHA256(i, 16)
        return decrypt(_plainText, k, i)
    }

    /***
     * This funtion decrypts the encrypted text to plain text using the key
     * provided. You'll have to use the same key which you used during
     * encryprtion
     *
     * @param _encryptedText Encrypted/Cipher text to be decrypted
     * @param _key           Encryption key which you used during encryption
     * @param _iv            initialization Vector
     * @return encrypted value
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        InvalidKeyException::class,
        UnsupportedEncodingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun decrypt(_encryptedText: String, _key: String, _iv: String): String {
        return encryptDecrypt(_encryptedText, _key, EncryptMode.DECRYPT, _iv)
    }

    /**
     * Encryption mode enumeration
     */
    private enum class EncryptMode {
        ENCRYPT,
        DECRYPT
    }

    companion object {
        /**
         * Note: This function is no longer used.
         * This function generates md5 hash of the input string
         *
         * @param inputString
         * @return md5 hash of the input string
         */
        fun md5(inputString: String): String {
            val MD5 = "MD5"
            try {
                // Create MD5 Hash
                val digest = MessageDigest
                    .getInstance(MD5)
                digest.update(inputString.toByteArray())
                val messageDigest = digest.digest()

                // Create Hex String
                val hexString = StringBuilder()
                for (aMessageDigest in messageDigest) {
                    var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                    while (h.length < 2) h = "0$h"
                    hexString.append(h)
                }
                return hexString.toString()
            } catch (e: NoSuchAlgorithmException) {
                //e.printStackTrace();
            }
            return ""
        }

        /***
         * This function computes the SHA256 hash of input string
         *
         * @param text   input text whose SHA256 hash has to be computed
         * @param length length of the text to be returned
         * @return returns SHA256 hash of input text
         * @throws NoSuchAlgorithmException
         * @throws UnsupportedEncodingException
         */
        @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
        fun SHA256(text: String, length: Int): String {
            val resultStr: String
            val md = MessageDigest.getInstance("SHA-256")
            md.update(text.toByteArray(StandardCharsets.UTF_8))
            val digest = md.digest()
            val result = StringBuffer()
            for (b in digest) {
                result.append(String.format("%02x", b)) //convert to hex
            }
            //return result.toString();
            resultStr = if (length > result.toString().length) {
                result.toString()
            } else {
                result.toString().substring(0, length)
            }
            return resultStr
        }

        fun bin2hex(data: ByteArray): String {
            return String.format("%0" + data.size * 2 + 'x', BigInteger(1, data))
        }

        /**
         * this function generates random string for given length
         *
         * @param length Desired length
         * * @return
         */
        fun generateRandomIV(length: Int): String {
            val ranGen = SecureRandom()
            val aesKey = ByteArray(16)
            ranGen.nextBytes(aesKey)
            val result = StringBuffer()
            for (b in aesKey) {
                result.append(String.format("%02x", b)) //convert to hex
            }
            return if (length > result.toString().length) {
                result.toString()
            } else {
                result.toString().substring(0, length)
            }
        }
    }
}

