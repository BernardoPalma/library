package bftsmart.demo.monitoringsystem.aggregator.function;

import java.math.BigDecimal;

public class BigDecimalNoOpFunction implements AggregationFunction {
    @Override
    public Object execute(Object[] input, int f) {
        return input[0];
    }

    @Override
    public Boolean validate(Object input) {
        return input instanceof BigDecimal;
    }
}
