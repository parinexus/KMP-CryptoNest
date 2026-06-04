package parinexus.kmp.first.core.serialization

import kotlinx.serialization.json.Json

/**
 * Shared kotlinx.serialization config for network, API errors, and Room JSON cache.
 */
object AppJson {
    val instance: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
}
