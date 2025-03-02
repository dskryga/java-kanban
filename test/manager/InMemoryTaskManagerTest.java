package manager;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest{


    @BeforeEach
    void init() {
        tm = getTaskManager();
    }

    @Override
    TaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}