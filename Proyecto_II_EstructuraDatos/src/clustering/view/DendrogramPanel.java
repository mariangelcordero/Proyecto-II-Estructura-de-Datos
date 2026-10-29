package clustering.view;

import clustering.model.iterators.MyIterator;
import clustering.model.structures.MyTreeNode;
import clustering.services.TextUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Dibuja el dendrograma horizontal: raíz a la izquierda, hojas a la derecha, eje de distancia abajo. */
public class DendrogramPanel extends JPanel {
    private static final int ROW_HEIGHT = 24;
    private static final int MARGIN_TOP = 30;
    private static final int MARGIN_LEFT = 30;
    private static final int TICKS = 5;

    private MyTreeNode root;
    private int plotWidth = 600;
    private int labelWidth = 150;
    private double maxDistance = 1;
    private int leafCounter;

    public DendrogramPanel() { setBackground(Color.WHITE); }

    public void setRoot(MyTreeNode root) {
        this.root = root;
        if (root != null) {
            maxDistance = 0;
            MyIterator<MyTreeNode> nodes = root.getIterator();
            while (nodes.hasNext()) maxDistance = Math.max(maxDistance, nodes.next().getDistance());
            if (maxDistance <= 0) maxDistance = 1;
            FontMetrics fm = getFontMetrics(getFont());
            labelWidth = 0;
            MyIterator<MyTreeNode> leaves = root.getLeafIterator();
            while (leaves.hasNext()) labelWidth = Math.max(labelWidth, fm.stringWidth(leaves.next().getLabel()));
        }
        updateSize();
        repaint();
    }

    public void zoom(int deltaPixels) {
        plotWidth = Math.max(200, plotWidth + deltaPixels);
        updateSize();
        repaint();
    }

    private void updateSize() {
        int leaves = root == null ? 1 : root.getLeafCount();
        setPreferredSize(new Dimension(MARGIN_LEFT + plotWidth + labelWidth + 40, MARGIN_TOP + leaves * ROW_HEIGHT + 70));
        revalidate();
    }

    private int xOf(double distance) {
        return MARGIN_LEFT + (int) Math.round((maxDistance - distance) / maxDistance * plotWidth);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (root == null) {
            g2.setColor(Color.GRAY);
            g2.drawString("Sin dendrograma: ejecute el clustering o cargue un archivo JSON.", 30, 40);
            g2.dispose();
            return;
        }
        leafCounter = 0;
        g2.setStroke(new BasicStroke(1.6f));
        drawNode(g2, root);
        drawAxis(g2);
        g2.dispose();
    }

    private int drawNode(Graphics2D g2, MyTreeNode node) {
        if (node.isLeaf()) {
            int y = MARGIN_TOP + leafCounter++ * ROW_HEIGHT;
            g2.setColor(Color.BLACK);
            g2.drawString(node.getLabel(), xOf(0) + 8, y + 4);
            return y;
        }
        int yLeft = drawNode(g2, node.getLeft());
        int yRight = drawNode(g2, node.getRight());
        int x = xOf(node.getDistance());
        g2.setColor(new Color(31, 78, 140));
        g2.drawLine(x, yLeft, x, yRight);
        g2.drawLine(x, yLeft, xOf(node.getLeft().getDistance()), yLeft);
        g2.drawLine(x, yRight, xOf(node.getRight().getDistance()), yRight);
        return (yLeft + yRight) / 2;
    }

    private void drawAxis(Graphics2D g2) {
        int y = MARGIN_TOP + root.getLeafCount() * ROW_HEIGHT + 4;
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(xOf(maxDistance), y, xOf(0), y);
        for (int t = 0; t <= TICKS; t++) {
            double value = maxDistance * t / TICKS;
            int x = xOf(value);
            g2.drawLine(x, y, x, y + 5);
            g2.drawString(Double.toString(TextUtils.round2(value)), x - 10, y + 20);
        }
        g2.drawString("Distancia", xOf(maxDistance / 2) - 20, y + 38);
    }
}
