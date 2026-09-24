package pion.tech.pionbase.flow

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pion.tech.pionbase.R
import pion.tech.pionbase.app.MainActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class WallpaperPickerFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testPickPhotoFromGallery() {
        val resultData = Intent()
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        // Phải đảm bảo match với action bạn gọi trong fragment
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result)

        onView(withId(R.id.btnPickPhoto)).perform(click())
    }
}
