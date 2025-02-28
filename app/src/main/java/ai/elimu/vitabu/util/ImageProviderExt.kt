package ai.elimu.vitabu.util

import ai.elimu.vitabu.BuildConfig
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.WorkerThread
import java.io.ByteArrayOutputStream
import java.io.IOException

@WorkerThread
suspend fun Context.readImageBytes(fileId: Long): ByteArray? {
    val uri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
            + ".provider.image_provider/images/")

    val imageUri = ContentUris.withAppendedId(uri, fileId)

    try {
        contentResolver.openInputStream(imageUri).use { inputStream ->
            ByteArrayOutputStream().use { byteBuffer ->
                if (inputStream == null) return null
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                    byteBuffer.write(buffer, 0, bytesRead)
                }
                return byteBuffer.toByteArray()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("readImageBytes", "exception: " + e.message)
        return null
    }
}