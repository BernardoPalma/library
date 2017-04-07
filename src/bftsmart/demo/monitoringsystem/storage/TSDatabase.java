package bftsmart.demo.monitoringsystem.storage;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TSDatabase {

    private Map<Long, DecidedValue> database;

    public TSDatabase(){
        this.database = new TreeMap<>(Comparator.comparing(Long::longValue).reversed());
    }

    public void insertValue(String type, long timestamp, DecidedValue val){

    }

    public void selectValues(String type, int start, int amount){

    }
}
