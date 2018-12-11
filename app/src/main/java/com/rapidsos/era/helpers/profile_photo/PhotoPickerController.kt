package com.rapidsos.era.helpers.profile_photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.mlsdev.rximagepicker.RxImageConverters
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.rapidsos.androidutils.FileUtils
import com.rapidsos.androidutils.resize
import com.rapidsos.androidutils.rotateIfNeeded
import com.rapidsos.androidutils.saveToDisk
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import java.io.File

/**
 * @author Josias Sena
 */
class PhotoPickerController(private val context: Context) : AnkoLogger {

    data class FileBitmap(var file: File? = null, var bitmap: Bitmap? = null)

    private val fileBitmap = FileBitmap()

    private lateinit var observer: SingleObserver<FileBitmap>

    companion object {
        private const val FILE_NAME = "era_profile_pic.jpg"
    }

    /**
     * Opens up the gallery and camera on the device depending on the source. On success a
     * [FileBitmap] is returned. This object holds a file of where the photo is located, and a
     * bitmap.
     *
     * @param sources the source where the photo will be fetched from
     * @param observer the observer to be notified when the photo is available or
     * when an error occurs
     *
     * @see Sources.GALLERY
     * @see Sources.CAMERA
     */
    fun choosePhotoFromSource(sources: Sources, observer: SingleObserver<FileBitmap>) {
        this.observer = observer

        when (sources) {
            Sources.GALLERY -> {
                handleChoosingPhotoFromGallery(sources)
            }
            Sources.CAMERA -> {
                handleUsingTheCamera(sources)
            }
        }
    }

    private fun handleChoosingPhotoFromGallery(sources: Sources) {
        RxImagePicker.with(context)
                .requestImage(sources)
                .subscribeOn(Schedulers.io())
                .flatMap { uri: Uri ->
                    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val file = File(filesDir, FILE_NAME)
                    RxImageConverters.uriToFile(context, uri, file)
                }
                .map {
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888

                    val bitmap = BitmapFactory.decodeFile(it.absolutePath, options)

                    fileBitmap.file = bitmap.rotateIfNeeded(it)
                            .resize(300, 300)
                            .saveToDisk(context, FILE_NAME)

                    FileUtils.getFileAsBitmap(context, FILE_NAME)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bitmap: Bitmap? ->
                    fileBitmap.bitmap = bitmap

                    observer.onSuccess(fileBitmap)
                }, { throwable: Throwable? ->
                    throwable?.let { observer.onError(it) }
                })
    }

    private fun handleUsingTheCamera(sources: Sources) {
        RxImagePicker.with(context)
                .requestImage(sources)
                .subscribeOn(Schedulers.io())
                .flatMap { uri: Uri ->
                    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val file = File(filesDir, FILE_NAME)
                    RxImageConverters.uriToFile(context, uri, file)
                }
                .map {
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888

                    val bitmap = BitmapFactory.decodeFile(it.absolutePath, options)

                    fileBitmap.file = bitmap.rotateIfNeeded(it)
                            .resize(300, 300)
                            .saveToDisk(context, FILE_NAME)

                    FileUtils.getFileAsBitmap(context, FILE_NAME)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bitmap: Bitmap? ->
                    bitmap?.let {
                        fileBitmap.bitmap = bitmap
                        observer.onSuccess(fileBitmap)
                    }
                }, { throwable: Throwable? ->
                    throwable?.let { observer.onError(it) }
                })
    }
}