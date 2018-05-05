package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.widget.ListAdapter
import android.widget.ListView
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.createListView
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineAbsListViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var listView: ListView
  private lateinit var adapter: ListAdapter

  @Before @UiThreadTest
  fun setUp() {
    val p = rule.createListView()
    listView = p.first
    adapter = p.second
    rule.activity.view.addView(listView)
  }

  @Test
  fun scrollEvents() = runBlocking<Unit> {
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
}
