package manager;

import tasks.*;

import java.util.HashMap;

public class TaskManager {
    private int taskCounter;
    HashMap<Integer, Task> taskList;
    HashMap<Integer, SubTask> subTaskList;
    HashMap<Integer, Epic> epicList;

    public TaskManager() {
        taskCounter = 0;
        taskList = new HashMap<>();
        subTaskList = new HashMap<>();
        epicList = new HashMap<>();
    }

    private int setTaskId() {
        taskCounter++;
        return taskCounter;
    }

    public void addTask(Task task) {
        taskList.put(setTaskId(), task);
    }

    public void addEpic(Epic epic) {
        epicList.put(setTaskId(), epic);
    }

    public void addSubTask(SubTask subTask) {
        subTaskList.put(setTaskId(), subTask);
    }

    public HashMap<Integer, Task> showTaskList() {
        return taskList;
    }

    public HashMap<Integer, SubTask> showSubTaskList() {
        return subTaskList;
    }

    public HashMap<Integer, Epic> showEpicList() {
        return epicList;
    }

    public HashMap<Integer, SubTask> showSubTaskListByEpicId(int epicId) {
        HashMap<Integer, SubTask> newSubTaskList = new HashMap<>();
        for (int i = 1; i <= taskCounter; i++) {
            if (subTaskList.containsKey(i)) {
                if (subTaskList.get(i).getEpicId() == epicId) {
                    newSubTaskList.put(i, subTaskList.get(i));
                }
            }
        }
        return newSubTaskList;
    }

    public void clearTaskList() {
        HashMap<Integer, Task> newTaskList = new HashMap<>();
        taskList = newTaskList;
    }

    public void clearSubTaskList() {
        HashMap<Integer, SubTask> newSubTaskList = new HashMap<>();
        subTaskList = newSubTaskList;
    }

    public void clearEpicList() {
        HashMap<Integer, Epic> newEpicList = new HashMap<>();
        epicList = newEpicList;
    }

    public Task getTaskById(int id) {
        return taskList.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTaskList.get(id);
    }

    public Epic getEpicById(int id) {
        return epicList.get(id);
    }

    public void removeTaskById(int id) {
        taskList.remove(id);
    }

    public void removeSubTaskById(int id) {
        subTaskList.remove(id);
    }

    public void removeEpicById(int id) {
        epicList.remove(id);
        removeAllSubTasksByEpicId(id);
    }

    private void removeAllSubTasksByEpicId(int epicId) {
        for (int i = 1; i <= taskCounter; i++) {
            if (subTaskList.containsKey(i)) {
                if (subTaskList.get(i).getEpicId() == epicId) {
                    removeSubTaskById(i);
                }
            }
        }
    }

    public void updateTask(int id, Task task, Status status) {
        if (taskList.containsKey(id)) {
            task.setStatus(status);
            taskList.put(id, task);
        }
    }

    public void updateSubTask(int id, SubTask subTask, Status status) {
        if (subTaskList.containsKey(id)) {
            subTask.setStatus(status);
            subTaskList.put(id, subTask);
            updateEpicStatus(subTask.getEpicId());
        }
    }

    public void updateEpic(int id, Epic epic) {
        if (epicList.containsKey(id)) {
            epicList.put(id, epic);
            updateEpicStatus(id);
        }
    }

    private void updateEpicStatus(int id) {
        boolean isNew = true;
        boolean isDone = true;
        for (SubTask subTask : subTaskList.values()) {
            if (subTask.getEpicId() == id) {
                if (subTask.getStatus() != Status.NEW) {
                    isNew = false;
                }
                if (subTask.getStatus() != Status.DONE) {
                    isDone = false;
                }
            }
            if (!isNew && isDone) {
                epicList.get(id).setStatus(Status.DONE);
            }
            if (!isNew && !isDone) {
                epicList.get(id).setStatus(Status.IN_PROGRESS);
            }
        }
    }
}
