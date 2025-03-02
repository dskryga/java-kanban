package manager;

import exception.FileManagerCrossedTimeInTasksException;
import tasks.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int taskCounter;
    private Map<Integer, Task> taskList;
    private Map<Integer, SubTask> subTaskList;
    private Map<Integer, Epic> epicList;
    private HistoryManager historyManager;
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


    public InMemoryTaskManager() {
        taskCounter = 0;
        taskList = new HashMap<>();
        subTaskList = new HashMap<>();
        epicList = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    private int setTaskId() {
        taskCounter++;
        return taskCounter;
    }

    @Override
    public void addTask(Task taskToAdd) {

        if (taskToAdd.getStartTime() != null) {
            showTaskList().stream()
                    .filter(task -> isCrossed(taskToAdd, task))
                    .findAny()
                    .ifPresent(task -> {
                        throw new FileManagerCrossedTimeInTasksException("Задача не может быть добавлена из-за" +
                                " пересечения времени");
                    });
        }
        taskToAdd.setId(setTaskId());
        taskToAdd.setType(Type.TASK);
        taskList.put(taskToAdd.getId(), taskToAdd);
        addToPrioritizedTask(taskToAdd);
    }

    @Override
    public void addEpic(Epic epicToAdd) {

        if (epicToAdd.getStartTime() != null) {
            showEpicList().stream()
                    .filter(epic -> isCrossed(epicToAdd, epic))
                    .findAny()
                    .ifPresent(epic -> {
                        throw new FileManagerCrossedTimeInTasksException("Задача не может быть добавлена из-за" +
                                " пересечения времени");
                    });
        }
        epicToAdd.setId(setTaskId());
        epicToAdd.setType(Type.EPIC);
        epicList.put(epicToAdd.getId(), epicToAdd);
        addToPrioritizedTask(epicToAdd);
    }

    @Override
    public void addSubTask(SubTask subTaskToAdd) {
        if (epicList.containsKey(subTaskToAdd.getEpicId())) {
            if (subTaskToAdd.getStartTime() != null) {
                showSubTaskList().stream()
                        .filter(subTask -> isCrossed(subTaskToAdd, subTask))
                        .findAny()
                        .ifPresent(subTask -> {
                            throw new FileManagerCrossedTimeInTasksException("Задача не может быть добавлена из-за" +
                                    " пересечения времени");
                        });
            }
            subTaskToAdd.setId(setTaskId());
            subTaskToAdd.setType(Type.SUBTASK);
            subTaskList.put(subTaskToAdd.getId(), subTaskToAdd);
            epicList.get(subTaskToAdd.getEpicId()).addSubTask(subTaskToAdd.getId());
            updateEpicStatus(subTaskToAdd.getEpicId());
            addToPrioritizedTask(subTaskToAdd);
        }
    }

    @Override
    public ArrayList<Task> showTaskList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(taskList.values());
        return tasks;
    }

    @Override
    public ArrayList<SubTask> showSubTaskList() {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        subTasks.addAll(subTaskList.values());
        return subTasks;
    }

    @Override
    public ArrayList<Epic> showEpicList() {
        ArrayList<Epic> epics = new ArrayList<>();
        epics.addAll(epicList.values());
        return epics;
    }

    @Override
    public ArrayList<SubTask> showSubTaskListByEpicId(int epicId) {
//        ArrayList<SubTask> subTasks = new ArrayList<>();
//        if (epicList.containsKey(epicId)) {
//            for (Integer i : epicList.get(epicId).getSubTaskIds()) {
//                subTasks.add(subTaskList.get(i));
//            }
//        }
//        return subTasks;
        ArrayList<SubTask> subTasks = new ArrayList<>();

        if (epicList.containsKey(epicId)) {
            Epic epic = epicList.get(epicId);
            subTasks.addAll(epic.getSubTaskIds().stream()
                    .map(integer -> subTaskList.get(integer))
                    .toList());
        }
        return subTasks;
    }

    @Override
    public void clearTaskList() {
        for (Integer id : taskList.keySet()) {
            historyManager.remove(id);
        }
        taskList.clear();
    }

    @Override
    public void clearSubTaskList() {
        for (Integer id : subTaskList.keySet()) {
            historyManager.remove(id);
        }
        subTaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.clearSubTaskIds();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void clearEpicList() {
        for (Integer id : subTaskList.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : epicList.keySet()) {
            historyManager.remove(id);
        }
        subTaskList.clear();
        epicList.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (taskList.containsKey(id)) {
            historyManager.addToHistory(taskList.get(id));
            return taskList.get(id);
        } else {
            return null;
        }

    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTaskList.containsKey(id)) {
            historyManager.addToHistory(subTaskList.get(id));
            return subTaskList.get(id);
        } else {
            return null;
        }

    }

    @Override
    public Epic getEpicById(int id) {
        if (epicList.containsKey(id)) {
            historyManager.addToHistory(epicList.get(id));
            return epicList.get(id);
        } else {
            return null;
        }

    }

    @Override
    public void removeTaskById(int id) {
        taskList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        if (subTaskList.containsKey(id)) {
            Integer epicId = subTaskList.get(id).getEpicId();
            epicList.get(epicId).removeSubTask(id);
            subTaskList.remove(id);
            historyManager.remove(id);
            updateEpicStatus(epicId);
        }
    }

    @Override
    public void removeEpicById(int id) {
        removeAllSubTasksByEpicId(id);
        epicList.remove(id);
        historyManager.remove(id);
    }

    private void removeAllSubTasksByEpicId(int epicId) {
        if (epicList.containsKey(epicId)) {
            ArrayList<Integer> subTasksIds = epicList.get(epicId).getSubTaskIds();
            for (Integer id : subTasksIds) {
                subTaskList.remove(id);
                historyManager.remove(id);
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.replace(task.getId(), task);
            updatePrioritizedTask();
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTaskList.containsKey(subTask.getId())) {
            if (subTaskList.get(subTask.getId()).getEpicId() == subTask.getEpicId()) {
                subTaskList.replace(subTask.getId(), subTask);
                updateEpicStatus(subTask.getEpicId());
                if (subTask.getStartTime() != null) {
                    updateEpicTime(subTask.getEpicId());
                }
                updatePrioritizedTask();
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            epicList.get(epic.getId()).setName(epic.getName());
            epicList.get(epic.getId()).setDescription(epic.getDescription());
        }
    }

    private void updateEpicStatus(int id) {
        boolean isNew = true;
        boolean isDone = true;
        Epic epic = epicList.get(id);
        if (epic.getSubTaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Integer i : epic.getSubTaskIds()) {
            if (subTaskList.get(i).getStatus() != Status.NEW) {
                isNew = false;
            }
            if (subTaskList.get(i).getStatus() != Status.DONE) {
                isDone = false;
            }
        }
        if (!isNew && isDone) {
            epic.setStatus(Status.DONE);
        }
        if (!isNew && !isDone) {
            epic.setStatus(Status.IN_PROGRESS);
        }
        if (isNew && !isDone) {
            epic.setStatus(Status.NEW);
        }
    }

    private void updateEpicTime(int id) {
        Epic epic = epicList.get(id);
        LocalDateTime epicStartTime = showSubTaskListByEpicId(id).stream()
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo)
                .get();

        LocalDateTime epicEndTime = showSubTaskListByEpicId(id)
                .stream()
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo)
                .get();

        epic.setStartTime(epicStartTime);
        epic.setEndTime(epicEndTime);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void setTaskCounter(Integer newId) {
        taskCounter = newId;
    }

    protected int getTaskCounter() {
        return taskCounter;
    }

    protected void loadTask(Task task) {
        taskList.put(task.getId(), task);
    }

    protected void loadSubTask(SubTask subTask) {
        if (epicList.containsKey(subTask.getEpicId())) {
            subTaskList.put(subTask.getId(), subTask);
            epicList.get(subTask.getEpicId()).addSubTask(subTask.getId());
            updateEpicStatus(subTask.getEpicId());
        }
    }

    protected void loadEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private void addToPrioritizedTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void updatePrioritizedTask() {
        TreeSet<Task> set = new TreeSet<>(prioritizedTasks);
        prioritizedTasks.clear();
        set.stream().forEach(task -> prioritizedTasks.add(task));
    }

    private boolean isCrossed(Task task1, Task task2) {
        return task1.getEndTime().isAfter(task2.getStartTime());
    }
}