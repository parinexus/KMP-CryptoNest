package parinexus.kmp.first.core.network.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import parinexus.kmp.first.core.network.HttpClientFactory
import parinexus.kmp.first.core.serialization.AppJson

val networkModule: Module = module {
    single<Json> { AppJson.instance }
    single<HttpClient> { HttpClientFactory.create(get(), get()) }
}
