package bftsmart.demo.monitoringsystem.aggregator.function;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class FaultTolerantBigDecimalAverageFunction implements AggregationFunction{

    @Override
    public Object execute(Object[] input, int f) {

        BigDecimal[] in = new BigDecimal[input.length];

        for(int x = 0; x < input.length; x++){
            in[x] = (BigDecimal) input[x];
        }

        Arrays.sort(in);
        BigDecimal[] values = Arrays.copyOfRange(in, f, in.length - f);

        BigDecimal result = new BigDecimal(0);

        for(BigDecimal v : values){
            result = result.add(v);
        }

        return result.divide(new BigDecimal(in.length), RoundingMode.FLOOR);
    }

    @Override
    public Boolean validate(Object input) {
        return input instanceof BigDecimal;
    }
}
