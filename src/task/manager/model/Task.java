package task.manager.model;

import task.manager.enums.Status;
import task.manager.enums.TypeOfTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected Duration duration;

    public Task(int id,
                String title,
                Status status,
                String description,
                LocalDateTime startTime,
                Duration duration
    ) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = getEndTime();
    }

    public Task(int id, String title, Status status, String description) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String title, String description) {
        this.status = Status.NEW;
        this.title = title;
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return LocalDateTime.from(startTime).plus(duration);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String startTime = null;
        if (this.startTime != null) {
            startTime = this.startTime.format(formatter);
        }
        String endTime = null;
        if (this.endTime != null) {
            endTime = this.endTime.format(formatter);
        }

        String duration = null;
        if (this.duration != null) {
            duration = String.format("%s", this.duration.toMinutes());
        }

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                id, TypeOfTasks.TASK, title, status, description, startTime, endTime, duration);
    }
}
