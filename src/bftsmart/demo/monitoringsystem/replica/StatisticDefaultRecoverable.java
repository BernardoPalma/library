package bftsmart.demo.monitoringsystem.replica;

import bftsmart.demo.monitoringsystem.sensor.client.ActiveClientsSensor;
import bftsmart.demo.monitoringsystem.sensor.client.ThroughputSensor;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultRecoverable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class StatisticDefaultRecoverable extends DefaultRecoverable{
    int throughput = 0;
    ServiceReplica replica;

    public void initStatistics() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(new ThroughputSensor(this, 2001, 1), 30, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new ActiveClientsSensor(this, 2011), 30, 1, TimeUnit.SECONDS);
    }

    public int getThroughput() { return throughput; }

    public void resetThroughput(){
        throughput=0;
    }

    public int getActiveClients(){
        return replica.getActiveClients();
    }

    public ServiceReplica getReplica() {
        return replica;
    }
}
