package dao;

import models.Employee;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oEmployeeDao implements EmployeeDao{

    private final Sql2o sql2o;

    public Sql2oEmployeeDao(Sql2o sql2o){
        this.sql2o = sql2o; //making the sql2o object available everywhere so we can call methods in it
    }

    @Override
    public void add(Employee employee) {
        String sql = "INSERT INTO employees (employeeName, sectionId, ekNo, designation) VALUES (:employeeName, :sectionId, :ekNo, :designation)"; //raw sql
        try(Connection con = sql2o.open()){ //try to open a connection
            int id = (int) con.createQuery(sql, true) //make a new variable
                    .bind(employee)
                    .executeUpdate() //run it all
                    .getKey(); //int id is now the row number (row “key”) of db
            employee.setId(id); //update object to set id now from database
        } catch (Sql2oException ex) {
            System.out.println(ex); //oops we have an error!
        }
    }

    @Override
    public List<Employee> getAll() {
        try(Connection con = sql2o.open()){
            return con.createQuery("SELECT * FROM employees") //raw sql
                    .executeAndFetch(Employee.class); //fetch a list
        }
    }


    @Override
    public Employee findById(int id) {
        try(Connection con = sql2o.open()){
            return con.createQuery("SELECT * FROM employees WHERE id = :id")
                    .addParameter("id", id) //key/value pair, key must match above
                    .executeAndFetchFirst(Employee.class); //fetch an individual item
        }
    }

    @Override
    public void update(int id, String newEmployeeName, int newTaskId, String newEkNo, String newDesignation){
        String sql = "UPDATE employees SET (employeeName, sectionId, ekNo, designation) = (:employeeName, :sectionId, :ekNo, :designation) WHERE id=:id"; //raw sql
        try(Connection con = sql2o.open()){
            con.createQuery(sql)
                    .addParameter("employeeName", newEmployeeName)
                    .addParameter("sectionId", newTaskId)
                    .addParameter("id", id)
                    .addParameter("ekNo", newEkNo)
                    .addParameter("designation", newDesignation)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE from employees WHERE id=:id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    @Override
    public void clearAllEmployees() {
        String sql = "DELETE from employees";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

}
