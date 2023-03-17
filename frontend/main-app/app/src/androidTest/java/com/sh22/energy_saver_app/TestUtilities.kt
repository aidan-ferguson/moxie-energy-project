package com.sh22.energy_saver_app

import android.content.Context
import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.util.HumanReadables
import com.sh22.energy_saver_app.common.Constants
import org.hamcrest.CoreMatchers.any
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException

/**
 * Contains all utility functions used during instrumentation tests
 */
class TestUtilities {
    companion object {
        /**
         * Deletes the authentication token in sharedPreferences
         */
        fun clear_token(context: Context) {
            val preferences =
                context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.remove(Constants.PREFERENCE_TOKEN_KEY)
            editor.apply()
        }
    }
}