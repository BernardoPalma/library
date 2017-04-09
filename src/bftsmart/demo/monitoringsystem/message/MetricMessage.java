package bftsmart.demo.monitoringsystem.message;

import java.io.Serializable;
import java.math.BigDecimal;

public class MetricMessage extends SensorMessage<BigDecimal> {

    private final int seqN;
    private final int sensorId;
    private final String type;
    private final BigDecimal metric;


    public MetricMessage(int seqN, int sensorId, String type, BigDecimal metric) {
        this.seqN = seqN;
        this.sensorId = sensorId;
        this.type = type;
        this.metric = metric;
    }

    public int getSeqN() {
        return seqN;
    }

    public int getSensorId() {
        return sensorId;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getContent() {
        return metric;
    }

    @Override
    public String toString(){
        return "Message " + seqN + " with type: " + type + " from sensor: " + sensorId;
    }
}
