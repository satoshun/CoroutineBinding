package com.github.satoshun.coroutinebinding.widget

import android.widget.ListAdapter
import android.widget.ListView
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.createListView
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isFalse
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class CoroutineAbsListViewTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var listView: ListView
  private lateinit var adapter: ListAdapter

  @Before @UiThreadTest
  fun setUp() {
    val p = rule.createListView()
    listView = p.first
    adapter = p.second
    rule.activity.view.addView(listView)
  }

  @Ignore("todo")
  @Test
  fun scrollEvents() = testRunBlocking {
    val scrollEvents = uiRunBlocking {
      listView.scrollEvents(1)
    }
    val event = scrollEvents.receive()
    event.totalItemCount.isEqualTo(100)
    event.firstVisibleItem.isEqualTo(0)

    uiRunBlocking {
      listView.smoothScrollByOffset(10)
    }
    val next = scrollEvents.receive()
    next.totalItemCount.isEqualTo(100)

    scrollEvents.cancel()
    uiRunBlocking {
      listView.smoothScrollByOffset(10)
    }
    scrollEvents.poll().isNull()
  }

  @Ignore("todo")
  @Test
  fun awaitScrollEvent() = testRunBlocking {
    val job = uiLaunch {
      val event = listView.awaitScrollEvent()
      event.totalItemCount.isEqualTo(100)
    }
    job.isCompleted.isFalse()
    uiRunBlocking { listView.smoothScrollByOffset(10) }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { listView.awaitScrollEvent() }
    cancelJob.cancel()
    uiRunBlocking { listView.smoothScrollByOffset(10) }
    cancelJob.isCancelled.isTrue()
  }
}
