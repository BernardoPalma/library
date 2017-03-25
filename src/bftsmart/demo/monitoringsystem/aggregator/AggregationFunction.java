package bftsmart.demo.monitoringsystem.aggregator;

public interface AggregationFunction {

    Object execute(Object[] input);

    //TODO probably add a data validator for the metrics being received

}
