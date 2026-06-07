package parinexus.kmp.first.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import parinexus.kmp.first.AppInitializer

val appModule = module {
    singleOf(::AppInitializer)
}
