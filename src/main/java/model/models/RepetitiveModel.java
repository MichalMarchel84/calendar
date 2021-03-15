package model.models;

import model.models.EntryModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RepetitiveModel extends EntryModel {

    private LocalDateTime startAt;
    private LocalDateTime finishedAt;
    private int interval;

    public RepetitiveModel(int entryID, String title, String description, LocalDateTime startAt, LocalDateTime finishedAt, int interval) {
        super(entryID, title, description);
        this.startAt = startAt;
        this.finishedAt = finishedAt;
        this.interval = interval;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public LocalDateTime getFirstAfter(LocalDateTime time){
        LocalDateTime res = null;
        time = time.minusMinutes(1); //counts including given time
        if((finishedAt == null) || time.isBefore(finishedAt)) {
            res = LocalDateTime.from(startAt);
            while (res.isBefore(time)) {
                if (interval == -1) {
                    res = res.plusYears(1);
                } else if (interval == 0) {
                    res = res.plusMonths(1);
                } else {
                    res = res.plusDays(interval);
                }
            }
        }
        return res;
    }
}
