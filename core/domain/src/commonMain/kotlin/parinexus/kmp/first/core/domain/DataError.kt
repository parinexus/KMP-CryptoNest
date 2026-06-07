package parinexus.kmp.first.core.domain

sealed interface DataError: Error {
    enum class Remote: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        COIN_NOT_FOUND,
        VALIDATION_ERROR,
        REFERENCE_UNAVAILABLE,
        UNKNOWN,
    }

    enum class Local: DataError {
        DISK_FULL,
        INSUFFICIENT_FUNDS,
        UNKNOWN
    }
}