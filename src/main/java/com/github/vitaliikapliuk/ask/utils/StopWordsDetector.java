package com.github.vitaliikapliuk.ask.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopWordsDetector {

    private static final String STOP_WORDS_FILENAME = "stopwords.txt";
    private static final Pattern LETTERS_PATTERN = Pattern.compile("\\p{L}+"); //find any letters in any languages patters

    private static final Set<String> stopWords = new HashSet<>();

    public static boolean containStopWords(CharSequence cs) {
        Matcher matcher = LETTERS_PATTERN.matcher(cs);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            if (stopWords.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean addStopWord(String stopWord) {
        return StringUtils.isNotBlank(stopWord) && isWord(stopWord) && stopWords.add(stopWord.toLowerCase());
    }

    private static boolean isWord(String text) {
        return !StringUtils.isBlank(text) && !text.matches(".*[^\\p{L}*].*"); //try to find non words letters
    }

    public static void init() throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(STOP_WORDS_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            addStopWord(line.trim());
        }
    }
}
