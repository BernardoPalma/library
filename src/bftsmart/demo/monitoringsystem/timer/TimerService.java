package bftsmart.demo.monitoringsystem.timer;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TimerService {

    private Map<String, Thread> timers;
    private final ScheduledExecutorService scheduler;

    public TimerService(){
        this.timers = new TreeMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

}
