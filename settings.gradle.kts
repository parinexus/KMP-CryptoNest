rootProject.name = "KMP-CryptoNest"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")

include(":core:domain")
include(":core:network")
include(":core:database")
include(":core:api")
include(":core:designsystem")
include(":core:ui")
include(":core:navigation")
include(":core:testing")

include(":feature:coins-api")
include(":feature:coins")
include(":feature:portfolio")
include(":feature:trade")
