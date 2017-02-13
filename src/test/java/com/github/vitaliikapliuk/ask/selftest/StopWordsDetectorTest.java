package com.github.vitaliikapliuk.ask.selftest;

import com.github.vitaliikapliuk.ask.utils.StopWordsDetector;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class StopWordsDetectorTest {

    @Test
    public void testCorrectText() {
        final String textWithoutStopWords = "hello world, how are you?";
        boolean isContain = StopWordsDetector.containStopWords(textWithoutStopWords);
        Assert.assertFalse("not contain", isContain);
    }

    @Test
    public void testTextWithStopWords() {
        final String textWithoutStopWords = "hello world, STOPWORD how are you?";
        StopWordsDetector.addStopWord("STOPWORD");
        boolean isContain = StopWordsDetector.containStopWords(textWithoutStopWords);
        Assert.assertTrue("contain", isContain);
    }

    @Test
    public void testTextWithStopWordsCamelCase() {
        final String textWithoutStopWords = "hello world, CamelCaseStopWord how are you?";
        StopWordsDetector.addStopWord("CAMELCASESTOPWORD"); //upper case
        boolean isContain = StopWordsDetector.containStopWords(textWithoutStopWords);
        Assert.assertTrue("contain", isContain);
    }

    @Test
    public void testAddStopWord_1() {
        boolean isAdded = StopWordsDetector.addStopWord("NEWSTOPWORD");
        Assert.assertTrue("must be added", isAdded);
    }

    @Test
    public void testAddStopWord_2() {
        boolean isAdded = StopWordsDetector.addStopWord("stopword with whitespaces");
        Assert.assertFalse("cannot be added because is subsequent", isAdded);
    }

    @Test
    public void testAddStopWord_3() {
        boolean isAdded = StopWordsDetector.addStopWord("123");
        Assert.assertFalse("numbers cannot be a stop word", isAdded);
    }

    @Test
    public void testAddStopWord_4() {
        boolean isAdded = StopWordsDetector.addStopWord("123 456");
        Assert.assertFalse("cannot be added because contain whitespace(and numbers)", isAdded);
    }

    @Test
    public void testStopWordsInitFromFile() throws IOException {
        // the file already contain word 'cornhub'
        StopWordsDetector.init(); //lets init the file before use
        final String textWithoutStopWords = "Was cornhub renamed to normal site name on 2st of April?";
        boolean isContain = StopWordsDetector.containStopWords(textWithoutStopWords);
        Assert.assertTrue("contain", isContain);
    }
}
