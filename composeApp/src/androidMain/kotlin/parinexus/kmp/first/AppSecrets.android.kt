package parinexus.kmp.first

import parinexus.kmp.first.BuildConfig.API_KEY
import parinexus.kmp.first.BuildConfig.BASE_URL

actual object AppSecrets {
    actual val apiKey: String
        get() = API_KEY
    actual val baseUrl: String
        get() = BASE_URL
}
