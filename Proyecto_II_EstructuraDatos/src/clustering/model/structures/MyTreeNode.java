package clustering.model.structures;

import clustering.model.iterators.MyIterable;
import clustering.model.iterators.MyIterator;
import clustering.model.iterators.TreeLeafIterator;
import clustering.model.iterators.TreePreOrderIterator;

/** Nodo de árbol binario del dendrograma. Las hojas llevan etiqueta; los nodos internos, distancia de fusión. */
public class MyTreeNode implements MyIterable<MyTreeNode> {
    private final int id;
    private final String label;
    private final MyTreeNode left;
    private final MyTreeNode right;
    private final double distance;
    private final int leafCount;

    /** Hoja. */
    public MyTreeNode(int id, String label) {
        this.id = id;
        this.label = label;
        this.left = null;
        this.right = null;
        this.distance = 0;
        this.leafCount = 1;
    }

    /** Nodo interno (fusión de dos clústeres). */
    public MyTreeNode(int id, MyTreeNode left, MyTreeNode right, double distance) {
        if (left == null || right == null) throw new IllegalArgumentException("Un nodo interno requiere dos hijos");
        this.id = id;
        this.label = "";
        this.left = left;
        this.right = right;
        this.distance = distance;
        this.leafCount = left.leafCount + right.leafCount;
    }

    public int getId() { return id; }
    public String getLabel() { return label; }
    public MyTreeNode getLeft() { return left; }
    public MyTreeNode getRight() { return right; }
    public double getDistance() { return distance; }
    public int getLeafCount() { return leafCount; }
    public boolean isLeaf() { return left == null && right == null; }

    @Override public MyIterator<MyTreeNode> getIterator() { return new TreePreOrderIterator(this); }
    public MyIterator<MyTreeNode> getLeafIterator() { return new TreeLeafIterator(this); }
}
