package com.example

import android.graphics.BitmapFactory
import android.os.SystemClock
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToString
import androidx.compose.ui.test.swipeUp
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp-xxhdpi")
class InteractionPerformanceDebugTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun profileAllNavigationAndButtonsRespondImmediately() {
        composeTestRule.waitForIdle()

        // 1. Top bar CTA, Quote strip & Hero Drop The Phone
        val heroStart = SystemClock.elapsedRealtime()
        composeTestRule.onNodeWithTag("quote_strip_top").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("drop_phone_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("reset_hero_button").performScrollTo().assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        val heroElapsed = SystemClock.elapsedRealtime() - heroStart
        println("PROFILE_HERO_MS=$heroElapsed")

        // 2. Floating Music Player (single-thread off-main-thread AudioTrack)
        val musicStart = SystemClock.elapsedRealtime()
        composeTestRule.onNodeWithTag("music_drawer_toggle").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("music_track_0").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("music_play_pause_button").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("music_drawer_toggle").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("music_stop_button").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        val musicElapsed = SystemClock.elapsedRealtime() - musicStart
        println("PROFILE_MUSIC_MS=$musicElapsed")

        // 3. Navigate across all 5 bottom tabs and measure latency
        val navStart = SystemClock.elapsedRealtime()
        composeTestRule.onNodeWithTag("nav_lab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("doom_start_button").assertIsDisplayed()

        // Start Doom Test, engage post, scroll feed, stop & verify results summary
        composeTestRule.onNodeWithTag("doom_start_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("doom_scroll_score_live").assertIsDisplayed()
        composeTestRule.onNodeWithTag("doom_like_0").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("doom_feed_list").performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("doom_stop_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("doom_results_summary").assertIsDisplayed()
        composeTestRule.onNodeWithTag("doom_scroll_score_result").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("doom_reset_score_fab").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("doom_scroll_score_result").assertTextContains("0/100")
        composeTestRule.onNodeWithTag("doom_scroll_score_live").performScrollTo().assertTextContains("Score 0")

        // Switch Lab tabs
        composeTestRule.onNodeWithTag("lab_tab_NPA_LAB").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("lab_tab_STORYTELLING").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("lab_tab_DOOM_TEST").performClick()
        composeTestRule.waitForIdle()

        // Challenge screen
        composeTestRule.onNodeWithTag("nav_challenge").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("challenge_option_1d").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("pledge_name_input").performScrollTo().performTextInput("Rangga")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("submit_pledge_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Detox screen
        composeTestRule.onNodeWithTag("nav_detox").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("detox_start_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("detox_pause_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("detox_reset_button").performClick()
        composeTestRule.waitForIdle()

        // Team screen (verifies 7 downsampled team photos load without OOM or UI hang)
        val teamStart = SystemClock.elapsedRealtime()
        composeTestRule.onNodeWithTag("nav_team").performClick()
        composeTestRule.waitForIdle()
        val teamElapsed = SystemClock.elapsedRealtime() - teamStart
        println("PROFILE_TEAM_SCREEN_MS=$teamElapsed")

        // Topbar Mulai NPA button
        composeTestRule.onNodeWithTag("topbar_mulai_button").performClick()
        composeTestRule.waitForIdle()

        // Back Home
        composeTestRule.onNodeWithTag("nav_home").performClick()
        composeTestRule.waitForIdle()
        val navElapsed = SystemClock.elapsedRealtime() - navStart
        println("PROFILE_NAV_ALL_SCREENS_MS=$navElapsed")

        // 4. Floating Otter Pet -> Nopi Gemini Chat Sheet
        val chatStart = SystemClock.elapsedRealtime()
        composeTestRule.onNodeWithTag("floating_otter_chat_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("nopi_chat_sheet").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_role_quick_buddy").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("chat_input_field").performTextInput("Halo Nopi")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("chat_close_button").performClick()
        composeTestRule.waitForIdle()
        val chatElapsed = SystemClock.elapsedRealtime() - chatStart
        println("PROFILE_CHAT_MS=$chatElapsed")
    }

    @Test
    fun profileDrawableDecodeMemoryCost() {
        val resources = composeTestRule.activity.resources
        val drawables = listOf(
            "img_hero_before" to R.drawable.img_hero_before,
            "img_hero_after" to R.drawable.img_hero_after,
            "img_nopi_mascot" to R.drawable.img_nopi_mascot,
            "team_rafli" to R.drawable.team_rafli,
            "team_hansof" to R.drawable.team_hansof,
            "team_ody" to R.drawable.team_ody,
            "team_cecil" to R.drawable.team_cecil,
            "team_fey" to R.drawable.team_fey,
            "team_rangga" to R.drawable.team_rangga,
            "team_sabin" to R.drawable.team_sabin
        )
        var totalRawBytes = 0L
        val t0 = SystemClock.elapsedRealtime()
        for ((name, resId) in drawables) {
            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeResource(resources, resId, opts)
            val rawBytes = opts.outWidth.toLong() * opts.outHeight.toLong() * 4L
            totalRawBytes += rawBytes
            println("PROFILE_DRAWABLE $name: ${opts.outWidth}x${opts.outHeight} rawMB=${rawBytes / (1024f * 1024f)}")
        }
        val boundsMs = SystemClock.elapsedRealtime() - t0

        // Measure full unscaled decode vs inSampleSize downsampled decode
        val tFull = SystemClock.elapsedRealtime()
        var fullDecodedBytes = 0L
        for ((_, resId) in drawables) {
            val bmp = BitmapFactory.decodeResource(resources, resId)
            fullDecodedBytes += bmp.allocationByteCount.toLong()
            bmp.recycle()
        }
        val fullDecodeMs = SystemClock.elapsedRealtime() - tFull

        val tSampled = SystemClock.elapsedRealtime()
        var sampledDecodedBytes = 0L
        for ((_, resId) in drawables) {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeResource(resources, resId, bounds)
            var sample = 1
            while (bounds.outWidth / sample > 720 || bounds.outHeight / sample > 720) {
                sample *= 2
            }
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            val bmp = BitmapFactory.decodeResource(resources, resId, opts)
            sampledDecodedBytes += bmp.allocationByteCount.toLong()
            bmp.recycle()
        }
        val sampledDecodeMs = SystemClock.elapsedRealtime() - tSampled

        println(
            "PROFILE_DECODE boundsMs=$boundsMs fullMB=${fullDecodedBytes / (1024f * 1024f)} " +
                "fullDecodeMs=$fullDecodeMs sampledMB=${sampledDecodedBytes / (1024f * 1024f)} " +
                "sampledDecodeMs=$sampledDecodeMs"
        )
        assertTrue(totalRawBytes > 0L)
    }
}
