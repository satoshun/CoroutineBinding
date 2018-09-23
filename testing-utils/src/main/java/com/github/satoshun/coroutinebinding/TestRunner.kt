package com.github.satoshun.coroutinebinding

import android.app.KeyguardManager
import android.content.Context.KEYGUARD_SERVICE
import android.content.Context.POWER_SERVICE
import android.os.PowerManager
import android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP
import android.os.PowerManager.FULL_WAKE_LOCK
import android.os.PowerManager.ON_AFTER_RELEASE
import androidx.test.runner.AndroidJUnitRunner

class MyTestRunner : AndroidJUnitRunner() {
  private lateinit var wakeLock: PowerManager.WakeLock

  override fun onStart() {
    val app = targetContext.applicationContext

    val name = "MyTestRunner"
    // Unlock the device so that the tests can input keystrokes.
    val keyguard = app.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
    keyguard.newKeyguardLock(name).disableKeyguard()
    // Wake up the screen.
    val power = app.getSystemService(POWER_SERVICE) as PowerManager
    wakeLock = power.newWakeLock(FULL_WAKE_LOCK or ACQUIRE_CAUSES_WAKEUP or ON_AFTER_RELEASE, name)
    wakeLock.acquire()

    super.onStart()
  }

  override fun onDestroy() {
    super.onDestroy()
    wakeLock.release()
  }
}
