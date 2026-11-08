package clustering.services;

import clustering.model.entities.DataPoint;
import clustering.model.strategies.DistanciaStrategy;
import clustering.model.structures.MyList;
import clustering.model.structures.MyMatrix;
import clustering.model.structures.MyTreeNode;

public class HierarchicalClustering {
    private final MyList<DataPoint> dataPoints;
    private final DistanciaStrategy strategy;
    private final String linkageMethod;
    private MyMatrix distances;
    private MyTreeNode root;


    public HierarchicalClustering(MyList<DataPoint> dataPoints,
                                  DistanciaStrategy strategy,
                                  String linkageMethod) {

        if (dataPoints == null || dataPoints.isEmpty())
            throw new IllegalArgumentException("No hay datos para agrupar");

        if (strategy == null)
            throw new IllegalArgumentException("Seleccione una función de distancia");

        this.dataPoints = dataPoints;
        this.strategy = strategy;
        this.linkageMethod = linkageMethod;
    }

    // Matriz cuadrada simétrica con diagonal 0. Solo se calcula el triángulo superior
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
        if (distances == null) {
            buildDistanceMatrix();
        }

        int n = dataPoints.size();

        MyMatrix work = new MyMatrix(n, n);

        // Copiar la matriz original de distancias
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                work.set(i, j, distances.get(i, j));
            }
        }

        MyTreeNode[] clusters = new MyTreeNode[n];
        boolean[] active = new boolean[n];
        int[] clusterSizes = new int[n];

        // Inicializar los clusters individuales
        for (int i = 0; i < n; i++) {
            clusters[i] = new MyTreeNode(
                    i,
                    (i + 1) + ": " + dataPoints.get(i).getName()
            );

            active[i] = true;
            clusterSizes[i] = 1;
        }

        int nextId = n;

        // Realizar n-1 fusiones
        for (int step = 0; step < n - 1; step++) {

            double min = Double.MAX_VALUE;
            int a = -1;
            int b = -1;

            // Buscar los dos clusters activos más cercanos
            for (int i = 0; i < n; i++) {
                if (!active[i]) continue;

                for (int j = i + 1; j < n; j++) {
                    if (active[j] && work.get(i, j) < min) {
                        min = work.get(i, j);
                        a = i;
                        b = j;
                    }
                }
            }

            // Guardar los tamaños originales
            int sizeA = clusterSizes[a];
            int sizeB = clusterSizes[b];

            // Crear el nuevo cluster
            clusters[a] = new MyTreeNode(
                    nextId++,
                    clusters[a],
                    clusters[b],
                    min
            );

            // Actualizar el tamaño del nuevo cluster
            clusterSizes[a] = sizeA + sizeB;

            // Desactivar el cluster que se fusionó
            active[b] = false;

            // Actualizar las distancias con los demás clusters
            for (int k = 0; k < n; k++) {

                if (!active[k] || k == a) continue;

                double d;

                if (linkageMethod.equals("Enlace Simple")) {

                    // Tomar la distancia mínima
                    d = Math.min(
                            work.get(a, k),
                            work.get(b, k)
                    );

                } else if (linkageMethod.equals("Enlace Promedio")) {

                    // Calcular el promedio ponderado
                    d = (
                            work.get(a, k) * sizeA
                                    + work.get(b, k) * sizeB
                    ) / (sizeA + sizeB);

                } else {
                    throw new IllegalArgumentException(
                            "Método de enlace desconocido: " + linkageMethod
                    );
                }

                work.set(a, k, d);
                work.set(k, a, d);
            }
        }

        // Obtener el cluster raíz
        for (int i = 0; i < n; i++) {
            if (active[i]) {
                root = clusters[i];
            }
        }

        return root;
    }


    public MyMatrix getDistanceMatrix() { return distances; }
    public MyTreeNode getRoot() { return root; }
}
