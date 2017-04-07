package bftsmart.demo.monitoringsystem.storage;

public class DecidedValue {

    private final String type;
    private final Object value;

    public DecidedValue(String type, Object value){
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
