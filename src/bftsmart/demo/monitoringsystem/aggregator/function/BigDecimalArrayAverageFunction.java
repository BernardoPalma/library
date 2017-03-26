package bftsmart.demo.monitoringsystem.aggregator.function;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BigDecimalArrayAverageFunction implements AggregationFunction {

    @Override
    public Object execute(Object[] input) {


        BigDecimal[][] in = new BigDecimal[input.length][];

        for(int x = 0; x < input.length; x++){
            in[x] = (BigDecimal[]) input[x];
        }

        BigDecimal[] result = new BigDecimal[in[0].length];

        for(int i = 0; i < in[0].length; i++){
            BigDecimal sum = new BigDecimal(0);
            for(int j = 0; j < in.length; j++){
                if(in[j][i] != null)
                    sum.add(in[j][i]);
            }
            result[i] = sum.divide(new BigDecimal(in[0].length-1));
        }

        return result;
    }

    @Override
    public Boolean validate(Object input) {
        return input instanceof BigDecimal[];
    }
}
