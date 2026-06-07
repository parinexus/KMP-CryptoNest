package parinexus.kmp.first.core.network

/**
 * Lightweight network logger (Logcat on Android, console on iOS/desktop).
 */
object NetworkLogger {
    private const val TAG = "CryptoNest/Network"

    fun d(message: String) {
        println("D/$TAG: $message")
    }

    fun e(message: String, cause: Throwable? = null) {
        println("E/$TAG: $message")
        cause?.printStackTrace()
    }

    fun maskSecret(value: String, visibleTail: Int = 4): String {
        if (value.length <= visibleTail) return "***"
        return "***${value.takeLast(visibleTail)}"
    }
}
