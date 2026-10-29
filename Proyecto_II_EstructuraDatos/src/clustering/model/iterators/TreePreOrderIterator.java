package clustering.model.iterators;

import clustering.model.structures.MyStack;
import clustering.model.structures.MyTreeNode;

/** Recorrido preorden iterativo (usa pila propia, sin recursión). */
public class TreePreOrderIterator implements MyIterator<MyTreeNode> {
    private final MyTreeNode root;
    private MyStack<MyTreeNode> stack;
    private MyTreeNode current;

    public TreePreOrderIterator(MyTreeNode root) {
        if (root == null) throw new IllegalArgumentException("La raíz no puede ser nula");
        this.root = root;
        reset();
    }

    @Override public boolean hasNext() { return !stack.isEmpty(); }

    @Override public MyTreeNode next() {
        if (!hasNext()) throw new IllegalStateException("No hay más nodos");
        current = stack.pop();
        if (current.getRight() != null) stack.push(current.getRight());
        if (current.getLeft() != null) stack.push(current.getLeft());
        return current;
    }

    @Override public void reset() {
        stack = new MyStack<>();
        stack.push(root);
        current = null;
    }

    @Override public MyTreeNode current() {
        if (current == null) throw new IllegalStateException("Llame next() primero");
        return current;
    }
}
