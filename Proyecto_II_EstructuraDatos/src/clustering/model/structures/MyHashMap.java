package clustering.model.structures;

/** Tabla hash con claves String y encadenamiento. put/get/containsKey: O(1) promedio; rehash al superar 0.75. */
public class MyHashMap<V> {
    private static class Entry<E> {
        final String key;
        E value;
        Entry<E> next;
        Entry(String key, E value, Entry<E> next) { this.key = key; this.value = value; this.next = next; }
    }

    private Entry<V>[] buckets;
    private int size;

    @SuppressWarnings("unchecked")
    public MyHashMap() { buckets = (Entry<V>[]) new Entry[16]; }

    private int indexFor(String key, int capacity) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) hash = 31 * hash + key.charAt(i);
        return (hash & 0x7fffffff) % capacity;
    }

    public void put(String key, V value) {
        if (key == null) throw new IllegalArgumentException("La clave no puede ser nula");
        int index = indexFor(key, buckets.length);
        for (Entry<V> e = buckets[index]; e != null; e = e.next) {
            if (e.key.equals(key)) { e.value = value; return; }
        }
        buckets[index] = new Entry<>(key, value, buckets[index]);
        size++;
        if (size > buckets.length * 3 / 4) resize();
    }

    public V get(String key) {
        for (Entry<V> e = buckets[indexFor(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) return e.value;
        }
        return null;
    }

    public boolean containsKey(String key) {
        for (Entry<V> e = buckets[indexFor(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) return true;
        }
        return false;
    }

    public int size() { return size; }

    public MyList<String> keys() {
        MyList<String> result = new MyList<>();
        for (Entry<V> bucket : buckets) {
            for (Entry<V> e = bucket; e != null; e = e.next) result.add(e.key);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<V>[] old = buckets;
        Entry<V>[] bigger = (Entry<V>[]) new Entry[old.length * 2];
        for (Entry<V> bucket : old) {
            Entry<V> e = bucket;
            while (e != null) {
                Entry<V> next = e.next;
                int index = indexFor(e.key, bigger.length);
                e.next = bigger[index];
                bigger[index] = e;
                e = next;
            }
        }
        buckets = bigger;
    }
}
