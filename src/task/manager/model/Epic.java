package task.manager.model;

import task.manager.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import static task.manager.enums.TypeOfTasks.EPIC;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds;

    public Epic(int id, String title, Status status, String description, LocalDateTime startTime, Duration duration) {
        super(id, title, status, description, startTime, duration);
        setEndTime(duration);
        subTaskIds = new ArrayList<>();
    }

    public Epic(int id, String title, Status status, String description) {
        super(id, title, status, description);
        subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description) {
        super(title, description);
        subTaskIds = new ArrayList<>();
    }

    public void setEndTime(Duration duration) {
        endTime = startTime.plus(duration);
    }

    public void setStartTimeEndTimeAndDuration(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }


    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskId(Integer subTaskID) {
        subTaskIds.add(subTaskID);
    }

    public void removeSubtaskId(Integer id) {
        subTaskIds.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(this.subTaskIds, epic.subTaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String startTime = null;
        if (this.startTime != null) {
            startTime = this.startTime.format(formatter);
        }
        String endTime = null;
        if (getEndTime() != null) {
            endTime = this.endTime.format(formatter);
        }

        String duration = null;
        if (this.duration != null) {
            duration = String.format("%s", this.duration.toMinutes());
        }

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                id, EPIC, title, status, description, startTime, endTime, duration);
    }
}
