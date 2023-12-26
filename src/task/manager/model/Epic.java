package task.manager.model;

import task.manager.service.InMemoryTaskManager;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIDs;

    public Epic(String title, String description) {
        super(title, description);
    }

    public ArrayList<Integer> getSubTaskIDs() {
        return subTaskIDs;
    }

    public void addSubTaskIDs(int subTaskID) {
        subTaskIDs.add(subTaskID);
    }

    public void removeSubtaskID(int id) {
        subTaskIDs.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(this.subTaskIDs, epic.subTaskIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIDs);
    }

    @Override
    public String toString() {
        StringBuilder tasksList = new StringBuilder(ID + "-" + title + ":");
        if (subTaskIDs == null) {
            tasksList.append("\n\tПодзадач нет");
            return tasksList.toString();
        }
        for (Integer subTaskID : subTaskIDs) {
            tasksList.append("\n\t").append(InMemoryTaskManager.subTasks.get(subTaskID));
        }
        return tasksList.toString();
    }
}
