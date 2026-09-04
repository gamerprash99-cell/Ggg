package com.example.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object MediaStorageHelper {

    fun createImageFileUri(context: Context): Pair<Uri, String> {
        val photosDir = File(context.filesDir, "photos").apply { if (!exists()) mkdirs() }
        val file = File(photosDir, "photo_${System.currentTimeMillis()}.jpg")
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, file)
        return Pair(uri, file.absolutePath)
    }

    fun createVideoFileUri(context: Context): Pair<Uri, String> {
        val videosDir = File(context.filesDir, "videos").apply { if (!exists()) mkdirs() }
        val file = File(videosDir, "video_${System.currentTimeMillis()}.mp4")
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, file)
        return Pair(uri, file.absolutePath)
    }

    fun copyUriToInternalStorage(context: Context, sourceUri: Uri, subFolder: String, prefix: String, extension: String): String? {
        return try {
            val dir = File(context.filesDir, subFolder).apply { if (!exists()) mkdirs() }
            val destFile = File(dir, "${prefix}_${System.currentTimeMillis()}.$extension")
            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val outputStream = FileOutputStream(destFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteFile(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }
}
