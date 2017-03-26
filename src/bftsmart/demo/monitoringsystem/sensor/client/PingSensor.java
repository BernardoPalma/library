package bftsmart.demo.monitoringsystem.sensor.client;

import bftsmart.demo.monitoringsystem.message.MetricMessage;
import bftsmart.demo.monitoringsystem.message.SignedMessage;
import bftsmart.demo.monitoringsystem.util.SecurityUtils;
import bftsmart.demo.monitoringsystem.util.SerializableUtil;
import bftsmart.tom.ServiceProxy;

import java.io.*;
import java.math.BigDecimal;
import java.security.PrivateKey;

public class PingSensor {

    public static void main(String args[]) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java ...PingSensor <process id> <sensorId>");
            System.exit(-1);
        }

        Integer seqN = 0;
        Integer processId = Integer.parseInt(args[0]);
        Integer sensorId = Integer.parseInt(args[1]);
        String type = "Latency";
        PrivateKey privateKey = SecurityUtils.getPrivateKey("sensors/"+ type +"/keys/private" + sensorId + ".der");

        ServiceProxy sProxy = new ServiceProxy(processId, "monitor-config");

        String[] hosts = {"192.168.56.10", "192.168.56.11", "192.168.56.12", "192.168.56.13"};

        while (true) {

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            BigDecimal[] result = new BigDecimal[hosts.length];

            for (int i = 0; i < hosts.length; i++) {
                if (i == sensorId) {
                    result[i] = null;
                    continue;
                }

                ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", hosts[i]);
                Process process = pb.start();

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String s;
                String time = "-1";

                while ((s = stdInput.readLine()) != null) {
                    if (!s.contains("time=")) continue;

                    int index = s.indexOf("time=");
                    time = s.substring(index + "time=".length());
                    String[] split = time.split("ms");
                    time = split[0];

                    System.out.println(time);

                    break;
                }

                if (time.equals("-1")) {
                    result[i] = null;
                } else {
                    result[i] = new BigDecimal(time);
                }

            }

            MetricMessage ping = new MetricMessage(seqN, sensorId, type, result);
            SignedMessage message = new SignedMessage(ping, privateKey);


            sProxy.invokeOrdered(SerializableUtil.serialize(message));

            seqN++;
        }
    }
}
