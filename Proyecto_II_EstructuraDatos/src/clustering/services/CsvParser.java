package clustering.services;

import clustering.model.structures.MyList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Lector CSV propio (RFC 4180 básico): comillas, comas y saltos de línea dentro de campos, CRLF y BOM. O(caracteres). */
public class CsvParser {
    public MyList<String[]> parse(String path) throws IOException {
        MyList<String[]> rows = new MyList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            MyList<String> row = new MyList<>();
            StringBuilder field = new StringBuilder();
            boolean inQuotes = false;
            boolean first = true;
            int code;
            while ((code = reader.read()) != -1) {
                char c = (char) code;
                if (first) { first = false; if (c == '\uFEFF') continue; }
                if (inQuotes) {
                    if (c == '"') {
                        reader.mark(1);
                        int next = reader.read();
                        if (next == '"') field.append('"');
                        else { if (next != -1) reader.reset(); inQuotes = false; }
                    } else {
                        field.append(c);
                    }
                } else if (c == '"' && field.length() == 0) {
                    inQuotes = true;
                } else if (c == ',') {
                    row.add(field.toString());
                    field.setLength(0);
                } else if (c == '\n') {
                    finishRow(rows, row, field);
                    row = new MyList<>();
                } else if (c != '\r') {
                    field.append(c);
                }
            }
            if (field.length() > 0 || row.size() > 0) finishRow(rows, row, field);
        }
        return rows;
    }

    private void finishRow(MyList<String[]> rows, MyList<String> row, StringBuilder field) {
        row.add(field.toString());
        field.setLength(0);
        boolean blank = row.size() == 1 && row.get(0).trim().isEmpty();
        if (blank) return;
        String[] cells = new String[row.size()];
        for (int i = 0; i < cells.length; i++) cells[i] = row.get(i);
        rows.add(cells);
    }
}
