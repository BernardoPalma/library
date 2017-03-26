package bftsmart.demo.monitoringsystem.aggregator;

import bftsmart.demo.monitoringsystem.aggregator.function.AggregationFunction;
import bftsmart.demo.monitoringsystem.message.MetricMessage;
import bftsmart.demo.monitoringsystem.message.SignedMessage;
import bftsmart.demo.monitoringsystem.sensor.Sensor;
import bftsmart.demo.monitoringsystem.util.SecurityUtils;
import bftsmart.demo.monitoringsystem.util.SerializableUtil;

import java.util.*;

public class Aggregator {

    private Map<String, Sensor> sensors;

    private Map<String, Map<Integer, Map<Integer, Object>>> map = new TreeMap<>();

    public Aggregator() {
        this.sensors = new HashMap<>();
    }

    public Aggregator(Map<String, Sensor> sensors) {
        this.sensors = sensors;
    }

    public boolean receiveMetric(SignedMessage signedMessage) {
        MetricMessage message = signedMessage.getMessage();
        Sensor sensor = sensors.get(message.getType());

        if (sensor == null) {
            System.out.println("[Aggregator] Unrecognizable type");
            return false;
        }

        //TODO maybe verify correct serialization of message

        if (!SecurityUtils.verifySignature(SerializableUtil.serialize(message), signedMessage.getSignature(), sensor.getPublicKey(message.getSensorId()))) {
            System.out.println("[Aggregator] Invalid Signature");
            return false;
        }

        if(!sensor.getAggrFunc().validate(message.getMetric())){
            System.out.println("[Aggregator] Incorrect metric value type");
            return false;
        }

        Map<Integer, Map<Integer, Object>> timeFrames = map.get(message.getType());
        if (timeFrames == null) {
            System.out.println("[Aggregator] Type still not initialized. Creating type " + message.getType());
            timeFrames = new HashMap<>();
            map.put(message.getType(), timeFrames);
        }

        Map<Integer, Object> timeframe = timeFrames.get(message.getSeqN());
        if (timeframe == null) {
            System.out.println("[Aggregator] New timeframe received. Creating timeframe with sequence number: " + message.getSeqN());
            timeframe = new HashMap<>();
            timeFrames.put(message.getSeqN(), timeframe);
        }

        if (timeframe.get(message.getSensorId()) != null) {
            System.out.println("[Aggregator] Duplicate Message");
            return false;
        }

        timeframe.put(message.getSensorId(), message.getMetric());

        if(reachedQuorum(timeframe, sensor.getQuorumNeeded())) {
            if(sensor.getAggrFunc() != null) {
                System.out.println("[Aggregator] Reached Consensus for type: " + message.getType() + ". Obtained value: " + sensor.getAggrFunc().execute(timeframe.values().toArray()).toString());
            } else {
                System.out.println("[Aggregator] Reached Consensus for type: " + message.getType() + ". Obtained value: " + timeframe.values().toArray().toString());
            }
            return true;
        } else {
            return false;
        }
    }

    public void addNewSensor(Sensor sensor) {
        if (!sensors.containsKey(sensor.getSensorType())) {
            sensors.put(sensor.getSensorType(), sensor);
        } else {
            System.out.println("[Aggregator] Sensor with the same type already exists.");
            return;
        }
    }

    public void removeSensor(String sensorType) {
        sensors.remove(sensorType);
    }

    public void changeAggregationFunction(String sensorType, AggregationFunction func) {
        Sensor sensor = sensors.get(sensorType);

        if (sensor != null) {
            sensor.setAggrFunc(func);
        } else {
            System.out.println("[Aggregator] Sensor does not exist.");
        }
    }

    private boolean reachedQuorum(Map<Integer, Object> messages, int quorum) {
        return messages.size() == quorum;
    }

}