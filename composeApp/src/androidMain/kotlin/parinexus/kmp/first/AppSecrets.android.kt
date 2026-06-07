package parinexus.kmp.first

import parinexus.kmp.first.BuildConfig.API_KEY
import parinexus.kmp.first.BuildConfig.BASE_URL

class AndroidAppSecrets : AppSecrets {
    override val apiKey: String get() = API_KEY
    override val baseUrl: String get() = BASE_URL
}
