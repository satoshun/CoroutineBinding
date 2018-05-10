package com.github.satoshun.coroutinebinding.design.widget

import android.support.design.widget.Snackbar
import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import com.github.satoshun.coroutinebinding.design.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineSnackbarTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var view: Snackbar

  @Before @UiThreadTest
  fun setUp() {
    view = Snackbar.make(rule.activity.view, "Hey", Snackbar.LENGTH_SHORT)
  }

  @Test
  fun dismisses() = testRunBlocking {
    val dismisses = uiRunBlocking { view.dismisses(1) }

    uiRunBlocking { view.show() }
    uiRunBlocking { view.dismiss() }
    dismisses.receive().isEqualTo(Snackbar.Callback.DISMISS_EVENT_MANUAL)

    dismisses.cancel()
    uiRunBlocking { view.show() }
    uiRunBlocking { view.dismiss() }
    dismisses.receiveOrNull().isNull()
  }
}
