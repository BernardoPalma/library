package bftsmart.demo.monitoringsystem.sensor;

import bftsmart.demo.monitoringsystem.aggregator.AggregationFunction;

import java.security.PublicKey;
import java.util.List;

public class Sensor {

    private final String sensorType;
    private Integer quorumNeeded;
    private List<PublicKey> publicKeys;
    private AggregationFunction aggrFunc;

    public Sensor(String sensorType, Integer quorumNeeded, List<PublicKey> publicKeys, AggregationFunction aggregationFunction) {

        this.sensorType = sensorType;
        this.quorumNeeded = quorumNeeded;
        this.publicKeys = publicKeys;
        this.aggrFunc = aggregationFunction;

    }

    public String getSensorType() {
        return sensorType;
    }

    public int getQuorumNeeded() {
        return quorumNeeded;
    }

    public void setQuorumNeeded(int quorumNeeded) {
        this.quorumNeeded = quorumNeeded;
    }

    public List<PublicKey> getPublicKeys() {
        return publicKeys;
    }

    public void setPublicKeys(List<PublicKey> publicKeys) {
        this.publicKeys = publicKeys;
    }

    public PublicKey getPublicKey(int sensorId) {

        if (sensorId >= 0 && sensorId < publicKeys.size()) return publicKeys.get(sensorId);

        return null;
    }

    public AggregationFunction getAggrFunc() {
        return aggrFunc;
    }

    public void setAggrFunc(AggregationFunction aggrFunc) {
        this.aggrFunc = aggrFunc;
    }

}
