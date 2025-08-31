package parinexus.kmp.first

actual object AppSecrets {
    actual val apiKey: String
        get() = BuildConfig.API_KEY
}
