package clustering.services;

import clustering.model.entities.DataPoint;
import clustering.model.strategies.DistanceStrategy;
import clustering.model.structures.MyList;
import clustering.model.structures.MyMatrix;
import clustering.model.structures.MyTreeNode;

/**
 * Clustering jerárquico aglomerativo (enlace simple).
 * buildDistanceMatrix: O(n^2 * D). performClustering: n-1 fusiones, cada una busca el mínimo en O(k^2)
 * y actualiza una fila en O(k) -> O(n^3) en total, con memoria O(n^2).
 */
public class HierarchicalClustering {
    private final MyList<DataPoint> dataPoints;
    private final DistanceStrategy strategy;
    private MyMatrix distances;
    private MyTreeNode root;

    public HierarchicalClustering(MyList<DataPoint> dataPoints, DistanceStrategy strategy) {
        if (dataPoints == null || dataPoints.isEmpty()) throw new IllegalArgumentException("No hay datos para agrupar");
        if (strategy == null) throw new IllegalArgumentException("Seleccione una función de distancia");
        this.dataPoints = dataPoints;
        this.strategy = strategy;
    }

    /** Matriz cuadrada simétrica con diagonal 0. Solo se calcula el triángulo superior. */
    public void buildDistanceMatrix() {
        int n = dataPoints.size();
        distances = new MyMatrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double d = strategy.calculate(dataPoints.get(i).getVectorData(), dataPoints.get(j).getVectorData());
                distances.set(i, j, d);
                distances.set(j, i, d);
            }
        }
    }

    public MyTreeNode performClustering() {
        if (distances == null) buildDistanceMatrix();
        int n = dataPoints.size();
        MyMatrix work = new MyMatrix(n, n);
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) work.set(i, j, distances.get(i, j));

        MyTreeNode[] clusters = new MyTreeNode[n];
        boolean[] active = new boolean[n];
        for (int i = 0; i < n; i++) {
            clusters[i] = new MyTreeNode(i, (i + 1) + ": " + dataPoints.get(i).getName());
            active[i] = true;
        }
        int nextId = n;
        for (int step = 0; step < n - 1; step++) {
            double min = Double.MAX_VALUE;
            int a = -1, b = -1;
            for (int i = 0; i < n; i++) {
                if (!active[i]) continue;
                for (int j = i + 1; j < n; j++) {
                    if (active[j] && work.get(i, j) < min) { min = work.get(i, j); a = i; b = j; }
                }
            }
            clusters[a] = new MyTreeNode(nextId++, clusters[a], clusters[b], min);
            active[b] = false;
            for (int k = 0; k < n; k++) {
                if (!active[k] || k == a) continue;
                double d = Math.min(work.get(a, k), work.get(b, k)); // enlace simple (Lance-Williams)
                work.set(a, k, d);
                work.set(k, a, d);
            }
        }
        for (int i = 0; i < n; i++) if (active[i]) root = clusters[i];
        return root;
    }

    public MyMatrix getDistanceMatrix() { return distances; }
    public MyTreeNode getRoot() { return root; }
}
