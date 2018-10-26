package com.github.satoshun.coroutinebinding.androidx.drawerlayout.widget

import android.view.View
import androidx.annotation.CheckResult
import androidx.drawerlayout.widget.DrawerLayout
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Create an channel of the open state of the drawer of view.
 */
@CheckResult
fun DrawerLayout.drawerOpen(gravity: Int, capacity: Int = 0): ReceiveChannel<Boolean> = cancelableChannel(capacity) {
  val listener = object : DrawerLayout.DrawerListener {
    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerClosed(drawerView: View) {
      if ((drawerView.layoutParams as DrawerLayout.LayoutParams).gravity == gravity) {
        safeOffer(false)
      }
    }

    override fun onDrawerOpened(drawerView: View) {
      if ((drawerView.layoutParams as DrawerLayout.LayoutParams).gravity == gravity) {
        safeOffer(true)
      }
    }
  }
  invokeOnCloseOnMain {
    removeDrawerListener(listener)
  }
  addDrawerListener(listener)
}

/**
 * Suspend a of the open state of the drawer of view.
 */
suspend fun DrawerLayout.awaitDrawerOpen(gravity: Int): Boolean = suspendCancellableCoroutine { cont ->
  val listener = object : DrawerLayout.DrawerListener {
    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerClosed(drawerView: View) {
      if ((drawerView.layoutParams as DrawerLayout.LayoutParams).gravity == gravity) {
        cont.resume(false)
        removeDrawerListener(this)
      }
    }

    override fun onDrawerOpened(drawerView: View) {
      if ((drawerView.layoutParams as DrawerLayout.LayoutParams).gravity == gravity) {
        cont.resume(true)
        removeDrawerListener(this)
      }
    }
  }
  cont.invokeOnCancellation {
    removeDrawerListener(listener)
  }
  addDrawerListener(listener)
}
