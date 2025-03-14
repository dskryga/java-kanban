package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds;
    private LocalDateTime endTime;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTaskIds = new ArrayList<>();
    }

    public void addSubTask(Integer subTaskId) {
        subTaskIds.add(subTaskId);
    }

    public void removeSubTask(Integer subTaskId) {
        subTaskIds.remove(subTaskId);
    }

    public void clearSubTaskIds() {
        subTaskIds.clear();
    }

    public ArrayList<Integer> getSubTaskIds() {
        ArrayList<Integer> returnedSubTaskIds = subTaskIds;
        return returnedSubTaskIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
