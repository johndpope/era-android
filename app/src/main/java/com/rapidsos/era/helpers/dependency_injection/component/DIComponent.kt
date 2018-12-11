package com.rapidsos.era.helpers.dependency_injection.component

import com.rapidsos.era.authentication.auth_activity.view.AuthenticationActivity
import com.rapidsos.era.authentication.login.presenter.LoginPresenterImpl
import com.rapidsos.era.authentication.login.view.LoginFragment
import com.rapidsos.era.authentication.password_reset.view.PasswordResetActivity
import com.rapidsos.era.authentication.phone_number.presenter.PhoneNumberPresenterImpl
import com.rapidsos.era.authentication.phone_number.view.PhoneNumberActivity
import com.rapidsos.era.authentication.register.presenter.RegisterPresenterImpl
import com.rapidsos.era.authentication.register.view.RegisterFragment
import com.rapidsos.era.connected_devices.presenter.ConnectedDevicesPresenterImpl
import com.rapidsos.era.connected_devices.view.ConnectedDevicesFragment
import com.rapidsos.era.emergency_contacts.presenter.EmergencyContactsPresenterImpl
import com.rapidsos.era.emergency_contacts.view.EmergencyContactsActivity
import com.rapidsos.era.helpers.dependency_injection.modules.*
import com.rapidsos.era.helpers.log_out.LogOutDialog
import com.rapidsos.era.main.view.MainActivity
import com.rapidsos.era.profile.edit_profile.presenter.EditProfilePresenterImpl
import com.rapidsos.era.profile.edit_profile.view.EditProfileActivity
import com.rapidsos.era.profile.service.ProfileService
import com.rapidsos.era.profile.view.ProfileFragment
import com.rapidsos.era.settings.view.SettingsActivity
import com.rapidsos.era.splash_screen.view.SplashActivity
import com.rapidsos.shared.notification.PanicNotificationHandler
import dagger.Component
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Singleton
@Component(modules = [EraPreferencesModule::class, UtilsModule::class, DatabaseModule::class,
    MidasModule::class, AndroidModule::class, NotificationModule::class, BluetoothModule::class,
    LogOutModule::class])
interface DIComponent {
    fun inject(splashScreenPresenterImpl: LoginPresenterImpl)
    fun inject(authenticationActivity: AuthenticationActivity)
    fun inject(registerFragment: RegisterFragment)
    fun inject(splashActivity: SplashActivity)
    fun inject(passwordResetActivity: PasswordResetActivity)
    fun inject(registerPresenterImpl: RegisterPresenterImpl)
    fun inject(phoneNumberActivity: PhoneNumberActivity)
    fun inject(phoneNumberPresenterImpl: PhoneNumberPresenterImpl)
    fun inject(mainActivity: MainActivity)
    fun inject(loginFragment: LoginFragment)
    fun inject(editProfilePresenterImpl: EditProfilePresenterImpl)
    fun inject(editProfileActivity: EditProfileActivity)
    fun inject(emergencyContactsPresenterImpl: EmergencyContactsPresenterImpl)
    fun inject(emergencyContactsActivity: EmergencyContactsActivity)
    fun inject(panicNotificationHandler: PanicNotificationHandler)
    fun inject(settingsActivity: SettingsActivity)
    fun inject(connectedDevicesPresenterImpl: ConnectedDevicesPresenterImpl)
    fun inject(connectedDevicesFragment: ConnectedDevicesFragment)
    fun inject(profileService: ProfileService)
    fun inject(profileFragment: ProfileFragment)
    fun inject(logOutDialog: LogOutDialog)
}