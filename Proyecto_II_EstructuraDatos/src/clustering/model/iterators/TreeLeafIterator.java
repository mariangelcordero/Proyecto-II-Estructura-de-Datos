package clustering.model.iterators;

import clustering.model.structures.MyStack;
import clustering.model.structures.MyTreeNode;

/** Entrega las hojas de izquierda a derecha, de forma perezosa e iterativa. */
public class TreeLeafIterator implements MyIterator<MyTreeNode> {
    private final MyTreeNode root;
    private MyStack<MyTreeNode> stack;
    private MyTreeNode lookahead;
    private MyTreeNode current;

    public TreeLeafIterator(MyTreeNode root) {
        if (root == null) throw new IllegalArgumentException("La raíz no puede ser nula");
        this.root = root;
        reset();
    }

    private MyTreeNode findNextLeaf() {
        while (!stack.isEmpty()) {
            MyTreeNode node = stack.pop();
            if (node.isLeaf()) return node;
            if (node.getRight() != null) stack.push(node.getRight());
            if (node.getLeft() != null) stack.push(node.getLeft());
        }
        return null;
    }

    @Override public boolean hasNext() { return lookahead != null; }

    @Override public MyTreeNode next() {
        if (lookahead == null) throw new IllegalStateException("No hay más hojas");
        current = lookahead;
        lookahead = findNextLeaf();
        return current;
    }

    @Override public void reset() {
        stack = new MyStack<>();
        stack.push(root);
        current = null;
        lookahead = findNextLeaf();
    }

    @Override public MyTreeNode current() {
        if (current == null) throw new IllegalStateException("Llame next() primero");
        return current;
    }
}
