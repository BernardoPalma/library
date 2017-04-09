package bftsmart.demo.monitoringsystem.sensor;

import bftsmart.demo.monitoringsystem.aggregator.function.AggregationFunction;
import com.yahoo.ycsb.generator.IntegerGenerator;

import java.security.PublicKey;
import java.util.List;

public class Sensor {

    private final String identifier;
    private String sensorType;
    private int faultsAllowed;
    private int quorumNeeded;
    private List<PublicKey> publicKeys;
    private AggregationFunction aggrFunc;

    public Sensor(String identifier, String sensorType, int faultsAllowed, int quorumNeeded, List<PublicKey> publicKeys, AggregationFunction aggregationFunction) {

        this.identifier = identifier;
        this.sensorType = sensorType;
        this.faultsAllowed = faultsAllowed;
        this.quorumNeeded = quorumNeeded;
        this.publicKeys = publicKeys;
        this.aggrFunc = aggregationFunction;

    }

    public String getIdentifier() { return identifier; }

    public String getSensorType() {
        return sensorType;
    }

    public Integer getFaultsAllowed() { return faultsAllowed; }

    public void setFaultsAllowed(int faultsAllowed) { this.faultsAllowed = faultsAllowed; }

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
