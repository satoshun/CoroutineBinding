package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.widget.SearchView
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isFalse
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineSearchViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var searchView: SearchView

  @Before @UiThreadTest
  fun setUp() {
    searchView = SearchView(rule.activity)
    rule.activity.view.addView(searchView)
  }

  @Test
  fun queryTextChangeEvents() = runBlocking<Unit> {
    val queryTextChangeEvents = uiRunBlocking { searchView.queryTextChangeEvents(2) }

    uiLaunch { searchView.setQuery("init", false) }
    queryTextChangeEvents.receive().queryText.isEqualTo("init")

    uiLaunch { searchView.setQuery("HHH", false) }
    queryTextChangeEvents.receive().queryText.isEqualTo("HHH")

    uiLaunch { searchView.setQuery(null, false) }
    queryTextChangeEvents.receive().let {
      it.queryText.isEqualTo("")
      it.isSubmitted.isFalse()
    }

    // submit
    uiLaunch { searchView.setQuery("HHH", true) }
    queryTextChangeEvents.receive().let {
      it.queryText.isEqualTo("HHH")
      it.isSubmitted.isFalse()
    }
    // submission
    queryTextChangeEvents.receive().let {
      it.queryText.isEqualTo("HHH")
      it.isSubmitted.isTrue()
    }

    queryTextChangeEvents.cancel()
    uiLaunch { searchView.setQuery("HHH", false) }
    queryTextChangeEvents.receiveOrNull().isNull()
  }

  @Test
  fun queryTextChanges() = runBlocking<Unit> {
    val queryTextChanges = uiRunBlocking { searchView.queryTextChanges() }

    uiLaunch { searchView.setQuery("init", false) }
    queryTextChanges.receive().isEqualTo("init")

    uiLaunch { searchView.setQuery("HHH", false) }
    queryTextChanges.receive().isEqualTo("HHH")

    uiLaunch { searchView.setQuery(null, false) }
    queryTextChanges.receive().isEqualTo("")

    queryTextChanges.cancel()
    uiLaunch { searchView.setQuery("HHH", false) }
    queryTextChanges.receiveOrNull().isNull()
  }
}
