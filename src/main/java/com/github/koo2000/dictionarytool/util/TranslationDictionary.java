package com.github.koo2000.dictionarytool.util;

import java.util.Map;

public class TranslationDictionary {

    private Map<String, String> dictionary;

    private static final String JOIN_CHAR = "_";
    public TranslationDictionary(Map<String, String> dictionary) {
        this.dictionary = dictionary;
    }



    public String translateToSnakeCase(String from) throws WordNotFoundException {
        int start = 0;

        StringBuffer sb = new StringBuffer();

        String temp = from;

        while (temp.length() > 0) {
            String longest = findLongest(temp);
            if (longest == null) {
                throw new WordNotFoundException("can't convert from "
                        + from + ". please add dictionary word [" + temp + "]");
            }
            String translated = dictionary.get(longest);
            if (sb.length() > 0) {
                sb.append(JOIN_CHAR);
            }
            sb.append(translated);
            temp = temp.substring(longest.length());
        }

        return sb.toString();
    }

    private String findLongest(String term){
        for (int len = term.length(); len > 0; len--) {

            String challenge = term.substring(0, len);
            if (dictionary.containsKey(challenge)) {
                return challenge;
            }
        }
        return null;
    }
}
