package pion.tech.pionbase.domain.usecase.settings

import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

class FormatCacheSizeUseCase {
    operator fun invoke(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt().coerceIn(0, units.size - 1)
        return String.format(
            Locale.US,
            "%.1f %s",
            bytes / 1024.0.pow(digitGroups.toDouble()),
            units[digitGroups],
        ).replace(".0 ", " ")
    }
}
