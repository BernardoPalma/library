package bftsmart.demo.monitoringsystem.aggregator.function;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalAverageFunction implements AggregationFunction{
    @Override
    public Object execute(Object[] input) {

        BigDecimal[] in = new BigDecimal[input.length];

        for(int x = 0; x < input.length; x++){
            in[x] = (BigDecimal) input[x];
        }

        BigDecimal result = new BigDecimal(0);

        for(BigDecimal v : in){
            result = result.add(v);
        }

        return result.divide(new BigDecimal(in.length), RoundingMode.FLOOR);
    }

    @Override
    public Boolean validate(Object input) {
        return input instanceof BigDecimal;
    }
}
