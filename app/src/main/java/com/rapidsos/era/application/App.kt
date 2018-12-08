package com.rapidsos.era.application

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.rapidsos.emergencydatasdk.sdk.EraSdk
import com.rapidsos.era.BuildConfig
import com.rapidsos.era.helpers.dependency_injection.component.DIComponent
import com.rapidsos.era.helpers.dependency_injection.component.DaggerDIComponent
import com.rapidsos.era.helpers.dependency_injection.modules.*
import io.fabric.sdk.android.Fabric

/**
 * @author Josias Sena
 */
open class App : Application() {

    var host = "https://api-sandbox.rapidsos.com/"

    companion object {
        lateinit var component: DIComponent

        // These credentials are provided by RapidSOS
        const val CLIENT_ID = BuildConfig.ERA_CLIENT_ID
        const val CLIENT_SECRET = BuildConfig.ERA_CLIENT_SECRET
    }

    override fun onCreate() {
        super.onCreate()

        initObjectGraph()

        initCrashlytics()

        initEraSdk()
    }

    /**
     * Initialize the Dagger object graph
     */
    private fun initObjectGraph() {
        component = DaggerDIComponent.builder()
                .eraPreferencesModule(EraPreferencesModule(applicationContext))
                .utilsModule(UtilsModule(applicationContext))
                .databaseModule(DatabaseModule(applicationContext))
                .midasModule(MidasModule(applicationContext))
                .androidModule(AndroidModule(applicationContext))
                .notificationModule(NotificationModule(applicationContext))
                .bluetoothModule(BluetoothModule(applicationContext))
                .logOutModule(LogOutModule(applicationContext))
                .profilePhotoModule(ProfilePhotoModule(applicationContext))
                .build()
    }

    /**
     * Initialize the Crashlytics SDK
     */
    private fun initCrashlytics() {
        Fabric.with(applicationContext, Crashlytics())
    }

    /**
     * Initialize the ERA SDK
     */
    private fun initEraSdk() {
        EraSdk(applicationContext).initialize(host, CLIENT_ID, CLIENT_SECRET)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(applicationContext)
    }
}