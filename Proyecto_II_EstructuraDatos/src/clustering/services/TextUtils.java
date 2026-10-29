package clustering.services;

import clustering.model.structures.MyList;

/** Utilidades de texto propias: validación numérica, separación por espacios y ordenamiento. */
public final class TextUtils {
    private TextUtils() { }

    /** true si el texto es un número decimal válido (signo, dígitos, punto y exponente opcionales). */
    public static boolean isNumber(String text) {
        String s = text.trim();
        int i = 0, n = s.length();
        if (n == 0) return false;
        if (s.charAt(i) == '+' || s.charAt(i) == '-') i++;
        int digits = 0;
        while (i < n && Character.isDigit(s.charAt(i))) { i++; digits++; }
        if (i < n && s.charAt(i) == '.') {
            i++;
            while (i < n && Character.isDigit(s.charAt(i))) { i++; digits++; }
        }
        if (digits == 0) return false;
        if (i < n && (s.charAt(i) == 'e' || s.charAt(i) == 'E')) {
            i++;
            if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) i++;
            int expDigits = 0;
            while (i < n && Character.isDigit(s.charAt(i))) { i++; expDigits++; }
            if (expDigits == 0) return false;
        }
        return i == n;
    }

    public static double parseNumber(String text) { return Double.parseDouble(text.trim()); }

    /** Separa por espacios en blanco, descartando tokens vacíos. O(longitud). */
    public static MyList<String> splitWhitespace(String text) {
        MyList<String> tokens = new MyList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                if (current.length() > 0) { tokens.add(current.toString()); current.setLength(0); }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) tokens.add(current.toString());
        return tokens;
    }

    /** Ordenamiento por mezcla (merge sort) de una lista de textos. O(k log k). */
    public static void sortStrings(MyList<String> list) {
        int n = list.size();
        String[] data = new String[n];
        for (int i = 0; i < n; i++) data[i] = list.get(i);
        mergeSort(data, new String[n], 0, n);
        for (int i = 0; i < n; i++) list.set(i, data[i]);
    }

    private static void mergeSort(String[] a, String[] tmp, int lo, int hi) {
        if (hi - lo < 2) return;
        int mid = (lo + hi) >>> 1;
        mergeSort(a, tmp, lo, mid);
        mergeSort(a, tmp, mid, hi);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) tmp[k++] = a[i].compareTo(a[j]) <= 0 ? a[i++] : a[j++];
        while (i < mid) tmp[k++] = a[i++];
        while (j < hi) tmp[k++] = a[j++];
        for (k = lo; k < hi; k++) a[k] = tmp[k];
    }

    /** Redondea a 2 decimales. */
    public static double round2(double value) { return Math.round(value * 100.0) / 100.0; }
}
