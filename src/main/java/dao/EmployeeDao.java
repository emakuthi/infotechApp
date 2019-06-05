package dao;

import models.Employee;

import java.util.List;

public interface EmployeeDao {

    // LIST
    List<Employee> getAll();

    // CREATE
    void add(Employee employee);

    // READ
    Employee findById(int id);

    // UPDATE
    void update(int id, String employeeName, int taskId, String ekNo, String designation);

    // DELETE
    void deleteById(int id);
    void clearAllEmployees();
}
