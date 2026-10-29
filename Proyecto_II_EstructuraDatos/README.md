# ClusteringSystem (Java) – Dendrograma y clasificación jerárquica

Portado desde la versión C#. Java 17+ (probado con 21). Aplicación de escritorio (Swing).

## Compilar y ejecutar
Con JDK instalado:

    # Linux / macOS
    ./build.sh && java -jar ClusteringSystem.jar
    # Windows
    build.bat && java -jar ClusteringSystem.jar

## Uso
1. **Cargar CSV** (hay un `movies_dataset_mini.csv` de ejemplo).
2. Elegir **normalización** (Min-Max, Z-Score, Logarítmica) y **distancia** (Euclidiana, Manhattan, Coseno, Hamming).
3. En la tabla: cambiar el **tipo** de cada variable, marcar **Ignorar** y ajustar el **peso**.
   La **columna de etiqueta** (nombre de cada registro) se elige en el combo; por defecto se ignora como variable.
4. **Ejecutar clustering** → pestañas *Dendrograma*, *Matriz de distancias* y *Resumen*.
5. **Exportar JSON** / **Cargar JSON (visualizador)**: formato `{ "n": nombre, "d": distancia, "c": [hijos] }`,
   compatible con el `dendrogram.json` de la versión C#.

## Estructura (MVC)
- `model/structures`: MyList, MyLinkedList, MyStack, MyHashMap, MyVector, MyMatrix, MyTreeNode (propias)
- `model/iterators`: Iterator (lista, vector, matriz, árbol preorden, hojas)
- `model/strategies` + `model/factories`: Strategy y Factory Method (normalización y distancias)
- `model/Configuration`: Singleton
- `services`: CsvParser, DataProcessor, HierarchicalClustering, DendrogramExporter/Reader (JSON propio)
- `controller`, `view`: MVC (Swing)

## Notas
- No se usa `java.util` en el código del proyecto. Solo se usan `java.lang`, `java.io`/`StandardCharsets`
  (lectura/escritura de archivos) y Swing/AWT (ventana, inevitable para la interfaz).
- `test/SmokeTest.java`: prueba sin ventana (carga, clustering, export/import y render a PNG).
