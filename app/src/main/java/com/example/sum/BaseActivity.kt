package com.example.sum

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.sum.utility.PreferenceUtils

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            PreferenceUtils.updateBaseContextLocale(newBase)
        )
    }
}