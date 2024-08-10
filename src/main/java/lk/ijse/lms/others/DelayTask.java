package lk.ijse.lms.others;

import javafx.concurrent.Task;

public class DelayTask extends Task<Void> {
    private final long delayMillis;

    public DelayTask(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    protected Void call() throws Exception {
        Thread.sleep(delayMillis);  // Add the delay
        return null;
    }
}
