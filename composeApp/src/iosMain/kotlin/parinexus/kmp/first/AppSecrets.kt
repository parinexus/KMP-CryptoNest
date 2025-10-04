package parinexus.kmp.first

import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.dictionaryWithContentsOfFile

actual object AppSecrets {
    actual val apiKey: String
        get() = requireStringResource("Secrets", "plist", "API_KEY")
    actual val baseUrl: String
        get() = requireStringResource("Secrets", "plist", "BASE_URL")
}

internal fun getStringResource(
    filename: String,
    fileType: String,
    valueKey: String,
): String? {
    val result = NSBundle.mainBundle.pathForResource(filename, fileType)?.let {
        val map = NSDictionary.dictionaryWithContentsOfFile(it)
        (map?.get(valueKey))?.toString()
    }
    return result
}

private fun requireStringResource(
    filename: String,
    fileType: String,
    valueKey: String
): String =
    getStringResource(filename, fileType, valueKey)
        ?: error("Missing $valueKey in $filename.$fileType (not in bundle?)")
