package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.view.inputmethod.EditorInfo.IME_ACTION_GO
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.widget.TextView
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineTextViewTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
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
  fun awaitEditorAction() = testRunBlocking {
    val job = uiLaunch { textView.awaitEditorAction().isEqualTo(IME_ACTION_GO) }
    uiLaunch { textView.onEditorAction(IME_ACTION_GO) }
    job.joinAndIsCompleted()

    val jobCancel = toBeCancelLaunch { textView.awaitEditorAction() }
    uiRunBlocking { textView.onEditorAction(IME_ACTION_GO) }
    jobCancel.isCancelled.isTrue()
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
  fun awaitEditorActionEvent() = testRunBlocking {
    val job = uiLaunch {
      textView.awaitEditorActionEvent().actionId.isEqualTo(IME_ACTION_GO)
    }
    uiLaunch { textView.onEditorAction(IME_ACTION_GO) }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { textView.awaitEditorActionEvent() }
    uiRunBlocking { textView.onEditorAction(IME_ACTION_GO) }
    cancelJob.isCancelled.isTrue()
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
  fun awaitTextChange() = testRunBlocking {
    val job = uiLaunch {
      textView.awaitTextChange().toString().isEqualTo("HHHHH")
    }
    uiLaunch { textView.text = "HHHHH" }
    job.joinAndIsCompleted()

    val jobCancel = toBeCancelLaunch {
      textView.awaitTextChange()
    }
    uiLaunch { textView.text = "HHHHH" }
    jobCancel.isCancelled.isTrue()
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
  fun awaitTextChangeEvent() = testRunBlocking {
    val job = uiLaunch {
      textView.awaitTextChangeEvent().text.toString().isEqualTo("HHHHH")
    }
    uiLaunch { textView.text = "HHHHH" }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { textView.awaitTextChangeEvent() }
    uiLaunch { textView.text = "HHHHH" }
    cancelJob.isCancelled.isTrue()
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
  fun awaitBeforeTextChangeEvent() = testRunBlocking {
    val job = uiLaunch {
      textView.text = "first"
      textView.awaitBeforeTextChangeEvent().text.toString().isEqualTo("first")
    }
    uiLaunch { textView.text = "DDDDD" }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch {
      textView.awaitBeforeTextChangeEvent()
    }
    uiLaunch { textView.text = "HHHHH" }
    cancelJob.isCancelled.isTrue()
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

  @Test
  fun awaitAfterTextChangeEvent() = testRunBlocking {
    val job = uiLaunch {
      textView.text = "first"
      textView.awaitAfterTextChangeEvent().editable.toString().isEqualTo("HHHHH")
    }
    uiLaunch { textView.text = "HHHHH" }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { textView.awaitAfterTextChangeEvent() }
    uiLaunch { textView.text = "HHHHH" }
    cancelJob.isCancelled.isTrue()
  }
}
