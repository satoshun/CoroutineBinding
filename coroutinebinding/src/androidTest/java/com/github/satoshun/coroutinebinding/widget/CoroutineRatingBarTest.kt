package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.widget.RatingBar
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineRatingBarTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var ratingBar: RatingBar

  @Before @UiThreadTest
  fun setUp() {
    ratingBar = RatingBar(rule.activity)
    rule.activity.view.addView(ratingBar)
  }

  @Test
  fun ratingChanges() = runBlocking<Unit> {
    val ratingChanges = uiRunBlocking { ratingBar.ratingChanges() }

    uiLaunch { ratingBar.rating = 1f }
    ratingChanges.receiveOrNull().isEqualTo(1f)

    uiLaunch { ratingBar.rating = 3f }
    ratingChanges.receiveOrNull().isEqualTo(3f)

    ratingChanges.cancel()
    uiLaunch { ratingBar.rating = 5f }
    ratingChanges.receiveOrNull().isNull()
  }

  @Test
  fun ratingChangeEvents() = runBlocking<Unit> {
    val ratingChangeEvents = uiRunBlocking { ratingBar.ratingChangeEvents() }

    uiLaunch { ratingBar.rating = 1f }
    ratingChangeEvents.receive().rating.isEqualTo(1f)

    uiLaunch { ratingBar.rating = 3f }
    ratingChangeEvents.receive().rating.isEqualTo(3f)

    ratingChangeEvents.cancel()
    uiLaunch { ratingBar.rating = 5f }
    ratingChangeEvents.receiveOrNull().isNull()
  }
}
