package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    public void addToHistory(Task task);
    public void addToHistory(SubTask subTask);
    public void addToHistory(Epic epic);
    public ArrayList<Task> getHistory();

}
