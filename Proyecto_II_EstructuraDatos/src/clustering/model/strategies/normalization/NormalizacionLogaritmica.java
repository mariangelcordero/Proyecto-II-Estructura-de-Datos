package clustering.model.strategies.normalization;

import clustering.model.strategies.NormalizacionStrategy;

public class NormalizacionLogaritmica implements NormalizacionStrategy {

    @Override public double[] normalizeColumn(double[] values) {
        double[] result = new double[values.length];

        if(values.length == 0){
            return result;
        }

        //Busca el valor minimo de la columna
        double min = values[0];

        for(double value : values){
            if(!Double.isFinite(value)){
                throw new IllegalArgumentException("El valor tiene que ser finito");
            }

            if(value < min){
                min = value;
            }
        }

        //Si se encuentran numeros negativos se mueve toda la columna
        double shift = min < 0 ? -min : 0;

        for(int i =0; i < values.length; i++){
            double adjusted = values[i] + shift;

            if(!Double.isFinite(adjusted)){
                throw new IllegalArgumentException("Error al mover los valores");
            }
            result[i] = Math.log1p(adjusted);
        }
        return result;
    }

    @Override public String getName() { return "Logarithmic"; }
}
