package com.rapidsos.era

import android.support.test.InstrumentationRegistry.getInstrumentation
import com.rapidsos.era.authentication.password_reset.view.PasswordResetActivityTest
import com.rapidsos.era.helpers.dependency_injection.component.DIComponent
import com.rapidsos.era.helpers.dependency_injection.modules.*
import com.rapidsos.era.splash_screen.view.AuthActivityTest
import dagger.Component
import org.junit.Before
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
open class BaseAndroidTest {

    private val app = getInstrumentation().targetContext.applicationContext as TestApplication

    lateinit var component: AndroidTestComponent

    @Singleton
    @Component(modules = [EraPreferencesModule::class, UtilsModule::class, DatabaseModule::class,
        MidasModule::class, AndroidModule::class, NotificationModule::class, BluetoothModule::class,
        LogOutModule::class, ProfilePhotoModule::class])
    interface AndroidTestComponent : DIComponent {
        fun inject(authActivityTest: AuthActivityTest)
        fun inject(passwordResetActivityTest: PasswordResetActivityTest)
    }

    @Before
    fun setup() {
        component = DaggerBaseAndroidTest_AndroidTestComponent.builder()
                .eraPreferencesModule(EraPreferencesModule(app))
                .utilsModule(UtilsModule(app))
                .databaseModule(DatabaseModule(app))
                .midasModule(MidasModule(app))
                .androidModule(AndroidModule(app))
                .notificationModule(NotificationModule(app))
                .bluetoothModule(BluetoothModule(app))
                .logOutModule(LogOutModule(app))
                .profilePhotoModule(ProfilePhotoModule(app))
                .build()
    }
}


