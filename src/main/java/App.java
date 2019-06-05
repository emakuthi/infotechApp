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
        String connectionString = "jdbc:h2:~/todolist.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        Sql2OSectionDao sectionDao = new Sql2OSectionDao(sql2o);
        Sql2ODepartmentDao categoryDao = new Sql2ODepartmentDao(sql2o);
        Sql2oEmployeeDao employeeDao = new Sql2oEmployeeDao(sql2o);

        ProcessBuilder process = new ProcessBuilder();
        Integer port;
        if (process.environment().get("PORT") != null) {
            port = Integer.parseInt(process.environment().get("PORT"));
        } else {
            port = 4567;
        }

        port(port);




        //get: show all sections in all categories and show all categories
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> allCategories = categoryDao.getAll();
            model.put("categories", allCategories);
            List<Section> sections = sectionDao.getAll();
            model.put("sections", sections);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //show new category form
        get("/categories/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> categories = categoryDao.getAll(); //refresh list of links for navbar
            model.put("categories", categories);
            return new ModelAndView(model, "department-form.hbs"); //new
        }, new HandlebarsTemplateEngine());

        //post: process new category form
        post("/categories", (req, res) -> { //new
            Map<String, Object> model = new HashMap<>();
            String name = req.queryParams("name");
            Department newDepartment = new Department(name);
            categoryDao.add(newDepartment);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all categories and all sections
        get("/categories/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            categoryDao.clearAllCategories();
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

        //get: show an individual category and sections it contains
        get("/categories/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfCategoryToFind = Integer.parseInt(req.params("id")); //new
            Department foundDepartment = categoryDao.findById(idOfCategoryToFind);
            model.put("category", foundDepartment);
            List<Section> allSectionsByCategory = categoryDao.getAllSectionsByCategory(idOfCategoryToFind);
            model.put("sections", allSectionsByCategory);
            model.put("categories", categoryDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "department-detail.hbs"); //new
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a category
        get("/categories/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("editCategory", true);
            Department department = categoryDao.findById(Integer.parseInt(req.params("id")));
            model.put("department", department);
            model.put("categories", categoryDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "department-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process a form to update a category
        post("/categories/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfCategoryToEdit = Integer.parseInt(req.params("id"));
            String newName = req.queryParams("newCategoryName");
            categoryDao.update(idOfCategoryToEdit, newName);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete a category and sections it contains
        //  /categories/:id/delete

        //get: delete an individual section
        get("/categories/:category_id/sections/:section_id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToDelete = Integer.parseInt(req.params("section_id"));
            sectionDao.deleteById(idOfTaskToDelete);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show new section form
        get("/sections/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> categories = categoryDao.getAll();
            model.put("categories", categories);
            return new ModelAndView(model, "section-form.hbs");
        }, new HandlebarsTemplateEngine());

        //section: process new section form
        post("/sections", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> allCategories = categoryDao.getAll();
            model.put("categories", allCategories);
            String description = req.queryParams("description");
            int categoryId = Integer.parseInt(req.queryParams("categoryId"));
            Section newSection = new Section(description, categoryId ); //ignore the hardcoded categoryId
            sectionDao.add(newSection);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show an individual section that is nested in a category
        get("/categories/:category_id/sections/:section_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToFind = Integer.parseInt(req.params("section_id"));
            Section foundSection = sectionDao.findById(idOfTaskToFind);
            int idOfCategoryToFind = Integer.parseInt(req.params("category_id"));
            Department foundDepartment = categoryDao.findById(idOfCategoryToFind);
            List<Employee> employees = employeeDao.getAll();
            model.put("section", foundSection);
            model.put("category", foundDepartment);
            model.put("categories", categoryDao.getAll()); //refresh list of links for navbar
            model.put("employees", employeeDao.getAll());
            return new ModelAndView(model, "section-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a section
        get("/sections/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Department> allCategories = categoryDao.getAll();
            model.put("categories", allCategories);
            Section section = sectionDao.findById(Integer.parseInt(req.params("id")));
            model.put("section", section);
            model.put("editTask", true);
            return new ModelAndView(model, "section-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process a form to update a section
        post("/sections/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int sectionToEditId = Integer.parseInt(req.params("id"));
            String newContent = req.queryParams("description");
            int newCategoryId = Integer.parseInt(req.queryParams("categoryId"));
            sectionDao.update(sectionToEditId, newContent, newCategoryId);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());


        ////////////////////////////////////////////////////////////////////////////////////////


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
            Employee newEmployee = new Employee(employeeName, ekNo, designation,sectionId ); //ignore the hardcoded categoryId
            employeeDao.add(newEmployee);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show an individual employee that is nested in a section
        get("/sections/:section_id/employees/:employee_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfEmployeeToFind = Integer.parseInt(req.params("employee_id"));
            Employee foundEmployee = employeeDao.findById(idOfEmployeeToFind);
            int idOfTaskToFind = Integer.parseInt(req.params("section_id"));
            Section foundSection = sectionDao.findById(idOfTaskToFind);
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
            int newTaskId = Integer.parseInt(req.queryParams("sectionId"));
            employeeDao.update(employeeToEditId, newEmployeeName, newTaskId,newDesignation, newEKNo);
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
