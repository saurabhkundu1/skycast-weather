package com.applify.skycast.settings

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.applify.skycast.forecast.download.WeatherProvider
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Stores the provider choice and encrypts a user's WeatherAPI key with an Android Keystore key. */
class WeatherProviderRepository(
    private val prefs: SharedPreferences
) {
    fun getProvider(): WeatherProvider =
        runCatching { WeatherProvider.valueOf(prefs.getString(PROVIDER_KEY, null) ?: "") }
            .getOrDefault(WeatherProvider.OpenMeteo)

    fun selectProvider(provider: WeatherProvider) {
        prefs.edit { putString(PROVIDER_KEY, provider.name) }
    }

    fun hasWeatherApiKey(): Boolean = prefs.contains(ENCRYPTED_KEY)

    fun getWeatherApiKey(): String? = runCatching {
        val iv = Base64.decode(prefs.getString(IV_KEY, null), Base64.NO_WRAP)
        val encrypted = Base64.decode(prefs.getString(ENCRYPTED_KEY, null), Base64.NO_WRAP)
        Cipher.getInstance(TRANSFORMATION).run {
            init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(128, iv))
            String(doFinal(encrypted), Charsets.UTF_8)
        }
    }.getOrNull()

    fun saveWeatherApiKey(value: String) {
        if (value.isBlank()) {
            clearWeatherApiKey()
            return
        }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val encrypted = cipher.doFinal(value.trim().toByteArray(Charsets.UTF_8))
        prefs.edit {
            putString(IV_KEY, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            putString(ENCRYPTED_KEY, Base64.encodeToString(encrypted, Base64.NO_WRAP))
        }
    }

    fun clearWeatherApiKey() {
        prefs.edit {
            remove(IV_KEY)
            remove(ENCRYPTED_KEY)
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    companion object {
        private const val PROVIDER_KEY = "weather_provider"
        private const val IV_KEY = "weather_api_key_iv"
        private const val ENCRYPTED_KEY = "weather_api_key_ciphertext"
        private const val KEY_ALIAS = "skycast_weather_api_key"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
