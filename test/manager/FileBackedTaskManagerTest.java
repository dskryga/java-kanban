package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.File;


public class FileBackedTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    void init() {
        tm = getTaskManager();
    }

    @Override
    TaskManager getTaskManager() {
        return new FileBackedTaskManager(new File("test/manager/data.csv"));
    }

    @Test
    void saveAndLoad() {
        Task taskWashCar = new Task("Помыть машину", "Съездить на автомойку", Status.NEW);
        tm.addTask(taskWashCar);
        Epic epicRepairCar = new Epic("Починить машину", "Починить в автосервисе");
        tm.addEpic(epicRepairCar);
        SubTask subtaskFindCarService = new SubTask("Найти сервис", "Найти на картах СТО поблизости",
                Status.NEW, epicRepairCar.getId());
        tm.addSubTask(subtaskFindCarService);
        Task savedTask = tm.getTaskById(1);
        SubTask savedSubTask = tm.getSubTaskById(3);
        Epic savedEpic = tm.getEpicById(2);

        tm = Managers.loadFileBackedTaskManager(new File("test/manager/data.csv"));
        Task loadedTask = tm.getTaskById(1);
        SubTask loadedSubTask = tm.getSubTaskById(3);
        Epic loadedEpic = tm.getEpicById(2);

        Assertions.assertEquals(savedTask, loadedTask);
        Assertions.assertEquals(savedSubTask, loadedSubTask);
        Assertions.assertEquals(savedEpic, loadedEpic);
    }
}
