package dao;

import models.Section;

import java.util.List;

public interface SectionDao {

    // LIST
    List<Section> getAll();

    // CREATE
    void add(Section section);

    // READ
    Section findById(int id);

    // UPDATE
    void update(int id, String content, int categoryId);

    // DELETE
    void deleteById(int id);
    void clearAllTasks();
}