package bftsmart.demo.monitoringsystem.replica;

import bftsmart.demo.monitoringsystem.aggregator.Aggregator;
import bftsmart.demo.monitoringsystem.message.MetricMessage;
import bftsmart.demo.monitoringsystem.message.SignedMessage;
import bftsmart.demo.monitoringsystem.sensor.Sensor;
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

    public MonitorServer(int id){
        Map<String, Sensor> sensors = SensorLoadingUtil.loadSensors();
        aggregator = sensors != null ? new Aggregator(sensors) : new Aggregator();
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
            if(sm.getMessage() instanceof MetricMessage){
                Object value = aggregator.receiveMetric(sm);
                if(value != null){
                    handleDecidedValue(sm.getMessage().getType(), value);
                }
            }
        }

        return new byte[0];
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        return new byte[0];
    }

    private void handleDecidedValue(String type, Object value){
        System.out.println("Decided value for type: " + type + ". Obtained: " + value.toString());
    }
}
