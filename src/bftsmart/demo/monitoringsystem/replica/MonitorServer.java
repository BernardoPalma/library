package bftsmart.demo.monitoringsystem.replica;

import bftsmart.demo.monitoringsystem.aggregator.Aggregator;
import bftsmart.demo.monitoringsystem.message.EventMessage;
import bftsmart.demo.monitoringsystem.message.MetricMessage;
import bftsmart.demo.monitoringsystem.message.SensorMessage;
import bftsmart.demo.monitoringsystem.message.SignedMessage;
import bftsmart.demo.monitoringsystem.sensor.Sensor;
import bftsmart.demo.monitoringsystem.storage.DecidedValue;
import bftsmart.demo.monitoringsystem.storage.TSDatabase;
import bftsmart.demo.monitoringsystem.util.SensorLoadingUtil;
import bftsmart.demo.monitoringsystem.util.SerializableUtil;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultRecoverable;
import bftsmart.tom.server.defaultservices.DefaultReplier;

import java.util.Map;

public class MonitorServer extends DefaultRecoverable {

    ServiceReplica replica;
    private Aggregator aggregator;
    private TSDatabase storage;

    public MonitorServer(int id){
        Map<String, Sensor> sensors = SensorLoadingUtil.loadSensors();
        aggregator = sensors != null ? new Aggregator(sensors) : new Aggregator();
        storage = new TSDatabase(1000);
        replica = new ServiceReplica(id, "monitor-config", this, this, null, new DefaultReplier());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use: java MonitorServer <processId>");
            System.exit(-1);
        }

        new MonitorServer(Integer.parseInt(args[0]));
    }

    @Override
    public void installSnapshot(byte[] state) {

    }

    @Override
    public byte[] getSnapshot() {
        return new byte[0];
    }

    @Override
    public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs) {
        byte [][] replies = new byte[commands.length][];
        for (int i = 0; i < commands.length; i++) {
            if (msgCtxs != null && msgCtxs[i] != null) {
                replies[i] = executeSingle(commands[i], msgCtxs[i]);
            }
        }
        return replies;
    }

    public byte[] executeSingle(byte[] command, MessageContext msgCtx){
        Object o = SerializableUtil.deserialize(command);
        if(o instanceof SignedMessage) {
            SignedMessage sm = (SignedMessage) o;
            if(sm.getMessage() instanceof SensorMessage){
                Object value = aggregator.receiveMetric(sm);
                if(value != null){
                    if(sm.getMessage() instanceof MetricMessage){
                        handleMetric(sm.getMessage().getType(), value);
                    } else if(sm.getMessage() instanceof EventMessage){
                        handleEvent(sm.getMessage().getType(), value);
                    }

                }
            }
        }

        return new byte[0];
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        return new byte[0];
    }

    private void handleMetric(String type, Object value){
        System.out.println("Decided metric value for type: " + type + ". Obtained: " + value.toString());
        storage.insertValue(type, 0, new DecidedValue(type, value));
    }

    private void handleEvent(String type, Object value){
        System.out.println("Decided event for type: " + type + ". Obtained: " + value.toString());
    }
}
