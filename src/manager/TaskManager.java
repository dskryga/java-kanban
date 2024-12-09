package manager;

import tasks.*;

import java.util.ArrayList;
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
        task.setId(setTaskId());
        taskList.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(setTaskId());
        epicList.put(epic.getId(), epic);
    }

    public void addSubTask(SubTask subTask) {
        if (epicList.containsKey(subTask.getEpicId())) {
            subTask.setId(setTaskId());
            subTaskList.put(subTask.getId(), subTask);
            epicList.get(subTask.getEpicId()).addSubTask(subTask.getId());
            updateEpicStatus(subTask.getEpicId());
        }
    }

    public ArrayList<Task> showTaskList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(taskList.values());
        return tasks;
    }

    public ArrayList<SubTask> showSubTaskList() {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        subTasks.addAll(subTaskList.values());
        return subTasks;
    }

    public ArrayList<Epic> showEpicList() {
        ArrayList<Epic> epics = new ArrayList<>();
        epics.addAll(epicList.values());
        return epics;
    }

    public ArrayList<SubTask> showSubTaskListByEpicId(int epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        if (epicList.containsKey(epicId)) {
            for (Integer i : epicList.get(epicId).getSubTaskIds()) {
                subTasks.add(subTaskList.get(i));
            }
        }
        return subTasks;
    }

    public void clearTaskList() {
        taskList.clear();
    }

    public void clearSubTaskList() {
        subTaskList.clear();
        for (Integer i : epicList.keySet()) {
            epicList.get(i).clearSubTaskIds();
            updateEpicStatus(i);
        }
    }

    public void clearEpicList() {
        clearSubTaskList();
        epicList.clear();
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
        Integer epicId = subTaskList.get(id).getEpicId();
        epicList.get(epicId).removeSubTask(id);
        subTaskList.remove(id);
    }

    public void removeEpicById(int id) {
        removeAllSubTasksByEpicId(id);
        epicList.remove(id);
    }

    private void removeAllSubTasksByEpicId(int epicId) {
        ArrayList<Integer> subTasksIds = epicList.get(epicId).getSubTaskIds();
        for (Integer id : subTasksIds) {
            subTaskList.remove(id);
        }
        epicList.get(epicId).clearSubTaskIds();
    }

    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.replace(task.getId(), task);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTaskList.containsKey(subTask.getId())) {
            if (subTaskList.get(subTask.getId()).getEpicId() == subTask.getEpicId()) {
                subTaskList.replace(subTask.getId(), subTask);
                updateEpicStatus(subTask.getEpicId());
            }
        }
    }

    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            epicList.get(epic.getId()).setName(epic.getName());
            epicList.get(epic.getId()).setDescription(epic.getDescription());
        }
    }

    private void updateEpicStatus(int id) {
        boolean isNew = true;
        boolean isDone = true;

        if (epicList.get(id).getSubTaskIds().isEmpty()) {
            epicList.get(id).setStatus(Status.NEW);
            return;
        }

        for (Integer i : epicList.get(id).getSubTaskIds()) {
            if (subTaskList.get(i).getStatus() != Status.NEW) {
                isNew = false;
            }
            if (subTaskList.get(i).getStatus() != Status.DONE) {
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
