package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager tm;

    @BeforeEach
    void createTaskManager() {
        tm = Managers.getDefault();
    }

    @Test
    void addTask() {
        String name = "Сходить за покупками";
        String description = "Купить продукты";
        Status status = Status.IN_PROGRESS;
        Task createdTask = new Task(name, description, status);

        tm.addTask(createdTask);
        Task actualTask = tm.getTaskById(createdTask.getId());

        Assertions.assertEquals(name, actualTask.getName());
        Assertions.assertEquals(description, actualTask.getDescription());
        Assertions.assertEquals(status, actualTask.getStatus());
        Assertions.assertNotNull(actualTask.getId());

    }

    @Test
    void addEpic() {
        String name = "Выпить кофе";
        String description = "Купить кофе в кофейне";
        Epic createdEpic = new Epic(name, description);

        tm.addEpic(createdEpic);
        Epic actualEpic = tm.getEpicById(createdEpic.getId());

        Assertions.assertNotNull(actualEpic.getId());
        Assertions.assertEquals(name, actualEpic.getName());
        Assertions.assertEquals(description, actualEpic.getDescription());
    }

    @Test
    void addSubTask() {
        String nameEpic = "Выпить кофе";
        String descriptionEpic = "Купить кофе в кофейне";
        Epic createdEpic = new Epic(nameEpic, descriptionEpic);
        String nameSubTask = "Отправиться в кофейню";
        String descriptionSubtask = "Поехать на такси";
        Status status = Status.DONE;

        tm.addEpic(createdEpic);
        SubTask createdSubtask = new SubTask(nameSubTask, descriptionSubtask, status, createdEpic.getId());
        tm.addSubTask(createdSubtask);
        SubTask actualSubtask = tm.getSubTaskById(createdSubtask.getId());

        Assertions.assertNotNull(actualSubtask.getId());
        Assertions.assertEquals(actualSubtask.getEpicId(), createdEpic.getId());
        Assertions.assertEquals(nameSubTask, actualSubtask.getName());
        Assertions.assertEquals(descriptionSubtask, actualSubtask.getDescription());
        Assertions.assertEquals(status, actualSubtask.getStatus());
        Assertions.assertEquals(status, createdEpic.getStatus());
    }

    @Test
    void showTaskList() {
        Task task1 = new Task("A", "AA", Status.NEW);
        Task task2 = new Task("B", "BB", Status.IN_PROGRESS);
        Task task3 = new Task("C", "CC", Status.DONE);
        tm.addTask(task1);
        tm.addTask(task2);
        tm.addTask(task3);

        List<Task> taskList = tm.showTaskList();
        Assertions.assertEquals(task1, taskList.get(0));
        Assertions.assertEquals(task2, taskList.get(1));
        Assertions.assertEquals(task3, taskList.get(2));
    }

    @Test
    void showSubTaskList() {
        Epic epic = new Epic("Name", "Description");
        tm.addEpic(epic);
        SubTask subTask1 = new SubTask("A", "AA", Status.DONE, epic.getId());
        SubTask subTask2 = new SubTask("BB", "BB", Status.DONE, epic.getId());
        SubTask subTask3 = new SubTask("C", "CC", Status.DONE, epic.getId());
        tm.addSubTask(subTask1);
        tm.addSubTask(subTask2);
        tm.addSubTask(subTask3);

        List<SubTask> subtaskList = tm.showSubTaskList();
        Assertions.assertEquals(subTask1, subtaskList.get(0));
        Assertions.assertEquals(subTask2, subtaskList.get(1));
        Assertions.assertEquals(subTask3, subtaskList.get(2));
    }

    @Test
    void showEpicList() {
        Epic epic1 = new Epic("A", "AA");
        Epic epic2 = new Epic("B", "BB");
        Epic epic3 = new Epic("C", "CC");
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        tm.addEpic(epic3);

        List<Epic> epicList = tm.showEpicList();

        Assertions.assertEquals(epic1, epicList.get(0));
        Assertions.assertEquals(epic2, epicList.get(1));
        Assertions.assertEquals(epic3, epicList.get(2));
    }

    @Test
    void showSubTaskListByEpicId() {
        Epic epic = new Epic("Name", "Description");
        tm.addEpic(epic);
        SubTask subTask1 = new SubTask("A", "AA", Status.DONE, epic.getId());
        SubTask subTask2 = new SubTask("BB", "BB", Status.DONE, epic.getId());
        SubTask subTask3 = new SubTask("C", "CC", Status.DONE, epic.getId());
        tm.addSubTask(subTask1);
        tm.addSubTask(subTask2);
        tm.addSubTask(subTask3);

        List<SubTask> subtaskList = tm.showSubTaskListByEpicId(epic.getId());

        Assertions.assertEquals(subTask1, subtaskList.get(0));
        Assertions.assertEquals(subTask2, subtaskList.get(1));
        Assertions.assertEquals(subTask3, subtaskList.get(2));
    }

    @Test
    void clearTaskList() {
        Task task1 = new Task("A", "AA", Status.DONE);
        Task task2 = new Task("B", "BB", Status.NEW);
        tm.addTask(task1);
        tm.addTask(task2);

        tm.clearTaskList();

        Assertions.assertTrue(tm.showTaskList().isEmpty());
    }

    @Test
    void clearSubTaskList() {
        Epic epic = new Epic("name", "description");
        tm.addEpic(epic);
        SubTask subTask1 = new SubTask("A", "AA", Status.NEW, epic.getId());
        SubTask subTask2 = new SubTask("B", "BB", Status.NEW, epic.getId());
        tm.addSubTask(subTask1);
        tm.addSubTask(subTask2);

        tm.clearSubTaskList();

        Assertions.assertTrue(tm.showSubTaskList().isEmpty());
    }

    @Test
    void clearEpicList() {
        Epic epic1 = new Epic("A", "AA");
        Epic epic2 = new Epic("B", "BB");
        tm.addEpic(epic1);
        tm.addEpic(epic2);

        tm.clearEpicList();

        Assertions.assertTrue(tm.showEpicList().isEmpty());
    }

    @Test
    void getTaskById() {
        String name = "A";
        String description = "AA";
        Status status = Status.IN_PROGRESS;
        Task createdTask = new Task(name, description, status);
        tm.addTask(createdTask);

        Task actualTask = tm.getTaskById(createdTask.getId());
        Assertions.assertEquals(name, actualTask.getName());
        Assertions.assertEquals(description, actualTask.getDescription());
        Assertions.assertEquals(createdTask.getId(), actualTask.getId());
        Assertions.assertEquals(status, actualTask.getStatus());
        actualTask = tm.getHistory().get(0);
        Assertions.assertEquals(name, actualTask.getName());
        Assertions.assertEquals(description, actualTask.getDescription());
        Assertions.assertEquals(status, actualTask.getStatus());
    }

    @Test
    void getSubTaskById() {
        Epic epic = new Epic("EpicN", "EpicD");
        tm.addEpic(epic);
        String name = "A";
        String description = "AA";
        Status status = Status.IN_PROGRESS;
        SubTask createdSubTask = new SubTask(name, description, status, epic.getId());
        tm.addSubTask(createdSubTask);

        SubTask actualSubtask = tm.getSubTaskById(createdSubTask.getId());

        Assertions.assertEquals(name, actualSubtask.getName());
        Assertions.assertEquals(description, actualSubtask.getDescription());
        Assertions.assertEquals(status, actualSubtask.getStatus());
        Assertions.assertEquals(epic.getId(), actualSubtask.getEpicId());
        actualSubtask = (SubTask) tm.getHistory().get(0);
        Assertions.assertEquals(name, actualSubtask.getName());
        Assertions.assertEquals(description, actualSubtask.getDescription());
        Assertions.assertEquals(status, actualSubtask.getStatus());
        Assertions.assertEquals(epic.getId(), actualSubtask.getEpicId());
    }

    @Test
    void getEpicById() {
        String name = "A";
        String description = "AA";
        Epic createdEpic = new Epic(name, description);
        tm.addEpic(createdEpic);

        Epic actualEpic = tm.getEpicById(createdEpic.getId());

        Assertions.assertEquals(name, actualEpic.getName());
        Assertions.assertEquals(description, actualEpic.getDescription());
        actualEpic = (Epic) tm.getHistory().get(0);
        Assertions.assertEquals(name, actualEpic.getName());
        Assertions.assertEquals(description, actualEpic.getDescription());
    }

    @Test
    void removeTaskById() {
        Task task = new Task("A", "AA", Status.NEW);
        tm.addTask(task);
        int id = task.getId();

        tm.removeTaskById(id);

        Assertions.assertNull(tm.getTaskById(id));
    }

    @Test
    void removeSubTaskById() {
        Epic epic = new Epic("A", "AA");
        tm.addEpic(epic);
        SubTask subTask = new SubTask("B", "BB", Status.NEW, epic.getId());
        tm.addSubTask(subTask);
        int id = subTask.getId();

        tm.removeSubTaskById(id);

        Assertions.assertNull(tm.getSubTaskById(id));
        Assertions.assertTrue(tm.showSubTaskListByEpicId(epic.getId()).isEmpty());
    }

    @Test
    void removeEpicById() {
        Epic epic = new Epic("A", "AA");
        tm.addEpic(epic);
        int id = epic.getId();

        tm.removeEpicById(id);

        Assertions.assertNull(tm.getEpicById(id));
    }

    @Test
    void updateTask() {
        String oldName = "A";
        String newName = "BB";
        String oldDescription = "AA";
        String newDescription = "BB";
        Status oldStatus = Status.NEW;
        Status newStatus = Status.IN_PROGRESS;
        Task createdTask = new Task(oldName, oldDescription, oldStatus);
        tm.addTask(createdTask);

        createdTask.setStatus(newStatus);
        createdTask.setName(newName);
        createdTask.setDescription(newDescription);
        tm.updateTask(createdTask);
        Task updatedTask = tm.getTaskById(createdTask.getId());

        Assertions.assertEquals(newStatus, updatedTask.getStatus());
        Assertions.assertEquals(newName, updatedTask.getName());
        Assertions.assertEquals(newDescription, updatedTask.getDescription());
    }

    @Test
    void updateSubTask() {
        Epic epic = new Epic("A", "AA");
        tm.addEpic(epic);
        String oldName = "B";
        String newName = "C";
        String oldDescription = "BB";
        String newDescription = "CC";
        Status oldStatus = Status.NEW;
        Status newStatus = Status.IN_PROGRESS;
        SubTask subTask = new SubTask(oldName, oldDescription, oldStatus, epic.getId());
        tm.addSubTask(subTask);

        subTask.setStatus(newStatus);
        subTask.setName(newName);
        subTask.setDescription(newDescription);
        tm.updateSubTask(subTask);
        SubTask updatedSubtask = tm.getSubTaskById(subTask.getId());

        Assertions.assertEquals(newStatus, updatedSubtask.getStatus());
        Assertions.assertEquals(newName, updatedSubtask.getName());
        Assertions.assertEquals(newDescription, updatedSubtask.getDescription());
        Assertions.assertEquals(newStatus, epic.getStatus());
    }

    @Test
    void updateEpic() {
        String oldName = "A";
        String newName = "B";
        String oldDescription = "AA";
        String newDescription = "BB";
        Epic createdEpic = new Epic(oldName, oldDescription);
        tm.addEpic(createdEpic);

        createdEpic.setName(newName);
        createdEpic.setDescription(newDescription);
        tm.updateEpic(createdEpic);
        Epic updatedEpic = tm.getEpicById(createdEpic.getId());

        Assertions.assertEquals(newName, updatedEpic.getName());
        Assertions.assertEquals(newDescription, updatedEpic.getDescription());
    }

    @Test
    void getHistory() {
        Task task = new Task("A", "AA", Status.IN_PROGRESS);
        Epic epic = new Epic("B", "BB");
        tm.addTask(task);
        tm.addEpic(epic);
        SubTask subTask = new SubTask("C", "CC", Status.NEW, epic.getId());
        tm.addSubTask(subTask);
        tm.getTaskById(task.getId());
        tm.getEpicById(epic.getId());
        tm.getSubTaskById(subTask.getId());

        List<Task> list = tm.getHistory();
        Task taskInHistory = list.get(0);
        Epic epicInHistory = (Epic) list.get(1);
        SubTask subtaskInHistory = (SubTask) list.get(2);

        Assertions.assertEquals(task.getName(),taskInHistory.getName());
        Assertions.assertEquals(task.getDescription(), taskInHistory.getDescription());
        Assertions.assertEquals(task.getStatus(), taskInHistory.getStatus());
        Assertions.assertEquals(epic.getName(),epicInHistory.getName());
        Assertions.assertEquals(epic.getDescription(),epicInHistory.getDescription());
        Assertions.assertEquals(subTask.getName(),subtaskInHistory.getName());
        Assertions.assertEquals(subTask.getDescription(),subtaskInHistory.getDescription());
        Assertions.assertEquals(subTask.getStatus(),subtaskInHistory.getStatus());
    }

    @Test
    void tasksAreEqualsIfIdsAreEquals() {
        Task task1 = new Task("A","AA",Status.NEW);
        tm.addTask(task1);
        Task task2 = tm.getTaskById(task1.getId());

        Assertions.assertEquals(task1,task2);
    }

    @Test
    void epicsAreEqualsIfIdsAreEquals() {
        Epic epic1 = new Epic("A","AA");
        tm.addEpic(epic1);
        Epic epic2 = tm.getEpicById(epic1.getId());

        Assertions.assertEquals(epic1,epic2);
    }

    @Test
    void subtasksAreEqualsIfIdsAreEquals() {
        Epic epic = new Epic("A","AA");
        tm.addEpic(epic);
        SubTask subTask1 = new SubTask("B","BB",Status.NEW,epic.getId());
        tm.addSubTask(subTask1);
        SubTask subTask2 = tm.getSubTaskById(subTask1.getId());

        Assertions.assertEquals(subTask1,subTask2);
    }




}