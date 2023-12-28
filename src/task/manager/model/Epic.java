package task.manager.model;

import task.manager.service.taskManager.InMemoryTaskManager;
import task.manager.service.taskManager.TaskManager;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds;

    public Epic(String title, String description) {
        super(title, description);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskIds(int subTaskID) {
        subTaskIds.add(subTaskID);
    }

    public void removeSubtaskId(int id) {
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
        TaskManager taskManager = new InMemoryTaskManager();
        StringBuilder tasksList = new StringBuilder(id + "-" + title + ":");
        if (subTaskIds == null) {
            tasksList.append("\n\tПодзадач нет");
            return tasksList.toString();
        }
        for (Integer subTaskId : subTaskIds) {
            tasksList.append("\n\t").append(taskManager.getSubTask(subTaskId));
        }
        return tasksList.toString();
    }
}
