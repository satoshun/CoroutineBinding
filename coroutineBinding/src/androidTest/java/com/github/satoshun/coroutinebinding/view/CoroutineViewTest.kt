package com.github.satoshun.coroutinebinding.view

import android.support.test.rule.ActivityTestRule
import android.view.View
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.verify
import com.google.common.truth.Truth
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class CoroutineViewTest {

  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  @Test
  fun attaches__do_event() {
    launch(UI) {
      val attaches = rule.activity.view.attaches()

      Truth.assertThat(attaches.isEmpty).isTrue()
      rule.activity.rootView.removeView(rule.activity.view)
      Truth.assertThat(attaches.isEmpty).isTrue()
      rule.activity.rootView.addView(rule.activity.view)
      Truth.assertThat(attaches.receive()).isNotNull()
    }
  }

  @Ignore("AndroidTest doesn't work mockito + final")
  @Test
  fun attaches__do_removeOnAttachStateChangeListener_when_cancel() {
    val view: View = mock {}
    val attaches = view.attaches()
    view.verify().addOnAttachStateChangeListener(any())
    attaches.cancel()
    view.verify().removeOnAttachStateChangeListener(any())
  }
}
