package model.daos;

import model.App;
import model.models.RepetitiveReminderModel;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RepetitiveReminderDao extends RepetitiveDao{

    public RepetitiveReminderDao(int clientID, Connection conn) {
        super(0, clientID, conn);
    }

    public RepetitiveReminderDao(int clientID) {
        super(0, clientID, App.conn);
    }

    public void create(RepetitiveReminderModel model){
        String sql = "INSERT INTO repetitive_reminders VALUES(?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement s = super.getConn().prepareStatement(sql);
            s.setInt(1, super.getClientID());
            s.setInt(2, model.getEntryID());
            s.setInt(3, toUnixTime(model.getStartAt()));
            if(model.getFinishedAt() == null) {
                s.setNull(4, Types.NULL);
            }
            else {
                s.setInt(4, toUnixTime(model.getFinishedAt()));
            }
            s.setInt(5, model.getInterval());
            s.setString(6, model.getTitle());
            s.setString(7, model.getDescription());
            s.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void update(RepetitiveReminderModel model){
        String sql = "UPDATE repetitive_reminders SET started_at = ?, finished_at = ?, interval = ?, title = ?, description = ?";
        try{
            PreparedStatement s = super.getConn().prepareStatement(sql);
            s.setInt(1, toUnixTime(model.getStartAt()));
            if(model.getFinishedAt() == null) {
                s.setNull(2, Types.NULL);
            }
            else {
                s.setInt(2, toUnixTime(model.getFinishedAt()));
            }
            s.setInt(3, model.getInterval());
            s.setString(4, model.getTitle());
            s.setString(5, model.getDescription());
            s.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<RepetitiveReminderModel> getBetween(LocalDateTime t1, LocalDateTime t2){
        if(t1.isAfter(t2)){
            LocalDateTime temp = t1;
            t1 = t2;
            t2 = temp;
        }
        ArrayList<RepetitiveReminderModel> list = new ArrayList<>();
        ArrayList<RepetitiveReminderModel> instances = new ArrayList<>();
        try{

            //looking for instances valid for given period in DB
            ResultSet set = super.getInstancesBetween(t1, t2);
            while (set.next()){
                int id = set.getInt("entry_id");
                LocalDateTime startedAt = toLocalTime(set.getInt("started_at"));
                LocalDateTime finishedAt;
                if(set.getInt("finished_at") != 0) {
                    finishedAt = toLocalTime(set.getInt("finished_at"));
                }
                else {
                    finishedAt = null;
                }
                String title = set.getString("title");
                String description = set.getString("description");
                int interval = set.getInt("interval");
                instances.add(new RepetitiveReminderModel(id, title, description, startedAt, finishedAt, interval));
            }

            //generating occurrences in given period
            for(RepetitiveReminderModel m : instances){
                LocalDateTime occurrenceTime = m.getFirstAfter(t1);
                while ((occurrenceTime != null) && occurrenceTime.isBefore(t2)){
                    list.add(m.copy(occurrenceTime));
                    occurrenceTime = m.getFirstAfter(occurrenceTime.plusDays(1));
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
}