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
import pt.pasadinhas.App;
import pt.pasadinhas.adaptation.Adaptation;
import pt.pasadinhas.adaptation.AdaptationInstance;
import pt.pasadinhas.component.Component;
import pt.pasadinhas.goals.MinimizationGoal;
import pt.pasadinhas.storage.Storage;
import pt.pasadinhas.communication.ServerReplica;
import pt.pasadinhas.engine.Engine;
import pt.pasadinhas.parser.PolicyFileParser;
import udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.Map;

public class MonitorServer extends DefaultRecoverable {

    ServiceReplica replica;
    private Aggregator aggregator;
    private int snapshotCounter = 0;
    private int counter = 0;
    String currentLeader = "0";

    public MonitorServer(int id){
        Map<String, Sensor> sensors = SensorLoadingUtil.loadSensors();
        aggregator = sensors != null ? new Aggregator(sensors) : new Aggregator();
        replica = new ServiceReplica(id, "monitor-config", this, this, null, new DefaultReplier());
        Expression.init();
        System.out.println("WERGSDFGSERGWEGE" + App.class.getClassLoader().getResource("Test.policy").getFile());
        //PolicyFileParser.parse("Test.policy", Engine.getInstance());
        Engine.register(new Component("replica"));
        Engine.register(new MinimizationGoal(new Expression("latency")));
        Adaptation adaptation = new Adaptation("NewLeader") {
            @Override
            public void execute(Component.Instance... instances) {
                Engine.getInstance().sendReconfiguration("switchleader", instances);
            }
        };
        adaptation.addImpactFunction("latency", new Expression(":r.Latency"));
        adaptation.addInputVariable("replica", "r");
        Engine.register(adaptation);
        Engine.component("replica").addInstance("0");
        Engine.component("replica").addInstance("1");
        Engine.component("replica").addInstance("2");
        Engine.component("replica").addInstance("3");
        try {
            Engine.register(new ServerReplica("0", "192.168.56.10", 12000));
            Engine.register(new ServerReplica("1", "192.168.56.11", 12010));
            Engine.register(new ServerReplica("2", "192.168.56.12", 12020));
            Engine.register(new ServerReplica("3", "192.168.56.13", 12030));
        } catch(Exception e){
            e.printStackTrace();
        }
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
        snapshotCounter++;
        System.out.println("Decided value in snapshot " + snapshotCounter + " for type: " + type + ". Obtained: " + value.toString());

        if(type.equals("Latency")){
            System.out.println("Im IN");
            BigDecimal[] v = (BigDecimal[]) value;
            int i = 0;
            for (BigDecimal vl: v) {
                Storage.put("replica." + i + ".Latency", vl);
                i++;
            }

            if(counter++%20 == 0) {
                AdaptationInstance adaptationInstance = Engine.getInstance().run();
                if (adaptationInstance.instances[0].getId().equals(currentLeader)) {
                    return;
                }
                adaptationInstance.execute();
                currentLeader = adaptationInstance.instances[0].getId();
            }

        }


    }
}
