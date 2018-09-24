package com.github.satoshun.coroutinebinding.androidx.appcompat.widget

import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SearchView
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.androidx.appcompat.R
import com.github.satoshun.coroutinebinding.androidx.appcompat.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isFalse
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineSearchViewTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var searchView: SearchView

  @Before @UiThreadTest
  fun setUp() {
    val context = ContextThemeWrapper(rule.activity, R.style.Theme_AppCompat)
    searchView = SearchView(context)
    rule.activity.view.addView(searchView)
  }

  @Test
  fun queryTextChangeEvents() = testRunBlocking {
    uiRunBlocking { searchView.setQuery("init", false) }
    val queryTextChangeEvents = uiRunBlocking { searchView.queryTextChangeEvents(2) }

    uiLaunch { searchView.setQuery("Hi", false) }
    queryTextChangeEvents.receive().queryText.isEqualTo("Hi")
    uiLaunch { searchView.setQuery("Hoi", false) }
    queryTextChangeEvents.receive().queryText.isEqualTo("Hoi")
    uiLaunch { searchView.setQuery(null, false) }
    queryTextChangeEvents.receive().queryText.isEqualTo("")

    uiLaunch { searchView.setQuery("Hello", true) }
    // change event
    queryTextChangeEvents.receive().isSubmitted.isEqualTo(false)
    // submitted event
    queryTextChangeEvents.receive().isSubmitted.isEqualTo(true)

    queryTextChangeEvents.cancel()
    uiLaunch { searchView.setQuery("Hoi", false) }
    queryTextChangeEvents.receiveOrNull().isNull()
  }

  @Test
  fun awaitQueryTextSubmitEvent() = testRunBlocking {
    uiRunBlocking { searchView.setQuery("init", false) }

    val job = uiLaunch {
      val event = searchView.awaitQueryTextSubmitEvent()
      event.isSubmitted.isTrue()
      event.queryText.isEqualTo("Hello")
    }
    uiRunBlocking { searchView.setQuery("hi", false) }
    uiRunBlocking { searchView.setQuery("Hello", true) }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch {
      searchView.awaitQueryTextChangeEvent()
    }
    uiRunBlocking { searchView.setQuery("Hoi", true) }
    cancelJob.isCancelled.isTrue()
  }

  @Test
  fun awaitQueryTextChangeEvent() = testRunBlocking {
    uiRunBlocking { searchView.setQuery("init", false) }

    val job = uiLaunch {
      val event = searchView.awaitQueryTextChangeEvent()
      event.isSubmitted.isFalse()
      event.queryText.isEqualTo("hi")
    }
    uiLaunch { searchView.setQuery("hi", false) }
    job.joinAndIsCompleted()

    val job2 = uiLaunch {
      val event = searchView.awaitQueryTextChangeEvent()
      event.isSubmitted.isFalse()
      event.queryText.isEqualTo("Hello")
    }
    uiLaunch { searchView.setQuery("Hello", true) }
    job2.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch {
      searchView.awaitQueryTextChangeEvent()
    }
    uiRunBlocking { searchView.setQuery("Hoi", false) }
    cancelJob.isCancelled.isTrue()
  }

  @Test
  fun queryTextChange() = testRunBlocking {
    uiRunBlocking { searchView.setQuery("init", false) }
    val queryTextChange = uiRunBlocking { searchView.queryTextChange(2) }

    uiLaunch { searchView.setQuery("Hi", false) }
    queryTextChange.receive().isEqualTo("Hi")
    uiLaunch { searchView.setQuery("Hoi", false) }
    queryTextChange.receive().isEqualTo("Hoi")
    uiLaunch { searchView.setQuery(null, false) }
    queryTextChange.receive().isEqualTo("")

    uiLaunch { searchView.setQuery("Hello", true) }
    // change event
    queryTextChange.receive().isEqualTo("Hello")

    queryTextChange.cancel()
    uiLaunch { searchView.setQuery("Hoi", false) }
    queryTextChange.receiveOrNull().isNull()
  }

  @Test
  fun awaitQueryTextChange() = testRunBlocking {
    uiRunBlocking { searchView.setQuery("init", false) }

    val job = uiLaunch { searchView.awaitQueryTextChange().isEqualTo("Hi") }
    uiLaunch { searchView.setQuery("Hi", false) }
    job.joinAndIsCompleted()

    val job2 = uiLaunch { searchView.awaitQueryTextChange().isEqualTo("Hello") }
    uiLaunch { searchView.setQuery("Hello", true) }
    job2.joinAndIsCompleted()

    val jobCancel = toBeCancelLaunch {
      searchView.awaitQueryTextChange()
    }
    uiRunBlocking { searchView.setQuery("Hoi", false) }
    jobCancel.isCancelled.isTrue()
  }
}
