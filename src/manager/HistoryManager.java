package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void addToHistory(Task task);

    List<Task> getHistory();

}
