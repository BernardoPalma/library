package bftsmart.demo.monitoringsystem.aggregator.function;

import java.math.BigDecimal;

public class BigDecimalAverageFunction implements AggregationFunction{
    @Override
    public Object execute(Object[] input) {

        BigDecimal[] in = (BigDecimal[]) input;

        BigDecimal result = new BigDecimal(0);

        for(BigDecimal v : in){
            result.add(v);
        }

        return result.divide(new BigDecimal(in.length));
    }

    @Override
    public Boolean validate(Object input) {
        return input instanceof BigDecimal;
    }
}
