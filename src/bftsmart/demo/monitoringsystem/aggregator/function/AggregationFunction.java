package bftsmart.demo.monitoringsystem.aggregator.function;

public interface AggregationFunction {

    Object execute(Object[] input);

    Boolean validate(Object input);

}
