package clustering.services;

import clustering.model.iterators.MyIterator;
import clustering.model.structures.MyTreeNode;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Exporta el árbol a JSON: { "n": nombre, "d": distancia, "c": [hijos] }.
 * Cada nodo lista las hojas que contiene; O(n) por nodo -> O(n * profundidad) en total.
 */
public class DendrogramExporter {
    public void export(MyTreeNode root, String path) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)) {
            writer.write(toJson(root));
        }
    }

    public String toJson(MyTreeNode root) {
        StringBuilder sb = new StringBuilder();
        writeNode(root, sb, 0);
        return sb.toString();
    }

    private void writeNode(MyTreeNode node, StringBuilder sb, int level) {
        String pad = "  ".repeat(level);
        sb.append(pad).append("{\n");
        sb.append(pad).append("  \"n\": \"").append(escape(nodeName(node))).append("\",\n");
        sb.append(pad).append("  \"d\": ").append(TextUtils.round2(node.getDistance())).append(",\n");
        if (node.isLeaf()) {
            sb.append(pad).append("  \"c\": []\n");
        } else {
            sb.append(pad).append("  \"c\": [\n");
            writeNode(node.getLeft(), sb, level + 2);
            sb.append(",\n");
            writeNode(node.getRight(), sb, level + 2);
            sb.append("\n").append(pad).append("  ]\n");
        }
        sb.append(pad).append("}");
    }

    private String nodeName(MyTreeNode node) {
        StringBuilder name = new StringBuilder("(");
        MyIterator<MyTreeNode> leaves = node.getLeafIterator();
        boolean first = true;
        while (leaves.hasNext()) {
            if (!first) name.append(", ");
            name.append(leaves.next().getLabel());
            first = false;
        }
        return name.append(")").toString();
    }

    private String escape(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c);
            }
        }
        return sb.toString();
    }
}
