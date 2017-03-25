package bftsmart.demo.monitoringsystem.sensor.client;


import bftsmart.demo.monitoringsystem.message.MetricMessage;
import bftsmart.demo.monitoringsystem.message.SignedMessage;
import bftsmart.demo.monitoringsystem.replica.StatisticDefaultRecoverable;
import bftsmart.demo.monitoringsystem.util.SecurityUtils;
import bftsmart.demo.monitoringsystem.util.SerializableUtil;
import bftsmart.tom.ServiceProxy;

import java.math.BigDecimal;
import java.security.PrivateKey;

public class ThroughputSensor implements Runnable {

    StatisticDefaultRecoverable replica;
    Integer seqN;
    Integer processId;
    Integer sensorId;
    String type = "Latency";
    PrivateKey privateKey;
    ServiceProxy sProxy;

    public ThroughputSensor(StatisticDefaultRecoverable replica, int processId){
        this.replica = replica;
        this.seqN = 0;
        this.processId = processId;
        this.sensorId = replica.getReplica().getId();
        this.type = "Throughput";
        this.privateKey = SecurityUtils.getPrivateKey("sensors/"+ type +"/keys/private" + sensorId + ".der");
        sProxy = new ServiceProxy(processId, "monitor-config");
    }

    public void run() {

        BigDecimal result = new BigDecimal(replica.getThroughput());
        replica.resetThroughput();

        MetricMessage ping = new MetricMessage(seqN, sensorId, type, result);
        SignedMessage message = new SignedMessage(ping, privateKey);

        sProxy.invokeOrdered(SerializableUtil.serialize(message));

        seqN++;
    }
}
