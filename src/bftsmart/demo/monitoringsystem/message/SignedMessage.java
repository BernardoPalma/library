package bftsmart.demo.monitoringsystem.message;

import bftsmart.demo.monitoringsystem.sensor.Sensor;
import bftsmart.demo.monitoringsystem.util.SecurityUtils;
import bftsmart.demo.monitoringsystem.util.SerializableUtil;

import java.io.Serializable;
import java.security.PrivateKey;

public class SignedMessage implements Serializable {

    private SensorMessage message;
    private byte[] signature;

    public SignedMessage(SensorMessage message, PrivateKey privateKey) {

        byte[] digest = SerializableUtil.serialize(message);

        this.message = message;
        this.signature = SecurityUtils.sign(digest, privateKey);
    }

    public SensorMessage getMessage() {
        return message;
    }

    public byte[] getSignature() {
        return signature;
    }
}
