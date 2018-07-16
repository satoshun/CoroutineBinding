@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.common.truth.Truth
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeout
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.reflect.KClass

inline fun Any?.isNull() = Truth.assertThat(this).isNull()
inline fun Any?.isNotNull() = Truth.assertThat(this).isNotNull()
inline fun Any?.isEqualTo(other: Any?) = Truth.assertThat(this).isEqualTo(other)
inline fun Any?.isInstanceOf(other: KClass<*>) = Truth.assertThat(this).isInstanceOf(other.java)
inline fun Boolean?.isTrue() = Truth.assertThat(this).isTrue()
inline fun Boolean?.isFalse() = Truth.assertThat(this).isFalse()
inline fun Float?.isGreaterThan(other: Float) = Truth.assertThat(this).isGreaterThan(other)
inline fun Float?.isLessThan(other: Float) = Truth.assertThat(this).isLessThan(other)
inline fun Any?.isSame(other: Any?) = Truth.assertThat(this).isSameAs(other)

inline fun <T> T.verify() = com.nhaarman.mockito_kotlin.verify(this)

inline fun testRunBlocking(
    context: CoroutineContext = EmptyCoroutineContext,
    noinline block: suspend CoroutineScope.() -> Unit
) {
  return runBlocking(context = context, block = {
    withTimeout(5, TimeUnit.SECONDS) { block() }
  })
}

inline fun <T> uiRunBlocking(noinline block: suspend CoroutineScope.() -> T): T {
  return runBlocking(context = UI, block = block)
}

inline fun uiLaunch(noinline block: suspend CoroutineScope.() -> Unit): Job {
  return launch(context = UI, block = block)
}

inline fun ActivityTestRule<out Activity>.createListView(): Pair<ListView, ListAdapter> {
  val listView = ListView(activity)
  val adapter = MyListAdapter()
  listView.adapter = adapter
  return listView to adapter
}

class MyListAdapter : BaseAdapter() {
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
