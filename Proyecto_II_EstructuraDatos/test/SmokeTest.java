import clustering.model.ClusteringModel;
import clustering.model.Configuration;
import clustering.model.entities.ColumnSetting;
import clustering.model.structures.MyTreeNode;
import clustering.view.DendrogramPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SmokeTest {
    public static void main(String[] args) throws Exception {
        String csv = args[0];
        String[] norms = { "Min-Max", "Z-Score", "Logarithmic" };
        String[] dists = { "Euclidean", "Manhattan", "Cosine", "Hamming" };
        ClusteringModel m = new ClusteringModel();

        // 1) Réplica de la configuración original (todas las columnas activas, título incluido como categórico)
        for (String n : norms) for (String d : dists) {
            m.loadCsv(csv);
            Configuration cfg = Configuration.getInstance();
            for (int i = 0; i < cfg.getColumns().size(); i++) cfg.getColumns().get(i).setIgnored(false);
            m.configureStrategies(n, d);
            m.runClustering();
            MyTreeNode r = m.getDisplayedRoot();
            System.out.printf("ORIG-STYLE %-12s %-10s dim=%d root=%.2f left=%.2f right=%.2f%n", n, d, m.getDimension(),
                    r.getDistance(), r.getLeft().getDistance(), r.getRight().getDistance());
        }

        // 2) Configuración por defecto de la app
        m.loadCsv(csv);
        Configuration cfg = Configuration.getInstance();
        System.out.println("Detected types:");
        for (int i = 0; i < cfg.getColumns().size(); i++) {
            ColumnSetting s = cfg.getColumns().get(i);
            System.out.println("  " + s.getName() + " -> " + s.getType() + (s.isIgnored() ? " (ignorada/etiqueta)" : ""));
        }
        m.configureStrategies("Min-Max", "Euclidean");
        m.runClustering();
        System.out.println("Default run: dim=" + m.getDimension() + " root=" + m.getDisplayedRoot().getDistance());
        m.exportDendrogram("/tmp/out.json");
        MyTreeNode back = m.loadDendrogram("/tmp/out.json");
        System.out.println("Reloaded leaves=" + back.getLeafCount() + " rootD=" + back.getDistance());

        DendrogramPanel p = new DendrogramPanel();
        p.setRoot(back);
        p.setSize(p.getPreferredSize());
        BufferedImage img = new BufferedImage(p.getWidth(), p.getHeight(), BufferedImage.TYPE_INT_RGB);
        p.paint(img.createGraphics());
        ImageIO.write(img, "png", new File("/tmp/dendro.png"));

        // 3) Cargar también el JSON producido por la versión C#
        MyTreeNode orig = m.loadDendrogram(args[1]);
        System.out.println("C# JSON loaded: leaves=" + orig.getLeafCount() + " rootD=" + orig.getDistance());
    }
}
