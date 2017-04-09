package bftsmart.demo.monitoringsystem.storage;

import java.util.*;

public class TSDatabase {

    private int maxStorage;
    private int snapshotCounter;
    private Map<String, List<DecidedValue>> currentSnapshot;
    private SortedMap<Long, Map<String,  List<DecidedValue>>> tSDatabase;

    public TSDatabase(int maxStorage){
        this.snapshotCounter = 0;
        this.maxStorage = maxStorage;
        this.currentSnapshot = new TreeMap<>();
        this.tSDatabase = new TreeMap<>(Comparator.comparing(Long::longValue).reversed());
    }

    public void insertValue(String type, long timestamp, DecidedValue val){
        List<DecidedValue> values = currentSnapshot.get(type);
        if(values == null){
            values = new ArrayList<>();
        }
        values.add(val);

        currentSnapshot.put(type, values);
    }

    public void selectValues(String type, int start, int amount){

    }

    public long createSnapshot(){
        snapshotCounter++;

        tSDatabase.put(new Long(snapshotCounter), currentSnapshot);
        currentSnapshot = new TreeMap<>();

        if(tSDatabase.size() >= maxStorage){
            tSDatabase.remove(tSDatabase.lastKey());
        }

        return snapshotCounter;
    }
}
