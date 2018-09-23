package com.github.satoshun.coroutinebinding.material.snackbar

import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.material.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import com.google.android.material.snackbar.Snackbar
import org.junit.Before
import org.junit.Test

class CoroutineSnackbarTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
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
