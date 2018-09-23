package com.github.satoshun.coroutinebinding.androidx.appcompat.widget

import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SearchView
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.androidx.appcompat.queryTextChange
import com.github.satoshun.coroutinebinding.androidx.appcompat.queryTextChangeEvents
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.androidx.appcompat.ViewActivity
import com.github.satoshun.coroutinebinding.androidx.appcompat.v7.appcompat.R
import com.github.satoshun.coroutinebinding.testRunBlocking
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
}
