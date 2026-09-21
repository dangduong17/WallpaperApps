package pion.tech.pionbase.domain.usecase.home

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.content.Context
import pion.tech.pionbase.util.Result

class DownloadImageToBitmapUseCase(
    private val context: Context,
) {
    operator fun invoke(url: String): Flow<Result<Bitmap>> = callbackFlow {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    trySend(Result.Success(resource))
                    close()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Ignored
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    trySend(Result.Error(Exception("Failed to download image")))
                    close()
                }
            })
        awaitClose { }
    }
}
