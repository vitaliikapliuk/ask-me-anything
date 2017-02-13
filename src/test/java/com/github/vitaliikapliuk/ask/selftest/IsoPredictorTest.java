package com.github.vitaliikapliuk.ask.selftest;

import com.github.vitaliikapliuk.ask.utils.IsoPredictor;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class IsoPredictorTest {

    @Test
    public void testPredict() throws IOException {
        String iso = IsoPredictor.getIso("somewronghost123.com");
        Assert.assertEquals("must be equals", "LV", iso);

    }
}
