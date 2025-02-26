package manager;

import exception.FileManagerFileInitilizationException;
import exception.FileManagerSaveException;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File data;

    public FileBackedTaskManager(File file) {
        super();
        this.data = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void clearTaskList() {
        super.clearTaskList();
        save();
    }

    @Override
    public void clearSubTaskList() {
        super.clearSubTaskList();
        save();
    }

    @Override
    public void clearEpicList() {
        super.clearEpicList();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    private void save() {
        List<String> allItems = new ArrayList<>();
        allItems.add("id,type,name,status,description,epic");
        for (Task task : showTaskList()) {
            String taskAsString = taskToString(task);
            allItems.add(taskAsString);
        }
        for (Epic epic : showEpicList()) {
            String epicAsString = taskToString(epic);
            allItems.add(epicAsString);
        }
        for (SubTask subTask : showSubTaskList()) {
            String subtasksAsString = taskToString(subTask);
            allItems.add(subtasksAsString);
        }
        writeStringToFile(allItems);
    }

    private void writeStringToFile(List<String> allItem) {
        try (FileWriter fileWriter = new FileWriter(data)) {
            for (String item : allItem) {
                fileWriter.write(item + "\n");
            }
        } catch (IOException ex) {
            String errorMessage = "Ошибка при записи в файл: " + ex.getMessage();
            System.out.println(errorMessage);
            throw new FileManagerSaveException(errorMessage);
        }
    }

    private String taskToString(Task task) {
        String taskAsString = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + ",";
        if (task.getType() == Type.SUBTASK) {
            SubTask subtask = (SubTask) task;
            taskAsString += subtask.getEpicId();
        }
        return taskAsString;
    }

    private Task stringToTask(String str) throws IOException {
        String[] parameters = str.split(",");
        Integer id = getTaskIdFromString(str);
        String name = parameters[2];
        String description = parameters[4];
        Status status = getStatusTaskFromString(str);
        Task task = new Task(name, description, status);
        task.setType(Type.TASK);
        task.setId(id);
        return task;
    }

    private SubTask stringToSubTask(String str) throws IOException {
        String[] parameters = str.split(",");
        Integer id = getTaskIdFromString(str);
        String name = parameters[2];
        String description = parameters[4];
        Status status = getStatusTaskFromString(str);
        Integer epicId = Integer.valueOf(parameters[5]);
        SubTask subTask = new SubTask(name, description, status, epicId);
        subTask.setType(Type.SUBTASK);
        subTask.setId(id);
        return subTask;
    }

    private Epic stringToEpic(String str) throws IOException {
        String[] parameters = str.split(",");
        Integer id = getTaskIdFromString(str);
        String name = parameters[2];
        String description = parameters[4];
        Status status = getStatusTaskFromString(str);
        Epic epic = new Epic(name, description);
        epic.setStatus(status);
        epic.setId(id);
        return epic;
    }

    public void loadFromFile(File file) {
        try {
            List<String> allLines = Files.readAllLines(file.toPath());
            allLines.removeFirst();
            for (String line : allLines) {
                Type type = getTypeTaskFromString(line);
                Integer id = getTaskIdFromString(line);
                if (id <= getTaskCounter()) {
                    setTaskCounter(id + 1);
                }
                switch (type) {
                    case Type.TASK:
                        Task task = stringToTask(line);
                        loadTask(task);
                        break;
                    case Type.EPIC:
                        Epic epic = stringToEpic(line);
                        loadEpic(epic);
                        break;
                    case Type.SUBTASK:
                        SubTask subTask = stringToSubTask(line);
                        loadSubTask(subTask);
                        break;
                }
            }
        } catch (IOException ex) {
            String errorMessage = "Ошибка при считывании файла: " + ex.getMessage();
            System.out.println(errorMessage);
            throw new FileManagerFileInitilizationException(errorMessage);
        }
    }

    private Type getTypeTaskFromString(String str) throws IOException {
        String[] lines = str.split(",");
        if (lines.length < 6) throw new IOException("Ошибка считывания файла");
        Type type;
        switch (lines[1]) {
            case "TASK":
                type = Type.TASK;
                break;
            case "SUBTASK":
                type = Type.SUBTASK;
                break;
            case "EPIC":
                type = Type.EPIC;
                break;
            default:
                throw new IOException("Ошибка считывания файла");
        }
        return type;
    }

    private Status getStatusTaskFromString(String str) throws IOException {
        String[] lines = str.split(",");
        if (lines.length < 6) throw new IOException("Ошибка считывания файла");
        Status status;
        switch (lines[3]) {
            case "NEW":
                status = Status.NEW;
                break;
            case "IN_PROGRESS":
                status = Status.IN_PROGRESS;
                break;
            case "DONE":
                status = Status.DONE;
                break;
            default:
                status = Status.NEW;
                break;
        }
        return status;
    }

    private Integer getTaskIdFromString(String str) throws IOException {
        String[] lines = str.split(",");
        if (lines.length < 6) throw new IOException("Ошибка считывания файла");
        Integer id = Integer.valueOf(lines[0]);
        return id;
    }

}
