package dan.wilkie.cakes.common

import dan.wilkie.cakes.common.network.RetrofitFactory
import org.koin.dsl.module

val commonModule = module {
    single { RetrofitFactory().createRetrofit() }
}