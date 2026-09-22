package pion.tech.pionbase.util

import android.content.ContentResolver
import android.net.Uri

fun Uri.isGif(contentResolver: ContentResolver): Boolean {
    val mimeType = contentResolver.getType(this)
    if ((mimeType == "image/gif") || toString().endsWith(".gif", ignoreCase = true)) {
        return true
    }
    return try {
        contentResolver.openInputStream(this)?.use { input ->
            val buffer = ByteArray(3)
            val bytesRead = input.read(buffer, 0, 3)
            bytesRead == 3 && buffer[0] == 'G'.code.toByte() && buffer[1] == 'I'.code.toByte() && buffer[2] == 'F'.code.toByte()
        } ?: false
    } catch (_: Exception) {
        false
    }
}
