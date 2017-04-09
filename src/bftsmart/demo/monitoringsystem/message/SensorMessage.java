package bftsmart.demo.monitoringsystem.message;

import java.io.Serializable;

public abstract class SensorMessage<T> implements Serializable{

    public abstract int getSeqN();

    public abstract int getSensorId();

    public abstract String getType();

    public abstract T getContent();

}
