package clustering.model.structures;

/** Pila (LIFO) construida por composición sobre {@link MyLinkedList}. */
public class MyStack<T> {
    private final MyLinkedList<T> list = new MyLinkedList<>();

    public void push(T value) { list.addFirst(value); }

    public T pop() { return list.removeFirst(); }

    public boolean isEmpty() { return list.isEmpty(); }

    public int size() { return list.size(); }
}
