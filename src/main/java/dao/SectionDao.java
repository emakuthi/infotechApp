package dao;

import models.Task;
import java.util.List;

public interface SectionDao {

    // LIST
    List<Task> getAll();

    // CREATE
    void add(Task task);

    // READ
    Task findById(int id);

    // UPDATE
    void update(int id, String content, int categoryId);

    // DELETE
    void deleteById(int id);
    void clearAllTasks();
}