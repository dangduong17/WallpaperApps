package pion.tech.pionbase.flow

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pion.tech.pionbase.feature.home.EditWallpaperActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class WallpaperFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(EditWallpaperActivity::class.java)

    @Test
    fun testEditWallpaperFlow_InvalidUri_FinishesGracefully() {
        // Test trường hợp không có URI (URI không hợp lệ)
        // Activity sẽ gọi finish() và test sẽ kiểm tra activity đã đóng hay chưa
        activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}
