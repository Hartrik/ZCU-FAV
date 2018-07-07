package cz.harag.bit.c03;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Ciphers {

    public static String caesar(int n, String text) {
        return text.chars().map(c -> {
                    c += n;
                    return (c > 'z') ? c - 26 : c;
                })
                .mapToObj(i -> "" + (char) i).collect(Collectors.joining());
    }

    public static String caesar(String text) {
        return caesar(3, text);
    }

    public static String atbas(String text) {
        return text.chars()
                .map(c -> 'z' - (c - 'a'))
                .mapToObj(i -> "" + (char) i).collect(Collectors.joining());
    }

    public static String mono_sub(String alphabet, String text) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int pos = c - 'a';
            out.append(alphabet.charAt(pos));
        }
        return out.toString();
    }

    public static String alberti(String a1, String a2, String text) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String alphabet = (i % 2 == 0) ? a1 : a2;
            int pos = c - 'a';
            out.append(alphabet.charAt(pos));
        }
        return out.toString();
    }

    public static String vigener(String key, String text) {
        while (key.length() < text.length()) {
            key = key + key;
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            char k = key.charAt(i);
            int r = c + k - 'a';
            r = (r > 'z') ? r - 26 : r;
            out.append((char) r);
        }
        return out.toString();
    }

    public static String col_trans(String key, String text) {
        // vytvoření pole s pozicemi
        int[] positions = new int[key.length()];
        int currentPos = 0;
        for (char i = 'a'; i <= 'z'; i++) {
            for (int j = 0; j < key.length(); j++) {
                char c = key.charAt(j);
                if (c == i) {
                    positions[j] = currentPos++;
                }
            }
        }

        // prodložení textu
        int oldLen = text.length();
        while (text.length() % key.length() != 0) {
            text += "_";
        }

        // šifrování
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i += key.length()) {
            char[] arr = new char[key.length()];
            for (int j = 0; j < key.length(); j++) {
                int pos = i + j;
                char c = text.charAt(pos);
                arr[positions[j]] = c;
            }
            out.append(arr);
        }
        return out.toString().substring(0, oldLen);
    }

}
