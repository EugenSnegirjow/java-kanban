package task.manager.model;

import task.manager.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static task.manager.enums.TypeOfTasks.SUBTASK;

public class SubTask extends Task {
    private int epicTaskId;

    public SubTask(int id,
                   String title,
                   Status status,
                   String description,
                   LocalDateTime startTime,
                   Duration duration,
                   int epicTaskId
    ) {
        super(id, title, status, description, startTime, duration);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(int id, String title, Status status, String description, int epicTaskId) {
        super(id, title, status, description);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(String title, String description) {
        super(title, description);
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicTaskId == subTask.epicTaskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicTaskId);
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

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d",
                id, SUBTASK, title, status, description, startTime, endTime, duration, epicTaskId);
    }
}
