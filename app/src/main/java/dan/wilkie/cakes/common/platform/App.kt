package dan.wilkie.cakes.common.platform

import android.app.Application
import dan.wilkie.cakes.cakelist.cakeListModule
import dan.wilkie.cakes.common.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(commonModule, cakeListModule)
        }
    }
}