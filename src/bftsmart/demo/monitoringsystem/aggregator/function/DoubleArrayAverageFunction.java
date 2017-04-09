package bftsmart.demo.monitoringsystem.aggregator.function;

import java.util.ArrayList;
import java.util.List;

public class DoubleArrayAverageFunction implements AggregationFunction{

    @Override
    public Object execute(Object[] input, int f) {

        List<Double> result = new ArrayList<>();

        Double[][] in = new Double[input.length][];

        for(int x = 0; x < input.length; x++){
            in[x] = (Double[]) input[x];
        }


        for(int i = 0; i < in[0].length; i++){
            Double sum = 0.0;
            for(int j = 0; j < in.length; j++){
                if(in[j][i] != null)
                    sum += in[j][i];
            }
            result.add(i, sum/(in[0].length-1));
        }

        return result;
    }

    @Override
    public Boolean validate(Object input) {
        return input instanceof Double[];
    }
}
