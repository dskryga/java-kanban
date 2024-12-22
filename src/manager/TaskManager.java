package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    ArrayList<Task> showTaskList();

    ArrayList<SubTask> showSubTaskList();

    ArrayList<Epic> showEpicList();

    ArrayList<SubTask> showSubTaskListByEpicId(int epicId);

    void clearTaskList();

    void clearSubTaskList();

    void clearEpicList();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void removeTaskById(int id);

    void removeSubTaskById(int id);

    void removeEpicById(int id);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    ArrayList<Task> getHistory();
}
