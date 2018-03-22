package com.rapidsos.rain.personal_info

import com.rapidsos.rain.api.RainApi
import com.rapidsos.rain.data.network_response.GetProfileUrlResponseFields
import com.rapidsos.rain.data.profile.Photo
import com.rapidsos.rain.data.profile.values.PhotoValue
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.util.*

/**
 * @author Josias Sena
 */
class PersonalInfoPhotoHandler(private val api: RainApi) {

    private lateinit var observer: SingleObserver<Photo>

    private var finalPhotoUrl: String = ""

    /**
     * Upload a photo.
     *
     * Example:
     *
     * ```
     * fun uploadPic(profilePicFile: File) {
     *       personalInfoManager.uploadProfilePicture(profilePicFile, object : SingleObserver<Photo> {
     *          override fun onSubscribe(disposable: Disposable) {
     *             this.disposable = disposable
     *          }
     *
     *          override fun onSuccess(photo: Photo) {
     *             // do something with the photo
     *          }
     *
     *          override fun onError(error: Throwable) {
     *             // do something on error
     *          }
     *       })
     *   }
     * ```
     *
     * Make sure to call `disposable.dispose()` when you are done.
     *
     * @param pictureFile the file that will get uploaded
     * @param observer the listener/observer that will get notified if anything goes wrong, and
     * when the photo has been successfully uploaded. On successful upload a [Photo] will be returned
     */
    fun uploadProfilePicture(pictureFile: File, observer: SingleObserver<Photo>) {
        this.observer = observer

        val randomUUID = UUID.randomUUID().toString()
        val fileName = UUID.nameUUIDFromBytes(randomUUID.toByteArray()).toString()
                .replace("\\W".toRegex(), "")

        api.getProfilePictureUploadUrl(fileName)
                .subscribeOn(Schedulers.io())
                .filter { response -> filter(response) }
                .flatMapSingle { response ->
                    val responseBody = response.body()
                    val fields = responseBody?.fields as GetProfileUrlResponseFields
                    val uploadUrl = responseBody.url

                    finalPhotoUrl = "$uploadUrl${fields.key}"

                    val image = getPictureAsMultiData(pictureFile)

                    // Set all of the AWS fields required for uploading photo as form-data
                    val fieldsMediaType = MediaType.parse("form-data")
                    val keyId = RequestBody.create(fieldsMediaType, fields.awsAccessKeyId)
                    val key = RequestBody.create(fieldsMediaType, fields.key)
                    val policy = RequestBody.create(fieldsMediaType, fields.policy)
                    val signature = RequestBody.create(fieldsMediaType, fields.signature)
                    val securityToken = RequestBody.create(fieldsMediaType, fields.xAmzSecurityToken)

                    api.uploadPic(uploadUrl, keyId, key, policy, signature, securityToken, image)
                }
                .filter { response -> filter(response) }
                .map {
                    val photoValue = PhotoValue().apply {
                        this.url = finalPhotoUrl
                        this.label = pictureFile.name
                    }

                    Photo().apply {
                        this.displayName = pictureFile.name
                        this.value = arrayListOf(photoValue)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ photo ->
                    observer.onSuccess(photo)
                }, { throwable: Throwable? ->
                    throwable?.let { observer.onError(it) }
                })
    }

    /**
     * Parse a picture file as form data of mime type image/x
     *
     * @param pictureFile the file containing the image
     */
    private fun getPictureAsMultiData(pictureFile: File): MultipartBody.Part {
        val mediaType = MediaType.parse("image/*")
        val requestFile = RequestBody.create(mediaType, pictureFile)
        return MultipartBody.Part.createFormData("file", pictureFile.name, requestFile)
    }

    private fun <T> filter(response: Response<T>): Boolean {
        return if (response.isSuccessful) {
            true
        } else {
            val errorBody = response.errorBody()?.string()
            val message = response.message()
            val code = response.code()

            observer.onError(Throwable("$code $message $errorBody"))
            false
        }
    }
}