package task.manager.model;

import java.util.Objects;

public class SubTask extends Task {
    int epicTaskID;

    public SubTask(String title, String description, String status, int epicTaskID) {
        super(title, description, status);
        this.epicTaskID = epicTaskID;
    }

    public int getEpicTaskID() {
        return epicTaskID;
    }

    public void setEpicTaskID(int epicTaskID) {
        this.epicTaskID = epicTaskID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicTaskID == subTask.epicTaskID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicTaskID);
    }
}
