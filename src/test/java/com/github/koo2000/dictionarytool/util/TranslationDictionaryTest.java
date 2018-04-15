package com.github.koo2000.dictionarytool.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TranslationDictionaryTest {

    private TranslationDictionary target;

    @BeforeEach
    void setUp() {
        Map<String, String> dictionary = new HashMap<String, String>();

        dictionary.put("ユーザ", "user");
        dictionary.put("ID", "id");
        dictionary.put("名", "name");

        target = new TranslationDictionary(dictionary);
    }

    @Test
    void translateToSnakeCase() throws WordNotFoundException {
        assertEquals("user_id", target.
                translateToSnakeCase("ユーザID"));
        assertEquals("user_name", target.translateToSnakeCase("ユーザ名"));
    }

    @Test
    void testTranslateToSnakeCaseNotFound() {
        WordNotFoundException e = assertThrows(WordNotFoundException.class,
                () -> target.translateToSnakeCase("ユーザ氏名"));

        assertTrue(e.getMessage().contains("[氏名]"));
    }
}