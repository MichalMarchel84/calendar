package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RepetitiveEventModel extends RepetitiveModel {

    private long duration;

    RepetitiveEventModel(int entryID, LocalDateTime time, LocalDateTime finishedAt, int interval, long duration, String title, String description) {
        super(entryID, time, finishedAt, interval, title, description);
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {

        this.duration = duration;
        fireActionEvent(4, "Duration changed");
    }

    public ArrayList<LocalDateTime> getBetween(LocalDateTime t1, LocalDateTime t2){
        ArrayList<LocalDateTime> list = new ArrayList<>();
        LocalDateTime occurrenceTime = getFirstAfter(t1.minusMinutes(getDuration())); //including result for ongoing event
        while ((occurrenceTime != null) && occurrenceTime.isBefore(t2.plusMinutes(1))){ //including result for t2
            list.add(occurrenceTime);
            occurrenceTime = getFirstAfter(occurrenceTime);
        }
        return list;
    }

    @Override
    public String toString(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String fin;
        if(getFinishedAt() == null){
            fin = "ongoing";
        }
        else {
            fin = getFinishedAt().format(dtf);
        }
        return "id: " + getEntryID() + " started at: " + getTime().format(dtf) + " finished at: " + fin + " duration: " + duration;
    }
}
