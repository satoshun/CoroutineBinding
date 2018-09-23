package com.github.satoshun.coroutinebinding.widget

import android.widget.RatingBar
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineRatingBarTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var ratingBar: RatingBar

  @Before @UiThreadTest
  fun setUp() {
    ratingBar = RatingBar(rule.activity)
    rule.activity.view.addView(ratingBar)
  }

  @Test
  fun ratingChanges() = testRunBlocking {
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
  fun awaitRatingChange() = testRunBlocking {
    val job = uiLaunch { ratingBar.awaitRatingChange().isEqualTo(1f) }
    uiLaunch { ratingBar.rating = 1f }
    job.joinAndIsCompleted()

    val cancelJob = uiLaunch { ratingBar.awaitRatingChange() }
    cancelJob.cancel()
    uiRunBlocking { ratingBar.rating = 5f }
    cancelJob.isCancelled.isTrue()
  }

  @Test
  fun ratingChangeEvents() = testRunBlocking {
    val ratingChangeEvents = uiRunBlocking { ratingBar.ratingChangeEvents() }

    uiLaunch { ratingBar.rating = 1f }
    ratingChangeEvents.receive().rating.isEqualTo(1f)

    uiLaunch { ratingBar.rating = 3f }
    ratingChangeEvents.receive().rating.isEqualTo(3f)

    ratingChangeEvents.cancel()
    uiLaunch { ratingBar.rating = 5f }
    ratingChangeEvents.receiveOrNull().isNull()
  }

  @Test
  fun awaitRatingChangeEvent() = testRunBlocking {
    val job = uiLaunch { ratingBar.awaitRatingChangeEvent().rating.isEqualTo(1f) }
    uiLaunch { ratingBar.rating = 1f }
    job.joinAndIsCompleted()

    val cancelJob = uiLaunch { ratingBar.awaitRatingChangeEvent() }
    cancelJob.cancel()
    uiRunBlocking { ratingBar.rating = 5f }
    cancelJob.isCancelled.isTrue()
  }
}
