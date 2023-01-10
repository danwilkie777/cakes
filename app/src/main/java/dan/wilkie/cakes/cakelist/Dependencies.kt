package dan.wilkie.cakes.cakelist

import dan.wilkie.cakes.cakelist.domain.CakeListRepository
import dan.wilkie.cakes.cakelist.domain.CakeListService
import dan.wilkie.cakes.cakelist.domain.CakeListViewModel
import dan.wilkie.cakes.cakelist.network.CakeApi
import dan.wilkie.cakes.cakelist.network.RetrofitCakeListService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val cakeListModule = module {
    factory { get<Retrofit>().create(CakeApi::class.java) }
    factory<CakeListService> { RetrofitCakeListService(get()) }
    factory { CakeListRepository(get()) }
    viewModel { CakeListViewModel(get()) }
}