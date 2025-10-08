package com.trackpoint.demo.Config.Utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    private const val SECRET_KEY = "1234567890123456" // 🔐 16 caracteres (128 bits)
    private const val ALGORITHM = "AES"

    fun encrypt(value: String?): String? {
        if (value.isNullOrBlank()) return value
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(encryptedValue: String?): String? {
        if (encryptedValue.isNullOrBlank()) return encryptedValue
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decodedBytes = Base64.getDecoder().decode(encryptedValue)
        val decrypted = cipher.doFinal(decodedBytes)
        return String(decrypted, Charsets.UTF_8)
    }
}