import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        taskManager.addTask(new Task("Сходить в магазин", "купить продукты для ужина"));
        taskManager.addTask(new Task("Приготовить ужин", "использовать купленные продукты"));
        taskManager.addEpic(new Epic("Убраться", "Убраться в спальне"));
        taskManager.addSubTask(new SubTask("Пропылесосить", " пропылесосить ковер", 3));
        taskManager.addSubTask(new SubTask("Помыть полы", " протереть ламинат", 3));
        taskManager.addEpic(new Epic("Найти фильм на вечер", " поискать триллер или детектив на вечер"));
        taskManager.addSubTask(new SubTask("Зайти на кинопоиск", "посмотреть новинки", 6));

        showTestLists(taskManager);

        taskManager.updateTask(1, new Task("Заказать продукты",
                "воспользоваться промокодом для Яндекс Еды"), Status.IN_PROGRESS);
        taskManager.updateSubTask(4, new SubTask("Запустить робот-пылесос",
                "не забыть зарядить его", 3), Status.DONE);
        taskManager.updateEpic(6, new Epic("Найти сериал на ближайшие вечера", "поискать ситком"));

        System.out.println();
        System.out.println("Выполнили обновление:");
        showTestLists(taskManager);

        taskManager.removeTaskById(1);
        taskManager.removeEpicById(6);
        taskManager.removeSubTaskById(5);

        System.out.println();
        System.out.println("Выполнили удаление:");
        showTestLists(taskManager);
    }

    static void showTestLists(TaskManager taskManager) {
        System.out.println("Список всех эпиков: ");
        System.out.println(taskManager.showEpicList());
        System.out.println();

        System.out.println("Список подзадач в эпике с id=3: ");
        System.out.println(taskManager.showSubTaskListByEpicId(3));
        System.out.println();

        System.out.println("Список подзадач в эпике с id=6: ");
        System.out.println(taskManager.showSubTaskListByEpicId(6));
        System.out.println();

        System.out.println("Список обычных задач: ");
        System.out.println(taskManager.showTaskList());

    }
}
