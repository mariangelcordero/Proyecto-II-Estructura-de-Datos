package clustering;

import clustering.controller.ClusteringController;
import clustering.model.ClusteringModel;

public class Main {

    public static void main(String[] args) {

        ClusteringModel model = new ClusteringModel();

        ClusteringController controller =
                new ClusteringController(model);

        controller.start();
    }
}