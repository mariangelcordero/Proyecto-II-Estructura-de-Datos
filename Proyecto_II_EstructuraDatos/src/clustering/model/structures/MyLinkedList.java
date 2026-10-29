package clustering.model.structures;

/** Lista simplemente enlazada con referencia a cabeza y cola. addFirst/addLast/removeFirst: O(1). */
public class MyLinkedList<T> {
    private static class Node<E> {
        final E value;
        Node<E> next;
        Node(E value) { this.value = value; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        node.next = head;
        head = node;
        if (tail == null) tail = node;
        size++;
    }

    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) head = node; else tail.next = node;
        tail = node;
        size++;
    }

    public T removeFirst() {
        if (head == null) throw new IllegalStateException("La lista está vacía");
        T value = head.value;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return value;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }
}
