package manager;

import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import tasks.Epic;

import java.util.ArrayList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> historyList = new ArrayList<>();
    private static final int MAX_TASKS_IN_HISTORY = 10;

    @Override
    public List<Task> getHistory() {

        return new ArrayList<>(historyList);
    }

    @Override
    public void addToHistory(Task task) {


        if (historyList.size() >= MAX_TASKS_IN_HISTORY) {
            historyList.remove(0);
        }
        historyList.add(task);

    }


}
