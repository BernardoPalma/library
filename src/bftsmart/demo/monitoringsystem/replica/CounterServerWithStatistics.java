package bftsmart.demo.monitoringsystem.replica;

import bftsmart.demo.monitoringsystem.sensor.client.ActiveClientsSensor;
import bftsmart.demo.monitoringsystem.sensor.client.ThroughputSensor;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CounterServerWithStatistics extends StatisticDefaultRecoverable {
    private int counter = 0;
    private int iterations = 0;

    public CounterServerWithStatistics(int id) {
        replica = new ServiceReplica(id, this, this);
    }

    @Override
    public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs) {


        byte [][] replies = new byte[commands.length][];
        for (int i = 0; i < commands.length; i++) {
            if(msgCtxs != null && msgCtxs[i] != null) {
                replies[i] = executeSingle(commands[i],msgCtxs[i]);
            }
            else executeSingle(commands[i],null);
        }

        return replies;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {

        iterations++;
        throughput++;
        System.out.println("(" + iterations + ") Reading counter at value: " + counter);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(4);
            new DataOutputStream(out).writeInt(counter);
            return out.toByteArray();
        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        }
    }

    private byte[] executeSingle(byte[] command, MessageContext msgCtx) {
        iterations++;
        throughput++;
        try {
            int increment = new DataInputStream(new ByteArrayInputStream(command)).readInt();
            //System.out.println("read-only request: "+(msgCtx.getConsensusId() == -1));
            counter += increment;

            if (msgCtx != null) {
                if (msgCtx.getConsensusId() == -1) {
                    System.out.println("(" + iterations + ") Counter was incremented: " + counter);
                } else {
                    System.out.println("(" + iterations + " / " + msgCtx.getConsensusId() + ") Counter was incremented: " + counter);
                }
            }
            else {
                System.out.println("(" + iterations + ") Counter was incremented: " + counter);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(4);
            new DataOutputStream(out).writeInt(counter);
            return out.toByteArray();
        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        }
    }

    public static void main(String[] args){
        if(args.length < 1) {
            System.out.println("Use: java CounterServer <processId>");
            System.exit(-1);
        }
        CounterServerWithStatistics cs = new CounterServerWithStatistics(Integer.parseInt(args[0]));

        cs.initStatistics();

    }


    @SuppressWarnings("unchecked")
    @Override
    public void installSnapshot(byte[] state) {
        try {
            System.out.println("setState called");
            ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis);
            counter =  in.readInt();
            in.close();
            bis.close();
        } catch (Exception e) {
            System.err.println("[ERROR] Error deserializing state: "
                    + e.getMessage());
        }
    }

    @Override
    public byte[] getSnapshot() {
        try {
            System.out.println("getState called");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeInt(counter);
            out.flush();
            bos.flush();
            out.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException ioe) {
            System.err.println("[ERROR] Error serializing state: "
                    + ioe.getMessage());
            return "ERROR".getBytes();
        }
    }
}
