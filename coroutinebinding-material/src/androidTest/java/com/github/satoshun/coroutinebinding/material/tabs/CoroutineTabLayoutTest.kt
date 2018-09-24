package com.github.satoshun.coroutinebinding.material.tabs

import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isFalse
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.material.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.experimental.delay
import org.junit.Before
import org.junit.Test

class CoroutineTabLayoutTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var view: TabLayout
  private lateinit var tab1: TabLayout.Tab
  private lateinit var tab2: TabLayout.Tab

  @Before @UiThreadTest
  fun setUp() {
    view = TabLayout(rule.activity)
    tab1 = view.newTab()
    tab2 = view.newTab()
    tab1.select()
  }

  @Test
  fun selections() = testRunBlocking {
    val selections = uiRunBlocking { view.selections(1) }

    uiRunBlocking { tab2.select() }
    selections.receive().isSame(tab2)

    // reselection
    uiRunBlocking {
      tab2.select()
      delay(100)
    }
    selections.poll().isNull()

    selections.cancel()
    uiRunBlocking { tab1.select() }
    selections.receiveOrNull().isNull()
  }

  @Test
  fun awaitSelection() = testRunBlocking {
    val job = uiLaunch {
      view.awaitSelection().isSame(tab2)
    }
    uiLaunch { tab2.select() }
    job.joinAndIsCompleted()

    // reselection
    val job2 = uiLaunch {
      view.awaitSelection()
    }
    uiLaunch {
      tab2.select()
      delay(100)
    }
    job2.isCompleted.isFalse()

    val cancelJob = toBeCancelLaunch {
      view.awaitSelection()
    }
    uiRunBlocking { tab1.select() }
    cancelJob.isCancelled.isTrue()
  }

  @Test
  fun selectionEvents() = testRunBlocking {
    val selectionEvents = uiRunBlocking { view.selectionEvents(2) }

    uiRunBlocking { tab2.select() }
    selectionEvents.receive().isEqualTo(
        TabLayoutSelectionUnselectedEvent(
            view,
            tab1
        )
    )
    selectionEvents.receive().isEqualTo(
        TabLayoutSelectionSelectedEvent(
            view,
            tab2
        )
    )
    // reselection
    uiRunBlocking { tab2.select() }
    selectionEvents.receiveOrNull().isEqualTo(
        TabLayoutSelectionReselectedEvent(
            view,
            tab2
        )
    )

    selectionEvents.cancel()
    uiRunBlocking { tab1.select() }
    selectionEvents.receiveOrNull().isNull()
  }

  @Test
  fun awaitSelectionEvent() = testRunBlocking {
    val job = uiLaunch {
      view.awaitSelectionEvent().isEqualTo(
          TabLayoutSelectionUnselectedEvent(
              view,
              tab1
          )
      )
    }
    uiLaunch { tab2.select() }
    job.joinAndIsCompleted()

    // reselection
    val job2 = uiLaunch {
      view.awaitSelectionEvent().isEqualTo(
          TabLayoutSelectionReselectedEvent(
              view,
              tab2
          )
      )
    }
    uiLaunch { tab2.select() }
    job2.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch {
      view.awaitSelectionEvent()
    }
    uiRunBlocking { tab1.select() }
    cancelJob.isCancelled.isTrue()
  }
}
