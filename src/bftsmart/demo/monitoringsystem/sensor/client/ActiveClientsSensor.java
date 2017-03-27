package bftsmart.demo.monitoringsystem.sensor.client;

import bftsmart.demo.monitoringsystem.message.MetricMessage;
import bftsmart.demo.monitoringsystem.message.SignedMessage;
import bftsmart.demo.monitoringsystem.replica.StatisticDefaultRecoverable;
import bftsmart.demo.monitoringsystem.util.SecurityUtils;
import bftsmart.demo.monitoringsystem.util.SerializableUtil;
import bftsmart.tom.ServiceProxy;

import java.math.BigDecimal;
import java.security.PrivateKey;

public class ActiveClientsSensor implements Runnable{

    StatisticDefaultRecoverable replica;
    Integer seqN;
    Integer processId;
    Integer sensorId;
    String type;
    PrivateKey privateKey;
    ServiceProxy sProxy;

    public ActiveClientsSensor(StatisticDefaultRecoverable replica, int processId){
        this.replica = replica;
        this.seqN = 0;
        this.sensorId = replica.getReplica().getId();
        this.processId = processId + sensorId;
        this.type = "ActiveClients";
        this.privateKey = SecurityUtils.getPrivateKey("sensors/"+ type +"/keys/private" + sensorId + ".der");
        sProxy = new ServiceProxy(this.processId, "monitor-config");
    }

    public void run() {

        BigDecimal result = new BigDecimal(replica.getActiveClients());

        MetricMessage actcli = new MetricMessage(seqN, sensorId, type, result);
        SignedMessage message = new SignedMessage(actcli, privateKey);

        sProxy.invokeOrdered(SerializableUtil.serialize(message));

        seqN++;
    }
}
