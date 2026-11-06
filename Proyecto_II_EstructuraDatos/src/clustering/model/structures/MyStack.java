package clustering.model.structures;

/** Pila (LIFO) construida por composición sobre {@link ListaLinqueada}. */
public class MyStack<T> {
    private final ListaLinqueada<T> list = new ListaLinqueada<>();

    public void push(T value) { list.addFirst(value); }

    public T pop() { return list.removeFirst(); }

    public boolean isEmpty() { return list.isEmpty(); }

    public int size() { return list.size(); }
}
