package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.SearchView
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.support.v7.ViewActivity
import com.github.satoshun.coroutinebinding.support.v7.appcompat.R
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineSearchViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

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
