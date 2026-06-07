import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.android)
            implementation(libs.room.runtime)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.biometric)
            implementation(libs.accompanist.systemuicontroller)
            implementation(projects.core.network)
            implementation(projects.core.database)
        }
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.network)
            implementation(projects.core.database)
            implementation(projects.core.api)
            implementation(projects.core.ui)
            implementation(projects.core.designsystem)
            implementation(projects.core.navigation)
            implementation(projects.feature.coins)
            implementation(projects.feature.portfolio)
            implementation(projects.feature.trade)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.serialization.json)
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.navigation)
        }
        iosMain.dependencies {
            implementation(libs.ktor.ios)
            implementation(libs.room.runtime)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.core.network)
            implementation(projects.core.database)
        }
        androidInstrumentedTest {
            dependsOn(commonTest.get())
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.junit)
            implementation(libs.test.compose.ui.junit4.android)
            implementation(libs.test.compose.manifest)
            implementation(libs.test.core.ktx)
            implementation(libs.test.androidx.runner)
            implementation(libs.test.androidx.ext.junit)
            implementation(libs.test.androidx.rules)
            implementation(projects.core.ui)
            implementation(projects.feature.coins)
        }
    }
}

android {
    namespace = "parinexus.kmp.first"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "parinexus.kmp.first"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiKey = project.loadLocalProperty(
            path = "local.properties",
            propertyName = "API_KEY",
        )
        val baseUrl = project.loadLocalProperty(
            path = "local.properties",
            propertyName = "BASE_URL",
        )
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

fun Project.loadLocalProperty(
    path: String,
    propertyName: String,
): String {
    val props = Properties()
    val file = rootProject.file(path)
    if (!file.exists()) {
        throw GradleException("Cannot find $path at ${file.absolutePath}")
    }
    file.inputStream().use { props.load(it) }
    return props.getProperty(propertyName)
        ?: throw GradleException("Property '$propertyName' not found in $path")
}
