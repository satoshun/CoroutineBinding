package com.github.satoshun.coroutinebinding.view

import android.view.View
import android.view.ViewGroup
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isInstanceOf
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Test

class CoroutineViewGroupTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private val view: ViewGroup get() = rule.activity.view

  @Test
  fun changeEvents() = testRunBlocking {
    val view = view
    val child = uiRunBlocking { View(rule.activity) }
    val changeEvents = view.changeEvents(1)

    uiLaunch { view.addView(child) }
    changeEvents.receive().isInstanceOf(ViewGroupHierarchyChildViewAddEvent::class)

    uiLaunch { view.removeView(child) }
    changeEvents.receive().isInstanceOf(ViewGroupHierarchyChildViewRemoveEvent::class)

    changeEvents.cancel()
    uiLaunch { view.addView(child) }
    changeEvents.poll().isNull()
  }

  @Test
  fun awaitChangeEvent() = testRunBlocking {
    val view = view
    val child = uiRunBlocking { View(rule.activity) }

    val job = uiLaunch {
      view.awaitChangeEvent().isInstanceOf(ViewGroupHierarchyChildViewAddEvent::class)
    }
    uiRunBlocking { view.addView(child) }
    job.join()
    job.isCompleted.isTrue()

    val job2 = uiLaunch {
      view.awaitChangeEvent().isInstanceOf(ViewGroupHierarchyChildViewRemoveEvent::class)
    }
    uiRunBlocking { view.removeView(child) }
    job2.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { view.awaitChangeEvent() }
    cancelJob.cancel()
    uiRunBlocking { view.addView(child) }
    cancelJob.isCancelled.isTrue()
  }
}
