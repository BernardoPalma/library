package bftsmart.demo.monitoringsystem.message;

public class EventMessage extends SensorMessage<String>{

    private final int seqN;
    private final int sensorId;
    private final String type;
    private final String event;

    public EventMessage(int seqN, int sensorId, String type, String event) {
        this.seqN = seqN;
        this.sensorId = sensorId;
        this.type = type;
        this.event = event;
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

    public String getContent() {
        return event;
    }

    @Override
    public String toString(){
        return "Message " + seqN + " with type: " + type + " from sensor: " + sensorId;
    }
}
