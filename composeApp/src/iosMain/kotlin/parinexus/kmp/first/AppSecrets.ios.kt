package parinexus.kmp.first

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSPropertyListImmutable
import platform.Foundation.NSPropertyListSerialization

class IosAppSecrets : AppSecrets {
    override val apiKey: String
        get() = secretsValue("API_KEY")
    override val baseUrl: String
        get() = secretsValue("BASE_URL")
}

@OptIn(ExperimentalForeignApi::class)
private fun secretsValue(key: String): String {
    val path = NSBundle.mainBundle.pathForResource("Secrets", ofType = "plist") ?: return ""
    val data = NSFileManager.defaultManager.contentsAtPath(path) ?: return ""
    val plist = NSPropertyListSerialization.propertyListWithData(
        data = data,
        options = NSPropertyListImmutable,
        format = null,
        error = null,
    )
    val map = plist as? Map<*, *>
    return map?.get(key) as? String ?: ""
}
