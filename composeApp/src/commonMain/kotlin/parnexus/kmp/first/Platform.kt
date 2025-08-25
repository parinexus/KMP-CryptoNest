package parnexus.kmp.first

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform