package dao;

import models.Department;
import models.Section;
import java.util.List;

public interface DepartmentDao {

    //LIST
    List<Department> getAll();

    //CREATE
    void add (Department department);

    //READ
    Department findById(int id);
    List<Section> getAllSectionsByDepartment(int departmentId);

    //UPDATE
    void update(int id, String name);

    //DELETE
    void deleteById(int id);
    void clearAllDepartments();
}