package clustering.services;

import clustering.model.structures.MyList;
import clustering.model.structures.MyTreeNode;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Lector JSON propio (descenso recursivo) para el formato { "n", "d", "c" }. O(longitud del archivo). */
public class DendrogramReader {
    private String text;
    private int pos;
    private int nextId;

    public MyTreeNode read(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            int code;
            while ((code = reader.read()) != -1) if (code != '\uFEFF') sb.append((char) code);
        }
        return parse(sb.toString());
    }

    public MyTreeNode parse(String json) {
        text = json;
        pos = 0;
        nextId = 0;
        MyTreeNode root = parseNode();
        skipWhitespace();
        if (pos != text.length()) throw error("Contenido inesperado después del nodo raíz");
        return root;
    }

    private MyTreeNode parseNode() {
        skipWhitespace();
        expect('{');
        String label = "";
        double distance = 0;
        MyList<MyTreeNode> children = new MyList<>();
        boolean first = true;
        while (true) {
            skipWhitespace();
            if (peek() == '}') { pos++; break; }
            if (!first) { expect(','); skipWhitespace(); }
            first = false;
            String key = parseString();
            skipWhitespace();
            expect(':');
            skipWhitespace();
            switch (key) {
                case "n": label = parseString(); break;
                case "d": distance = parseNumber(); break;
                case "c": parseChildren(children); break;
                default: throw error("Clave desconocida '" + key + "'");
            }
        }
        if (children.size() == 0) return new MyTreeNode(nextId++, stripParentheses(label));
        if (children.size() == 2) return new MyTreeNode(nextId++, children.get(0), children.get(1), distance);
        throw error("Cada nodo debe tener 0 o 2 hijos (tiene " + children.size() + ")");
    }

    private void parseChildren(MyList<MyTreeNode> children) {
        expect('[');
        skipWhitespace();
        if (peek() == ']') { pos++; return; }
        while (true) {
            children.add(parseNode());
            skipWhitespace();
            if (peek() == ',') { pos++; continue; }
            expect(']');
            return;
        }
    }

    private String parseString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (pos < text.length()) {
            char c = text.charAt(pos++);
            if (c == '"') return sb.toString();
            if (c != '\\') { sb.append(c); continue; }
            if (pos >= text.length()) break;
            char e = text.charAt(pos++);
            switch (e) {
                case 'n': sb.append('\n'); break;
                case 'r': sb.append('\r'); break;
                case 't': sb.append('\t'); break;
                case 'b': sb.append('\b'); break;
                case 'f': sb.append('\f'); break;
                case 'u':
                    if (pos + 4 > text.length()) throw error("Secuencia \\u incompleta");
                    sb.append((char) Integer.parseInt(text.substring(pos, pos + 4), 16));
                    pos += 4;
                    break;
                default: sb.append(e);
            }
        }
        throw error("Cadena sin cerrar");
    }

    private double parseNumber() {
        int start = pos;
        while (pos < text.length() && "+-0123456789.eE".indexOf(text.charAt(pos)) >= 0) pos++;
        String number = text.substring(start, pos);
        if (!TextUtils.isNumber(number)) throw error("Número inválido");
        return TextUtils.parseNumber(number);
    }

    private String stripParentheses(String label) {
        String s = label.trim();
        if (s.length() >= 2 && s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') return s.substring(1, s.length() - 1);
        return s;
    }

    private void skipWhitespace() {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) pos++;
    }

    private char peek() {
        if (pos >= text.length()) throw error("Fin inesperado del archivo");
        return text.charAt(pos);
    }

    private void expect(char c) {
        if (peek() != c) throw error("Se esperaba '" + c + "'");
        pos++;
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException("JSON inválido en la posición " + pos + ": " + message);
    }
}
