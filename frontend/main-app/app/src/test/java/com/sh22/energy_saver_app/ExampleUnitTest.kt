package com.sh22.energy_saver_app

import com.sh22.energy_saver_app.common.SH22Utils
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(0.5f, SH22Utils.normaliseEnergyRating(1.0f))
    }
}