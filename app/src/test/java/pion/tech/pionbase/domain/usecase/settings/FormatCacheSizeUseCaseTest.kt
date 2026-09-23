package pion.tech.pionbase.domain.usecase.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatCacheSizeUseCaseTest {

    private val formatCacheSizeUseCase = FormatCacheSizeUseCase()

    @Test
    fun formatCacheSize_zeroBytes_returnsZeroB() {
        val result = formatCacheSizeUseCase(0L)
        assertEquals("0 B", result)
    }

    @Test
    fun formatCacheSize_negativeBytes_returnsZeroB() {
        val result = formatCacheSizeUseCase(-100L)
        assertEquals("0 B", result)
    }

    @Test
    fun formatCacheSize_bytes_returnsCorrectFormat() {
        val result = formatCacheSizeUseCase(500L)
        assertEquals("500 B", result)
    }

    @Test
    fun formatCacheSize_kilobytes_returnsCorrectFormat() {
        val result = formatCacheSizeUseCase(1536L) // 1.5 KB
        assertEquals("1.5 KB", result)
    }

    @Test
    fun formatCacheSize_megabytes_returnsCorrectFormat() {
        val result = formatCacheSizeUseCase(12582912L) // 12 MB
        assertEquals("12 MB", result)
    }
}
