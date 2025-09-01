package parinexus.kmp.first

actual object AppSecrets {
    actual val apiKey: String
        get() = BuildConfig.API_KEY
    actual val baseUrl: String
        get() = BuildConfig.BASE_URL
}
