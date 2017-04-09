package bftsmart.demo.monitoringsystem.aggregator.function;

public interface AggregationFunction {

    Object execute(Object[] input, int f);

    Boolean validate(Object input);

}
