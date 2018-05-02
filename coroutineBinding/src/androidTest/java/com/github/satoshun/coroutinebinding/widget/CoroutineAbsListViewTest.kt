package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import com.github.satoshun.coroutinebinding.ViewActivity
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
    adapter = MyListAdapter()
    listView = ListView(rule.activity)
    listView.adapter = adapter
    rule.activity.rootView.addView(listView)
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

private class MyListAdapter : BaseAdapter() {
  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    return TextView(parent.context).apply {
      text = position.toString()
    }
  }

  override fun getItem(position: Int): Any {
    return position
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getCount(): Int = 100
}
