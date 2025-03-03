package manager;

import exception.FileManagerCrossedTimeInTasksException;
import tasks.*;

import java.time.Duration;
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
        checkForCrossTime(taskToAdd);
        taskToAdd.setId(setTaskId());
        taskToAdd.setType(Type.TASK);
        taskList.put(taskToAdd.getId(), taskToAdd);
        addToPrioritizedTask(taskToAdd);
    }

    @Override
    public void addEpic(Epic epicToAdd) {
        epicToAdd.setId(setTaskId());
        epicToAdd.setType(Type.EPIC);
        epicList.put(epicToAdd.getId(), epicToAdd);
    }

    @Override
    public void addSubTask(SubTask subTaskToAdd) {
        int epicId = subTaskToAdd.getEpicId();
        if (epicList.containsKey(epicId)) {
            checkForCrossTime(subTaskToAdd);
            subTaskToAdd.setId(setTaskId());
            subTaskToAdd.setType(Type.SUBTASK);
            subTaskList.put(subTaskToAdd.getId(), subTaskToAdd);
            epicList.get(epicId).addSubTask(subTaskToAdd.getId());
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
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
        showTaskList().stream()
                .forEach(task -> prioritizedTasks.remove(task));
        taskList.keySet().stream()
                .forEach(id -> historyManager.remove(id));
        taskList.clear();
    }

    @Override
    public void clearSubTaskList() {
        showSubTaskList().stream()
                .forEach(subTask -> prioritizedTasks.remove(subTask));
        subTaskList.keySet().stream()
                .forEach(id -> historyManager.remove(id));
        subTaskList.clear();
        epicList.values().stream()
                .forEach(epic -> epic.clearSubTaskIds());
        epicList.keySet().stream()
                .forEach(id -> {
                    updateEpicStatus(id);
                    updateEpicTime(id);
                });
    }

    @Override
    public void clearEpicList() {
        subTaskList.keySet().stream()
                .forEach(id -> historyManager.remove(id));
        subTaskList.values().stream()
                .forEach(subTask -> prioritizedTasks.remove(subTask));
        subTaskList.clear();
        epicList.keySet().stream()
                .forEach(id -> historyManager.remove(id));
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
        Task taskToRemove = taskList.get(id);
        prioritizedTasks.remove(taskToRemove);
        historyManager.remove(id);
        taskList.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        if (subTaskList.containsKey(id)) {
            SubTask subTaskToRemove = subTaskList.get(id);
            int epicId = subTaskList.get(id).getEpicId();
            epicList.get(epicId).removeSubTask(id);
            prioritizedTasks.remove(subTaskToRemove);
            historyManager.remove(id);
            subTaskList.remove(id);
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
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
                SubTask subTaskToRemove = subTaskList.get(id);
                prioritizedTasks.remove(subTaskToRemove);
                historyManager.remove(id);
                subTaskList.remove(id);
            }
        }
    }

    @Override
    public void updateTask(Task task) {

        if (taskList.containsKey(task.getId())) {
            Task oldTask = taskList.get(task.getId());
            // Чтобы не проверять на персечение старую и новую версии задачу, удаляем старую из prioritizedTasks
            prioritizedTasks.remove(oldTask);
            try {
                checkForCrossTime(task);
                // Если ловим исключение на пересечение, то возвращаем старую версию обратно
            } catch (FileManagerCrossedTimeInTasksException e) {
                System.out.println("Неудалось обновить задачу");
                System.out.println(e.getMessage());
                addToPrioritizedTask(oldTask);
            }
            taskList.replace(task.getId(), task);
            addToPrioritizedTask(task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTaskList.containsKey(subTask.getId())) {
            if (subTaskList.get(subTask.getId()).getEpicId() == subTask.getEpicId()) {
                SubTask oldSubTask = subTaskList.get(subTask.getId());
                prioritizedTasks.remove(oldSubTask);
                try {
                    checkForCrossTime(subTask);
                } catch (FileManagerCrossedTimeInTasksException e) {
                    System.out.println("Неудалось обновить задачу");
                    System.out.println(e.getMessage());
                    addToPrioritizedTask(oldSubTask);
                }
                subTaskList.replace(subTask.getId(), subTask);
                addToPrioritizedTask(subTask);
                updateEpicStatus(subTask.getEpicId());
                if (subTask.getStartTime() != null) {
                    updateEpicTime(subTask.getEpicId());
                }
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
        if (showSubTaskList().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
        } else {
            LocalDateTime epicStartTime = showSubTaskListByEpicId(id).stream()
                    .map(Task::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElseGet(() -> null);

            LocalDateTime epicEndTime = showSubTaskListByEpicId(id)
                    .stream()
                    .map(Task::getEndTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElseGet(() -> null);
            Duration epicDuration = showSubTaskListByEpicId(id).stream()
                    .map(Task::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration::plus)
                    .orElseGet(() -> null);
            epic.setStartTime(epicStartTime);
            epic.setEndTime(epicEndTime);
            epic.setDuration(epicDuration);
        }
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

    private boolean isCrossed(Task task1, Task task2) {
        return task1.getEndTime().isAfter(task2.getStartTime());
    }

    private void checkForCrossTime(Task taskToAdd) {
        if (taskToAdd.getStartTime() != null) {
            getPrioritizedTasks().stream()
                    .filter(task -> isCrossed(taskToAdd, task))
                    .findAny()
                    .ifPresent(task -> {
                        throw new FileManagerCrossedTimeInTasksException("Задача не может быть добавлена из-за" +
                                " пересечения времени");
                    });
        }
    }
}