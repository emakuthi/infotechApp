import dao.Sql2oEmployeeDao;
import models.Department;
import models.Section;
import models.Employee;
import dao.Sql2ODepartmentDao;
import dao.Sql2OSectionDao;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");
        String connectionString = "jdbc:h2:~/department.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        Sql2OSectionDao sectionDao = new Sql2OSectionDao(sql2o);
        Sql2ODepartmentDao departmentDao = new Sql2ODepartmentDao(sql2o);
        Sql2oEmployeeDao employeeDao = new Sql2oEmployeeDao(sql2o);

        ProcessBuilder process = new ProcessBuilder();
        Integer port;
        if (process.environment().get("PORT") != null) {
            port = Integer.parseInt(process.environment().get("PORT"));
        } else {
            port = 4567;
        }

        port(port);


        //get: show all sections in all departments and show all departments
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> allDepartments = departmentDao.getAll();
            model.put("departments", allDepartments);
            List<Section> sections = sectionDao.getAll();
            model.put("sections", sections);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //show new department form
        get("/departments/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> departments = departmentDao.getAll(); //refresh list of links for navbar
            model.put("departments", departments);
            return new ModelAndView(model, "department-form.hbs"); //new
        }, new HandlebarsTemplateEngine());

        //post: process new department form
        post("/departments", (req, res) -> { //new
            Map<String, Object> model = new HashMap<>();
            String name = req.queryParams("name");
            Department newDepartment = new Department(name);
            departmentDao.add(newDepartment);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all departments and all sections
        get("/departments/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            departmentDao.clearAllDepartments();
            sectionDao.clearAllSections();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all sections
        get("/sections/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            sectionDao.clearAllSections();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show an individual department and sections it contains
        get("/departments/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfDepartmentToFind = Integer.parseInt(req.params("id")); //new
            Department foundDepartment = departmentDao.findById(idOfDepartmentToFind);
            model.put("department", foundDepartment);
            List<Section> allSectionsByDepartment = departmentDao.getAllSectionsByDepartment(idOfDepartmentToFind);
            model.put("sections", allSectionsByDepartment);
            model.put("departments", departmentDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "department-detail.hbs"); //new
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a department
        get("/departments/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("editDepartment", true);
            Department department = departmentDao.findById(Integer.parseInt(req.params("id")));
            model.put("department", department);
            model.put("departments", departmentDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "department-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process a form to update a department
        post("/departments/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfDepartmentToEdit = Integer.parseInt(req.params("id"));
            String newName = req.queryParams("newDepartmentName");
            departmentDao.update(idOfDepartmentToEdit, newName);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete a department and sections it contains
        //  /departments/:id/delete

        //get: delete an individual section
        get("/departments/:department_id/sections/:section_id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSectionToDelete = Integer.parseInt(req.params("section_id"));
            sectionDao.deleteById(idOfSectionToDelete);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show new section form
        get("/sections/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> departments = departmentDao.getAll();
            model.put("departments", departments);
            return new ModelAndView(model, "section-form.hbs");
        }, new HandlebarsTemplateEngine());

        //section: process new section form
        post("/sections", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> allDepartments = departmentDao.getAll();
            model.put("departments", allDepartments);
            String sectionName = req.queryParams("sectionName");
            int departmentId = Integer.parseInt(req.queryParams("departmentId"));
            Section newSection = new Section(sectionName, departmentId ); //ignore the hardcoded departmentId
            sectionDao.add(newSection);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show an individual section that is nested in a department
        get("/departments/:department_id/sections/:section_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSectionToFind = Integer.parseInt(req.params("section_id"));
            Section foundSection = sectionDao.findById(idOfSectionToFind);
            int idOfDepartmentToFind = Integer.parseInt(req.params("department_id"));
            Department foundDepartment = departmentDao.findById(idOfDepartmentToFind);
            List<Employee> employees = employeeDao.getAll();
            model.put("section", foundSection);
            model.put("department", foundDepartment);
            model.put("departments", departmentDao.getAll()); //refresh list of links for navbar
            model.put("employees", employeeDao.getAll());
            return new ModelAndView(model, "section-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a section
        get("/sections/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> allDepartments = departmentDao.getAll();
            model.put("departments", allDepartments);
            Section section = sectionDao.findById(Integer.parseInt(req.params("id")));
            model.put("section", section);
            model.put("editSection", true);
            return new ModelAndView(model, "section-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process a form to update a section
        post("/sections/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int sectionToEditId = Integer.parseInt(req.params("id"));
            String newSectionName = req.queryParams("sectionName");
            int newDepartmentId = Integer.parseInt(req.queryParams("departmentId"));
            sectionDao.update(sectionToEditId, newSectionName, newDepartmentId);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show new employee form
        get("/employees/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Section> sections = sectionDao.getAll();
            model.put("sections", sections);
            return new ModelAndView(model, "employee-form.hbs");
        }, new HandlebarsTemplateEngine());


        //section: process new employee form
        post("/employees", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Section> allSections = sectionDao.getAll();
            model.put("sections", allSections);
            String employeeName = req.queryParams("employeeName");
            String ekNo = req.queryParams("ekNo");
            String designation = req.queryParams("designation");
            int sectionId = Integer.parseInt(req.queryParams("sectionId"));
            Employee newEmployee = new Employee(employeeName, ekNo, designation,sectionId ); //ignore the hardcoded departmentId
            employeeDao.add(newEmployee);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show an individual employee that is nested in a section
        get("/sections/:section_id/employees/:employee_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfEmployeeToFind = Integer.parseInt(req.params("employee_id"));
            Employee foundEmployee = employeeDao.findById(idOfEmployeeToFind);
            int idOfSectionToFind = Integer.parseInt(req.params("section_id"));
            Section foundSection = sectionDao.findById(idOfSectionToFind);
            model.put("employee", foundEmployee);
            model.put("section", foundSection);
            model.put("sections", sectionDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "employee-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a employee
        get("/employees/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Section> allSections = sectionDao.getAll();
            model.put("sections", allSections);
            Employee employee = employeeDao.findById(Integer.parseInt(req.params("id")));
            model.put("employee", employee);
            model.put("editEmployee", true);
            return new ModelAndView(model, "employee-form.hbs");
        }, new HandlebarsTemplateEngine());


        //post: process a form to update a employee
        post("/employees/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int employeeToEditId = Integer.parseInt(req.params("id"));
            String newEmployeeName = req.queryParams("employeeName");
            String newEKNo = req.queryParams("ekNo");
            String newDesignation = req.queryParams("designation");
            int newSectionId = Integer.parseInt(req.queryParams("sectionId"));
            employeeDao.update(employeeToEditId, newEmployeeName, newSectionId,newDesignation, newEKNo);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all sections
        get("/employees/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            employeeDao.clearAllEmployees();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

    }

}
