package pion.tech.pionbase.util

import android.content.ContentResolver
import android.net.Uri

fun Uri.isGif(contentResolver: ContentResolver): Boolean {
    val mimeType = contentResolver.getType(this)
    return mimeType == "image/gif" || toString().endsWith(".gif")
}
