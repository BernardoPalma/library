package bftsmart.demo.monitoringsystem.util;

import bftsmart.demo.monitoringsystem.aggregator.function.AggregationFunction;
import bftsmart.demo.monitoringsystem.sensor.Sensor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.PublicKey;
import java.util.*;
import java.util.regex.Pattern;

public class SensorLoadingUtil {

    private static Map<String, Sensor> sensors;

    public static Map<String, Sensor> loadSensors(){

        sensors = new TreeMap<>();
        File configHome = new File("sensors");

        if(configHome.exists() && configHome.isDirectory()){
            for(File sensorDir : configHome.listFiles()){
                if(sensorDir.isDirectory()) {
                    String path = sensorDir.getPath();
                    List<PublicKey> publicKeys = new ArrayList<>();

                    final Pattern p = Pattern.compile("public\\d*\\.der");

                    File keysFolder = new File(path + "/keys");

                    File[] pKeys = keysFolder.listFiles(file -> p.matcher(file.getName()).matches());

                    Arrays.sort(pKeys);

                    for(File key: pKeys) {
                        PublicKey pk = SecurityUtils.getPublicKey(key);
                        if (pk == null) break;
                        publicKeys.add(pk);
                    }

                    if (publicKeys.isEmpty()) {
                        System.out.println("[Sensor Config] Missing keys in " + sensorDir.getPath());
                        continue;
                    }

                    String configFile = path + "/sensor.config";
                    Sensor s = parseConfig(configFile, publicKeys);
                    if (s != null) {
                        sensors.put(s.getIdentifier(), s);
                    }
                }
            }
            if(!sensors.isEmpty()) return sensors;
        }

        return null;
    }

    private static Sensor parseConfig(String configFile, List<PublicKey> publicKeys){

        String identifier = null;
        String type = null;
        Integer faults = null;
        Integer quorum = null;
        AggregationFunction aggr = null;

        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.startsWith("#")){
                    StringTokenizer str = new StringTokenizer(line,"=");
                    if(str.countTokens() > 1){
                        switch (str.nextToken().trim()){
                            case "identifier":
                                identifier=str.nextToken().trim();
                                break;
                            case "type":
                                type = str.nextToken().trim();
                                break;
                            case "faults":
                                faults = Integer.parseInt(str.nextToken().trim());
                                break;
                            case "quorum":
                                quorum = Integer.parseInt(str.nextToken().trim());
                                break;
                            case "aggregationFunction":
                                try {
                                    aggr = (AggregationFunction) Class.forName(str.nextToken().trim()).newInstance();
                                } catch(Exception e){
                                    aggr = null;
                                }
                                break;
                            default:
                                System.out.println("[SensorConfig] Unnecessary parameter: " + str.nextToken());
                        }
                    }
                }
            }

            if (identifier != null && type != null && faults!=null && quorum != null) {
                return new Sensor(identifier, type, faults, quorum, publicKeys, aggr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
