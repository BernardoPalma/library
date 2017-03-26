package bftsmart.demo.monitoringsystem.sensor.client;


import bftsmart.demo.monitoringsystem.message.MetricMessage;
import bftsmart.demo.monitoringsystem.message.SignedMessage;
import bftsmart.demo.monitoringsystem.util.SecurityUtils;
import bftsmart.demo.monitoringsystem.util.SerializableUtil;
import bftsmart.tom.ServiceProxy;

import java.io.IOException;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.security.PrivateKey;

public class CpuSensor {

    public static void main(String args[]) throws IOException {

        if (args.length < 2) {
            System.out.println("Usage: java ...CpuSensor <process id> <typeid>");
            System.exit(-1);
        }

        Integer seqN = 0;
        Integer processId = Integer.parseInt(args[0]);
        Integer typeId = Integer.parseInt(args[1]);
        String type = "CpuLoad" + typeId;
        PrivateKey privateKey = SecurityUtils.getPrivateKey("sensors/" + type + "/keys/private0.der");

        ServiceProxy sProxy = new ServiceProxy(processId, "monitor-config");

        OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        Double warmup = bean.getSystemCpuLoad();

        while(warmup < 0){
            warmup = bean.getSystemCpuLoad();
        }

        BigDecimal result;

        while (true) {

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            result = new BigDecimal(bean.getSystemCpuLoad() * 100);

            System.out.println("SystemCPULoad: " + result);

            MetricMessage value = new MetricMessage(seqN, 0, type, result);
            SignedMessage message = new SignedMessage(value, privateKey);


            sProxy.invokeOrdered(SerializableUtil.serialize(message));

            seqN++;

        }
    }
}
