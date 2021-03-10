package com.yxcorp.gifshow.rules

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.rules.ExternalResource

class UIAutomatorRule : ExternalResource() {
  private lateinit var mDevice: UiDevice

  override fun before() {
    // Initialize UiDevice instance
    mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // Start from the home screen
    mDevice.pressHome()

    // Wait for launcher
    val launcherPackage = getLauncherPackageName()
    MatcherAssert.assertThat(launcherPackage, CoreMatchers.notNullValue())
    mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT.toLong())

    // Launch the blueprint app
    val context = ApplicationProvider.getApplicationContext<Context>()
    val intent = context.packageManager.getLaunchIntentForPackage(BASIC_PACKAGE)
        ?: throw RuntimeException("未安装该 App，applicationId = $BASIC_PACKAGE")
    // Clear out any previous instances
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)

    // Wait for the app to appear
    mDevice.wait(Until.hasObject(By.pkg(BASIC_PACKAGE).depth(0)), LAUNCH_TIMEOUT.toLong())
    Log.d(TAG, "before: ")
  }

  override fun after() {
    Log.d(TAG, "after: ")
  }

  private fun getLauncherPackageName(): String {
    // Create launcher Intent
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)

    // Use PackageManager to get the launcher package name
    val pm = ApplicationProvider.getApplicationContext<Context>().packageManager
    val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfo!!.activityInfo.packageName
  }

  companion object {
    //待测 app applicationId
    private const val BASIC_PACKAGE = "com.smile.gifmaker"
    private const val TAG = "UIAutomatorRule"
    private const val LAUNCH_TIMEOUT = 5000
  }
}