package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.github.satoshun.coroutinebinding.isInstanceOf
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.support.v7.ViewActivity
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
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
    view.layoutManager = LinearLayoutManager(rule.activity)
    view.id = android.R.id.primary
    child = View(rule.activity)
    rule.activity.view.addView(view)
  }

  @Test
  fun childAttachStateChangeEvents() = runBlocking<Unit> {
    val childAttachStateChangeEvents = uiRunBlocking { view.childAttachStateChangeEvents(1) }

    uiLaunch { view.adapter = SimpleAdapter(child) }
    childAttachStateChangeEvents.receiveOrNull().isInstanceOf(RecyclerViewChildAttachEvent::class)

    uiLaunch { view.adapter = null }
    childAttachStateChangeEvents.receiveOrNull().isInstanceOf(RecyclerViewChildDetachEvent::class)

    childAttachStateChangeEvents.cancel()
    uiLaunch { view.adapter = SimpleAdapter(child) }
    childAttachStateChangeEvents.receiveOrNull().isNull()
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
