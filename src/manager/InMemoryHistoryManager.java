package manager;

import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import tasks.Epic;

import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyListToReturn = new ArrayList<>(historyList);
        return historyListToReturn;
    }

    @Override
    public void addToHistory(Task task) {
        String name = task.getName();
        String description = task.getDescription();
        Status status = task.getStatus();
        Task taskToHistory = new Task(name, description, status);

        if (historyList.size() > 9) {
            historyList.remove(0);
        }
        historyList.add(taskToHistory);

    }

    @Override
    public void addToHistory(SubTask subTask) {

        String name = subTask.getName();
        String description = subTask.getDescription();
        Status status = subTask.getStatus();
        int epicId = subTask.getEpicId();
        SubTask subtaskToHistory = new SubTask(name, description, status, epicId);
        if (historyList.size() > 9) {
            historyList.remove(0);
        }
        historyList.add(subtaskToHistory);
    }

    @Override
    public void addToHistory(Epic epic) {

        String name = epic.getName();
        ;
        String description = epic.getDescription();

        Epic epicToHistory = new Epic(name, description);
        if (historyList.size() > 9) {
            historyList.remove(0);
        }
        historyList.add(epicToHistory);
    }
}
