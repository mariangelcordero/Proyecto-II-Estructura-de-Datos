package clustering;

import clustering.controller.ClusteringController;
import clustering.model.ClusteringModel;

public class Main {
    public static void main(String[] args) {
        new ClusteringController(new ClusteringModel()).start();
    }
}
