package com.intuit.catlist.activity

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.min

@RunWith(AndroidJUnit4::class)
@LargeTest
class CatActivityTest {
    companion object {
        private const val APP_PACKAGE = "com.intuit.catlist"
        private const val LAUNCH_TIMEOUT = 5000L
    }

    private lateinit var device: UiDevice

    @Before
    fun launchApp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        // Waiting for launcher
        val launcherPackage: String = device.launcherPackageName
        Assert.assertThat(launcherPackage, CoreMatchers.notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT)

        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(APP_PACKAGE)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        device.wait(Until.hasObject(By.pkg(APP_PACKAGE).depth(0)), LAUNCH_TIMEOUT)
    }

    fun getFullResourceId(id: String) : String {
        return "$APP_PACKAGE:id/$id"
    }

    @Test
    fun catActivity_listLoaded() {
        device.wait(Until.hasObject(By.res(APP_PACKAGE, "catName")), LAUNCH_TIMEOUT)
    }

    @Test
    fun catActivity_toDetailActivity() {
        val list = UiScrollable(UiSelector().resourceId(getFullResourceId("recyclerView")))
        val index = (min(list.childCount, 5) * Math.random()).toInt()
        val item = device.findObject(UiSelector().resourceId(getFullResourceId("itemId")).instance(index))

        item.clickAndWaitForNewWindow()
        val element = device.findObject(UiSelector().resourceId(getFullResourceId("descriptionText")))
        Assert.assertThat(element, CoreMatchers.notNullValue())
    }
}