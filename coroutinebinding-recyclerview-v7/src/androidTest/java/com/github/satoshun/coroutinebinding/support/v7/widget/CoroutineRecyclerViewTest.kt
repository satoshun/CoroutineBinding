package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isInstanceOf
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.support.v7.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineRecyclerViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var view: RecyclerView
  private lateinit var child: View

  @Before @UiThreadTest
  fun setUp() {
    view = RecyclerView(rule.activity)
    view.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    view.layoutManager = LinearLayoutManager(rule.activity)
    view.id = android.R.id.primary
    child = View(rule.activity)
    rule.activity.view.addView(view)
  }

  @Test
  fun childAttachStateChangeEvents() = testRunBlocking {
    val childAttachStateChangeEvents = uiRunBlocking { view.childAttachStateChangeEvents(1) }

    uiLaunch { view.adapter = SimpleAdapter(child) }
    childAttachStateChangeEvents.receiveOrNull().isInstanceOf(RecyclerViewChildAttachEvent::class)

    uiLaunch { view.adapter = null }
    childAttachStateChangeEvents.receiveOrNull().isInstanceOf(RecyclerViewChildDetachEvent::class)

    childAttachStateChangeEvents.cancel()
    uiLaunch { view.adapter = SimpleAdapter(child) }
    childAttachStateChangeEvents.receiveOrNull().isNull()
  }

  @Test
  fun scrollEvents() = testRunBlocking {
    uiRunBlocking {
      view.adapter = Adapter()
    }
    val scrollEvents = uiRunBlocking { view.scrollEvents(1) }

    uiLaunch { view.scrollBy(0, 50) }
    scrollEvents.receive().dy.isEqualTo(50)

    uiLaunch { view.scrollBy(0, -50) }
    scrollEvents.receive().dy.isEqualTo(-50)

    scrollEvents.cancel()
    uiRunBlocking { view.scrollBy(0, 50) }
    scrollEvents.receiveOrNull().isNull()
  }

  @Test
  fun scrollStateChanges() = testRunBlocking {
    uiRunBlocking { view.adapter = Adapter() }
    val scrollStateChanges = uiRunBlocking { view.scrollStateChanges(1) }

    uiLaunch {
      view.smoothScrollBy(0, 100)
      view.stopScroll()
    }
    scrollStateChanges.receive().isNotNull()

    scrollStateChanges.cancel()
    uiRunBlocking { view.scrollBy(0, -50) }
    scrollStateChanges.receiveOrNull().isNull()
  }
}

private class SimpleAdapter constructor(
  private val child: View
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return object : RecyclerView.ViewHolder(child) {
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

  override fun getItemCount(): Int {
    return 1
  }
}

private class Adapter : RecyclerView.Adapter<ViewHolder>() {
  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
    val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
    return object : ViewHolder(v) {}
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder.itemView as TextView).text = position.toString()
  }

  override fun getItemCount(): Int {
    return 100
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }
}
