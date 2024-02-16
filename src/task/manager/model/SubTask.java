package task.manager.model;

import task.manager.service.taskManager.Status;

import java.util.Objects;

public class SubTask extends Task {
    private int epicTaskId;

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
        return String.format("%d,%s,%s,%s,%s,%d\n", id, TypeOfTasks.SUBTASK, title, status, description, epicTaskId);
    }
}
