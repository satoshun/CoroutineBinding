package com.github.satoshun.coroutinebinding.androidx.recyclerview.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.androidx.recyclerview.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isInstanceOf
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class CoroutineRecyclerViewTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
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
  fun awaitChildAttachStateChangeEvent() = testRunBlocking {
    val job = uiLaunch {
      view.awaitChildAttachStateChangeEvent().isInstanceOf(RecyclerViewChildAttachEvent::class)
    }
    uiLaunch { view.adapter = SimpleAdapter(child) }
    job.joinAndIsCompleted()

//    val job2 = uiLaunch {
//      view.awaitChildAttachStateChangeEvent().isInstanceOf(RecyclerViewChildDetachEvent::class)
//    }
//    uiLaunch { view.adapter = null }
//    job2.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch {
      view.awaitChildAttachStateChangeEvent()
    }
    uiRunBlocking { view.adapter = SimpleAdapter(child) }
    cancelJob.isCancelled.isTrue()
  }

  @Ignore("flaky")
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

  @Ignore("flaky")
  @Test
  fun awaitScrollEvent() = testRunBlocking {
    uiRunBlocking {
      view.adapter = Adapter()
    }

    val job = uiLaunch { view.awaitScrollEvent().dy.isEqualTo(50) }
    uiLaunch { view.scrollBy(0, 50) }
    job.joinAndIsCompleted()

    val job2 = uiLaunch { view.awaitScrollEvent().dy.isEqualTo(-50) }
    uiLaunch { view.scrollBy(0, -50) }
    job2.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { view.awaitScrollEvent() }
    uiRunBlocking { view.scrollBy(0, 50) }
    cancelJob.isCancelled.isTrue()
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

  @Test
  fun awaitScrollStateChange() = testRunBlocking {
    uiRunBlocking { view.adapter = Adapter() }

    val job = uiLaunch {
      view.awaitScrollStateChange()
    }
    uiLaunch {
      view.smoothScrollBy(0, 100)
      view.stopScroll()
    }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch {
      view.awaitScrollStateChange()
    }
    uiRunBlocking { view.scrollBy(0, -50) }
    cancelJob.isCancelled.isTrue()
  }
}

private class SimpleAdapter constructor(
  private val child: View
) : RecyclerView.Adapter<ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return object : ViewHolder(child) {
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

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
