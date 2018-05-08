package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.view.inputmethod.EditorInfo.IME_ACTION_GO
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.widget.TextView
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineTextViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var textView: TextView

  @Before @UiThreadTest
  fun setUp() {
    textView = TextView(rule.activity)
    rule.activity.view.addView(textView)
  }

  @Test
  fun editorActions() = testRunBlocking {
    val editorActions = uiRunBlocking { textView.editorActions(1) }

    uiLaunch { textView.onEditorAction(IME_ACTION_GO) }
    editorActions.receive().isEqualTo(IME_ACTION_GO)

    uiLaunch { textView.onEditorAction(IME_ACTION_NEXT) }
    editorActions.receive().isEqualTo(IME_ACTION_NEXT)

    editorActions.cancel()
    uiLaunch { textView.onEditorAction(IME_ACTION_GO) }
    editorActions.receiveOrNull().isNull()
  }

  @Test
  fun editorActionEvents() = testRunBlocking {
    val editorActionEvents = uiRunBlocking { textView.editorActionEvents(1) }

    uiLaunch { textView.onEditorAction(IME_ACTION_GO) }
    editorActionEvents.receive().actionId.isEqualTo(IME_ACTION_GO)

    uiLaunch { textView.onEditorAction(IME_ACTION_NEXT) }
    editorActionEvents.receive().actionId.isEqualTo(IME_ACTION_NEXT)

    editorActionEvents.cancel()
    uiLaunch { textView.onEditorAction(IME_ACTION_GO) }
    editorActionEvents.receiveOrNull().isNull()
  }

  @Test
  fun textChanges() = testRunBlocking {
    val textChanges = uiRunBlocking { textView.textChanges(1) }

    uiLaunch { textView.text = "HHHHH" }
    textChanges.receive().toString().isEqualTo("HHHHH")

    uiLaunch { textView.text = "BBB" }
    textChanges.receive().toString().isEqualTo("BBB")

    textChanges.cancel()
    uiLaunch { textView.text = "HHHHH" }
    textChanges.receiveOrNull().isNull()
  }

  @Test
  fun textChangeEvents() = testRunBlocking {
    val textChangeEvents = uiRunBlocking { textView.textChangeEvents(1) }

    uiLaunch { textView.text = "HHHHH" }
    textChangeEvents.receive().text.toString().isEqualTo("HHHHH")

    uiLaunch { textView.text = "BBB" }
    textChangeEvents.receive().text.toString().isEqualTo("BBB")

    textChangeEvents.cancel()
    uiLaunch { textView.text = "HHHHH" }
    textChangeEvents.receiveOrNull().isNull()
  }

  @Test
  fun beforeTextChangeEvents() = testRunBlocking {
    val before = uiRunBlocking {
      textView.text = "first"
      textView.beforeTextChangeEvents()
    }

    uiLaunch { textView.text = "HHHHH" }
    before.receive().text.toString().isEqualTo("first")

    uiLaunch { textView.text = "BBB" }
    before.receive().text.toString().isEqualTo("HHHHH")

    before.cancel()
    uiLaunch { textView.text = "HHHHH" }
    before.receiveOrNull().isNull()
  }

  @Test
  fun afterTextChangeEvents() = testRunBlocking {
    val after = uiRunBlocking {
      textView.text = "first"
      textView.afterTextChangeEvents(1)
    }

    uiLaunch { textView.text = "HHHHH" }
    after.receive().editable.toString().isEqualTo("HHHHH")

    uiLaunch { textView.text = "BBB" }
    after.receive().editable.toString().isEqualTo("BBB")

    after.cancel()
    uiLaunch { textView.text = "HHHHH" }
    after.receiveOrNull().isNull()
  }
}
