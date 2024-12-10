import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task taskWashCar = new Task("Помыть машину", "Съездить на автомойку", Status.NEW);
        taskManager.addTask(taskWashCar);
        Task taskFuelCar = new Task("Заправить машину", "доехать до заправки, заправиться",
                Status.IN_PROGRESS);
        taskManager.addTask(taskFuelCar);
        Epic epicMakeSupper = new Epic("Приготовить ужин", "Приготовить из купленных продуктов");
        taskManager.addEpic(epicMakeSupper);
        SubTask subtaskShopping = new SubTask("Сходить в магазин", "Купить продуктов для ужина",
                Status.NEW, epicMakeSupper.getId());
        taskManager.addSubTask(subtaskShopping);
        Epic epicRepairCar = new Epic("Починить машину", "Починить в автосервисе");
        taskManager.addEpic(epicRepairCar);
        SubTask subtaskFindCarService = new SubTask("Найти сервис", "Найти на картах СТО поблизости",
                Status.NEW, epicRepairCar.getId());
        taskManager.addSubTask(subtaskFindCarService);
        SubTask subtaskCallToService = new SubTask("Позвонить в сервис", "Договориться о ремонте",
                Status.NEW, epicRepairCar.getId());
        taskManager.addSubTask(subtaskCallToService);

        showTestLists(taskManager);

        taskFuelCar.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(taskFuelCar);
        subtaskShopping.setStatus(Status.DONE);
        taskManager.updateSubTask(subtaskShopping);
        subtaskFindCarService.setStatus(Status.DONE);
        taskManager.updateSubTask(subtaskFindCarService);

        System.out.println();
        System.out.println("Обновили статусы");
        showTestLists(taskManager);


        System.out.println();
        System.out.println("Удалили задачу и эпик");
        taskManager.removeTaskById(taskWashCar.getId());
        taskManager.removeEpicById(epicMakeSupper.getId());
        showTestLists(taskManager);

        taskManager.clearSubTaskList();
        System.out.println();
        System.out.println("Удалили лист сабтасков");
        showTestLists(taskManager);

    }

    static void showTestLists(TaskManager taskManager) {
        System.out.println("Список всех эпиков: ");
        System.out.println(taskManager.showEpicList());
        System.out.println();

        System.out.println("Список подзадач в эпике с id=3: ");
        System.out.println(taskManager.showSubTaskListByEpicId(3));
        System.out.println();

        System.out.println("Список подзадач в эпике с id=5: ");
        System.out.println(taskManager.showSubTaskListByEpicId(5));
        System.out.println();

        System.out.println("Список обычных задач: ");
        System.out.println(taskManager.showTaskList());
        System.out.println();

        System.out.println("Список сабтасков");
        System.out.println(taskManager.showSubTaskList());

    }
}
