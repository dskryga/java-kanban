package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Epic addEpic(Epic epic);

    SubTask addSubTask(SubTask subTask);

    List<Task> showTaskList();

    List<SubTask> showSubTaskList();

    List<Epic> showEpicList();

    List<SubTask> showSubTaskListByEpicId(int epicId);

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

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
