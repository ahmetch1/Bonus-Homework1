import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

class Naive extends Solution {
    static {
        SUBCLASSES.add(Naive.class);
        System.out.println("Naive registered");
    }

    public Naive() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
            }
            if (j == m) {
                indices.add(i);
            }
        }

        return indicesToString(indices);
    }
}

class KMP extends Solution {
    static {
        SUBCLASSES.add(KMP.class);
        System.out.println("KMP registered");
    }

    public KMP() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Compute LPS (Longest Proper Prefix which is also Suffix) array
        int[] lps = computeLPS(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            if (j == m) {
                indices.add(i - j);
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return indicesToString(indices);
    }

    private int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}

class RabinKarp extends Solution {
    static {
        SUBCLASSES.add(RabinKarp.class);
        System.out.println("RabinKarp registered.");
    }

    public RabinKarp() {
    }

    private static final int PRIME = 101; // A prime number for hashing

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (m > n) {
            return "";
        }

        int d = 256; // Number of characters in the input alphabet
        long patternHash = 0;
        long textHash = 0;
        long h = 1;

        // Calculate h = d^(m-1) % PRIME
        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % PRIME;
        }

        // Calculate hash value for pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (d * patternHash + pattern.charAt(i)) % PRIME;
            textHash = (d * textHash + text.charAt(i)) % PRIME;
        }

        // Slide the pattern over text one by one
        for (int i = 0; i <= n - m; i++) {
            // Check if hash values match
            if (patternHash == textHash) {
                // Check characters one by one
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    indices.add(i);
                }
            }

            // Calculate hash value for next window
            if (i < n - m) {
                textHash = (d * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;

                // Convert negative hash to positive
                if (textHash < 0) {
                    textHash = textHash + PRIME;
                }
            }
        }

        return indicesToString(indices);
    }
}

/**
 * Boyer-Moore Implementation (Optimized with HashMap)
 */
class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Edge Case: Empty Pattern matches everywhere
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                matches.add(i);
            }
            return indicesToString(matches);
        }

        // Pre-Processing: Bad Character Heuristic with HashMap
        Map<Character, Integer> badChar = new HashMap<>();

        for (int i = 0; i < m; i++) {
            badChar.put(pattern.charAt(i), i);
        }

        // Searching Phase
        int s = 0; 
        while (s <= (n - m)) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                matches.add(s);
                if (s + m < n) {
                    char nextChar = text.charAt(s + m);
                    int shift = badChar.getOrDefault(nextChar, -1);
                    s += m - shift;
                } else {
                    s += 1;
                }
            } else {
                char badCharInText = text.charAt(s + j);
                int shift = badChar.getOrDefault(badCharInText, -1);
                s += Math.max(1, j - shift);
            }
        }

        return indicesToString(matches);
    }
}

/**
 * GoCrazy Implementation (Sunday's Algorithm - Optimized with HashMap)
 */
class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered (Implementing Sunday's Algorithm)");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Edge Case: Empty Pattern matches everywhere
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                matches.add(i);
            }
            return indicesToString(matches);
        }
        
        if (m > n) {
            return indicesToString(matches);
        }

        // Pre-Processing: Sunday's Algorithm Shift Table with HashMap
        Map<Character, Integer> shiftTable = new HashMap<>();

        for (int i = 0; i < m; i++) {
            shiftTable.put(pattern.charAt(i), m - i);
        }

        // Searching Phase
        int s = 0; 

        while (s <= n - m) {
            int j = 0;
            while (j < m && text.charAt(s + j) == pattern.charAt(j)) {
                j++;
            }

            if (j == m) {
                matches.add(s);
            }

            // Shift Strategy (Sunday's Algorithm)
            if (s + m < n) {
                char nextChar = text.charAt(s + m);
                s += shiftTable.getOrDefault(nextChar, m + 1);
            } else {
                break;
            }
        }

        return indicesToString(matches);
    }
}