package bftsmart.demo.monitoringsystem.replica;

import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultRecoverable;

public abstract class StatisticDefaultRecoverable extends DefaultRecoverable{
    int throughput = 0;
    ServiceReplica replica;

    public int getThroughput(){ return throughput; }

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
