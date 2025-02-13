import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        /*
        Опциональный пользовательский сценарий
         */

        FileBackedTaskManager taskManager = Managers.loadFileBackedTaskManager(new File("data.csv"));
        System.out.println(taskManager.showTaskList());

        Epic epic = new Epic("lolol","kekeke");
        taskManager.addEpic(epic);
        System.out.println(taskManager.showEpicList());



        // Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.

//        Task taskWashCar = new Task("Помыть машину", "Съездить на автомойку", Status.NEW);
//        taskManager.addTask(taskWashCar);
//        Task taskFuelCar = new Task("Заправить машину", "доехать до заправки, заправиться",
//                Status.IN_PROGRESS);
//        taskManager.addTask(taskFuelCar);
//
//        Epic epicRepairCar = new Epic("Починить машину", "Починить в автосервисе");
//        taskManager.addEpic(epicRepairCar);
//        SubTask subtaskFindCarService = new SubTask("Найти сервис", "Найти на картах СТО поблизости",
//                Status.NEW, epicRepairCar.getId());
//        taskManager.addSubTask(subtaskFindCarService);
//        SubTask subtaskCallToService = new SubTask("Позвонить в сервис", "Договориться о ремонте",
//                Status.NEW, epicRepairCar.getId());
//        taskManager.addSubTask(subtaskCallToService);
//        SubTask subTaskGetToService = new SubTask("Добраться до сервиса", "Доехать на эвакуаторе до СТО",
//                Status.NEW, epicRepairCar.getId());
//        taskManager.addSubTask(subTaskGetToService);



        //Запросите созданные задачи несколько раз в разном порядке.

//        System.out.println("Запрашиваем задачи и смотрим историю");
//        System.out.println(taskManager.getTaskById(2));
//        System.out.println(taskManager.getTaskById(1));
//        System.out.println(taskManager.getSubTaskById(5));
//        System.out.println(taskManager.getEpicById(3));
//        System.out.println(taskManager.getSubTaskById(6));
//        System.out.println(taskManager.getSubTaskById(4));
//        System.out.println(taskManager.getTaskById(1));
//
//        //Выведите историю, убедитесь, что в ней нет повторов
//
//        System.out.println(taskManager.getHistory());
//
//        //Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
//        System.out.println();
//        System.out.println("Смотрим историю после удаления задачи");
//        taskManager.removeTaskById(1);
//        System.out.println(taskManager.getHistory());
//
//        //Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи
//        System.out.println();
//        System.out.println("Смотрим историю после удаления эпика");
//        taskManager.removeEpicById(3);
//        System.out.println(taskManager.getHistory());
    }
}
