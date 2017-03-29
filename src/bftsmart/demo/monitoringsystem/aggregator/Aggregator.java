package bftsmart.demo.monitoringsystem.aggregator;

import bftsmart.demo.monitoringsystem.aggregator.function.AggregationFunction;
import bftsmart.demo.monitoringsystem.message.MetricMessage;
import bftsmart.demo.monitoringsystem.message.SignedMessage;
import bftsmart.demo.monitoringsystem.sensor.Sensor;
import bftsmart.demo.monitoringsystem.util.SecurityUtils;
import bftsmart.demo.monitoringsystem.util.SerializableUtil;

import java.util.*;

public class Aggregator {
    
    //TODO create new semantic, MetricMessage uses BigDecimal, EventMessage uses String.

    private Map<String, Sensor> sensors;

    private Map<String, Map<Integer, Map<Integer, Object>>> map = new TreeMap<>();

    public Aggregator() { this.sensors = new TreeMap<>(); }

    public Aggregator(Map<String, Sensor> sensors) {
        this.sensors = sensors;
    }

    public Object receiveMetric(SignedMessage signedMessage) {
        MetricMessage message = signedMessage.getMessage();
        Sensor sensor = sensors.get(message.getType());

        if (sensor == null) {
            System.out.println("[Aggregator] Unrecognizable type");
            return null;
        }

        //TODO maybe verify correct serialization of message

        if (!SecurityUtils.verifySignature(SerializableUtil.serialize(message), signedMessage.getSignature(), sensor.getPublicKey(message.getSensorId()))) {
            System.out.println("[Aggregator] Invalid Signature");
            return null;
        }

        if(!sensor.getAggrFunc().validate(message.getMetric())){
            System.out.println("[Aggregator] Incorrect metric value type");
            return null;
        }

        Map<Integer, Map<Integer, Object>> timeFrames = map.get(message.getType());
        if (timeFrames == null) {
            //System.out.println("[Aggregator] Type still not initialized. Creating type " + message.getType());
            timeFrames = new TreeMap<>();
            map.put(message.getType(), timeFrames);
        }

        Map<Integer, Object> timeframe = timeFrames.get(message.getSeqN());
        if (timeframe == null) {
            //System.out.println("[Aggregator] New timeframe received. Creating timeframe with sequence number: " + message.getSeqN());
            timeframe = new TreeMap<>();
            timeFrames.put(message.getSeqN(), timeframe);
        }

        if (timeframe.get(message.getSensorId()) != null) {
            System.out.println("[Aggregator] Duplicate Message");
            return null;
        }

        timeframe.put(message.getSensorId(), message.getMetric());

        if(reachedQuorum(timeframe, sensor.getQuorumNeeded())) {
            //System.out.println("[Aggregator] Reached Consensus for type: " + message.getType());
            if (sensor.getAggrFunc() != null) {
                return sensor.getAggrFunc().execute(timeframe.values().toArray());
            } else {
                return timeframe.values().toArray()[0];
            }
        }
        return null;
    }

    public void addNewSensor(Sensor sensor) {
        if (!sensors.containsKey(sensor.getIdentifier())) {
            sensors.put(sensor.getIdentifier(), sensor);
        } else {
            System.out.println("[Aggregator] Sensor with the same identifier already exists.");
            return;
        }
    }

    public void removeSensor(String identifier) {
        sensors.remove(identifier);
    }

    public void changeAggregationFunction(String identifier, AggregationFunction func) {
        Sensor sensor = sensors.get(identifier);

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