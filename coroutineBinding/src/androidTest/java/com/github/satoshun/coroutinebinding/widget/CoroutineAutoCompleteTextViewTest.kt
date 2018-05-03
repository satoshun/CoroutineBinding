package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.clearText
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.test.R
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.startsWith
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineAutoCompleteTextViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var autoCompleteTextView: AutoCompleteTextView

  @Before @UiThreadTest
  fun setUp() {
    autoCompleteTextView = AutoCompleteTextView(rule.activity)
    autoCompleteTextView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    autoCompleteTextView.id = R.id.auto_complete
    rule.activity.view.addView(autoCompleteTextView)
  }

  @Test
  fun itemClickEvents() = runBlocking<Unit> {
    val itemClickEvents = uiRunBlocking {
      autoCompleteTextView.threshold = 1
      val values = listOf("Two", "Three", "Twenty")
      autoCompleteTextView.setAdapter(
          ArrayAdapter(autoCompleteTextView.context, android.R.layout.simple_list_item_1, values)
      )
      autoCompleteTextView.itemClickEvents(1)
    }
    itemClickEvents.poll().isNull()
    onView(withId(R.id.auto_complete)).perform(typeText("Tw"))
    onData(startsWith("Twenty"))
        .inRoot(withDecorView(not(`is`(rule.activity.window.decorView))))
        .perform(click())
    val event = itemClickEvents.receive()
    event.position.isEqualTo(1)

    itemClickEvents.cancel()
    onView(withId(R.id.auto_complete))
        .perform(clearText(), typeText("Tw"))
    onData(startsWith("Twenty"))
        .inRoot(withDecorView(not(`is`(rule.activity.window.decorView))))
        .perform(click())
    itemClickEvents.receiveOrNull().isNull()
  }
}
