package pion.tech.pionbase.util

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import pion.tech.pionbase.R

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // Tối ưu disk cache lên 250MB
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, 1024L * 1024L * 250L))
        
        // Tối ưu mặc định cho request: Cache tất cả + format RGB_565 (tiết kiệm 50% RAM so với ARGB_8888)
        builder.setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(DecodeFormat.PREFER_RGB_565)
        )
    }

    // Tắt kiểm tra manifest để tăng tốc độ khởi tạo
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
