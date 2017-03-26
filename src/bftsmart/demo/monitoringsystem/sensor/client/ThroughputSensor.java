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
    int rate;
    String type;
    PrivateKey privateKey;
    ServiceProxy sProxy;

    public ThroughputSensor(StatisticDefaultRecoverable replica, int processId, int rate){
        this.replica = replica;
        this.seqN = 0;
        this.sensorId = replica.getReplica().getId();

        System.out.println("ADFGSDFGSDFGSDFGSDFGSDFGSDG" + sensorId);

        this.processId = processId + sensorId;
        this.rate = rate;
        this.type = "Throughput";
        this.privateKey = SecurityUtils.getPrivateKey("sensors/"+ type +"/keys/private" + sensorId + ".der");
        sProxy = new ServiceProxy(processId, "monitor-config");
    }

    public void run() {

        System.out.println("Throughput: " + replica.getThroughput());
        BigDecimal result = new BigDecimal(replica.getThroughput());
        replica.resetThroughput();

        MetricMessage thrput = new MetricMessage(seqN, sensorId, type, (result.compareTo(new BigDecimal(0)) == 0 ? result : result.divide(new BigDecimal(rate))));
        SignedMessage message = new SignedMessage(thrput, privateKey);

        sProxy.invokeOrdered(SerializableUtil.serialize(message));

        seqN++;
    }
}
