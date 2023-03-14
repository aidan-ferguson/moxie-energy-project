package com.sh22.energy_saver_app;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.sh22.energy_saver_app.common.SH22Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestSH22Utils {
    @Test
    public void test_energ_rating_normalisation() {
        assertEquals(0.119f, SH22Utils.normaliseEnergyRating(0.0f), 0.001f);
        assertEquals(0.5f, SH22Utils.normaliseEnergyRating(1.0f), 0.001f);
        assertEquals(0.881f, SH22Utils.normaliseEnergyRating(2.0f), 0.001f);
        assertTrue(SH22Utils.normaliseEnergyRating(1000000.0f) <= 1.0f);
    }

    @Test
    public void test_get_full_input_stream() {
        String test_string = new String("test_value");
        InputStream is = new ByteArrayInputStream( test_string.getBytes() );
        try {
            assertEquals(test_string, SH22Utils.readFullStream(is));
        } catch (IOException e) {
            fail();
        }
    }
}
