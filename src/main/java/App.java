import dao.Sql2oEmployeeDao;
import models.Category;
import models.Task;
import models.Employee;
import dao.Sql2oCategoryDao;
import dao.Sql2oTaskDao;
import dao.Sql2oEmployeeDao;

import java.util.ArrayList;
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
        Sql2oTaskDao taskDao = new Sql2oTaskDao(sql2o);
        Sql2oCategoryDao categoryDao = new Sql2oCategoryDao(sql2o);
        Sql2oEmployeeDao employeeDao = new Sql2oEmployeeDao(sql2o);

        ProcessBuilder process = new ProcessBuilder();
        Integer port;
        if (process.environment().get("PORT") != null) {
            port = Integer.parseInt(process.environment().get("PORT"));
        } else {
            port = 4567;
        }

        port(port);




        //get: show all tasks in all categories and show all categories
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Category> allCategories = categoryDao.getAll();
            model.put("categories", allCategories);
            List<Task> tasks = taskDao.getAll();
            model.put("tasks", tasks);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //show new category form
        get("/categories/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Category> categories = categoryDao.getAll(); //refresh list of links for navbar
            model.put("categories", categories);
            return new ModelAndView(model, "category-form.hbs"); //new
        }, new HandlebarsTemplateEngine());

        //post: process new category form
        post("/categories", (req, res) -> { //new
            Map<String, Object> model = new HashMap<>();
            String name = req.queryParams("name");
            Category newCategory = new Category(name);
            categoryDao.add(newCategory);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all categories and all tasks
        get("/categories/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            categoryDao.clearAllCategories();
            taskDao.clearAllTasks();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all tasks
        get("/tasks/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            taskDao.clearAllTasks();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show an individual category and tasks it contains
        get("/categories/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfCategoryToFind = Integer.parseInt(req.params("id")); //new
            Category foundCategory = categoryDao.findById(idOfCategoryToFind);
            model.put("category", foundCategory);
            List<Task> allTasksByCategory = categoryDao.getAllTasksByCategory(idOfCategoryToFind);
            model.put("tasks", allTasksByCategory);
            model.put("categories", categoryDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "category-detail.hbs"); //new
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a category
        get("/categories/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("editCategory", true);
            Category category = categoryDao.findById(Integer.parseInt(req.params("id")));
            model.put("category", category);
            model.put("categories", categoryDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "category-form.hbs");
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

        //get: delete a category and tasks it contains
        //  /categories/:id/delete

        //get: delete an individual task
        get("/categories/:category_id/tasks/:task_id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToDelete = Integer.parseInt(req.params("task_id"));
            taskDao.deleteById(idOfTaskToDelete);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show new task form
        get("/tasks/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Category> categories = categoryDao.getAll();
            model.put("categories", categories);
            return new ModelAndView(model, "task-form.hbs");
        }, new HandlebarsTemplateEngine());

        //task: process new task form
        post("/tasks", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Category> allCategories = categoryDao.getAll();
            model.put("categories", allCategories);
            String description = req.queryParams("description");
            int categoryId = Integer.parseInt(req.queryParams("categoryId"));
            Task newTask = new Task(description, categoryId ); //ignore the hardcoded categoryId
            taskDao.add(newTask);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show an individual task that is nested in a category
        get("/categories/:category_id/tasks/:task_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToFind = Integer.parseInt(req.params("task_id"));
            Task foundTask = taskDao.findById(idOfTaskToFind);
            int idOfCategoryToFind = Integer.parseInt(req.params("category_id"));
            Category foundCategory = categoryDao.findById(idOfCategoryToFind);
            List<Employee> employees = employeeDao.getAll();
            model.put("task", foundTask);
            model.put("category", foundCategory);
            model.put("categories", categoryDao.getAll()); //refresh list of links for navbar
            model.put("employees", employeeDao.getAll());
            return new ModelAndView(model, "task-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a task
        get("/tasks/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Category> allCategories = categoryDao.getAll();
            model.put("categories", allCategories);
            Task task = taskDao.findById(Integer.parseInt(req.params("id")));
            model.put("task", task);
            model.put("editTask", true);
            return new ModelAndView(model, "task-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process a form to update a task
        post("/tasks/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int taskToEditId = Integer.parseInt(req.params("id"));
            String newContent = req.queryParams("description");
            int newCategoryId = Integer.parseInt(req.queryParams("categoryId"));
            taskDao.update(taskToEditId, newContent, newCategoryId);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());


        ////////////////////////////////////////////////////////////////////////////////////////


        //get: show new employee form
        get("/employees/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Task> tasks = taskDao.getAll();
            model.put("tasks", tasks);
            return new ModelAndView(model, "employee-form.hbs");
        }, new HandlebarsTemplateEngine());


        //task: process new employee form
        post("/employees", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Task> allTasks= taskDao.getAll();
            model.put("tasks", allTasks);
            String employeeName = req.queryParams("employeeName");
            String ekNo = req.queryParams("ekNo");
            String designation = req.queryParams("designation");
            int taskId = Integer.parseInt(req.queryParams("taskId"));
            Employee newEmployee = new Employee(employeeName, ekNo, designation,taskId ); //ignore the hardcoded categoryId
            employeeDao.add(newEmployee);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show an individual employee that is nested in a task
        get("/tasks/:task_id/employees/:employee_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfEmployeeToFind = Integer.parseInt(req.params("employee_id"));
            Employee foundEmployee = employeeDao.findById(idOfEmployeeToFind);
            int idOfTaskToFind = Integer.parseInt(req.params("task_id"));
            Task foundTask = taskDao.findById(idOfTaskToFind);
            model.put("employee", foundEmployee);
            model.put("task", foundTask);
            model.put("tasks", taskDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "employee-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a employee
        get("/employees/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Task> allTasks = taskDao.getAll();
            model.put("tasks", allTasks);
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
            int newTaskId = Integer.parseInt(req.queryParams("taskId"));
            employeeDao.update(employeeToEditId, newEmployeeName, newTaskId,newDesignation, newEKNo);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all tasks
        get("/employees/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            employeeDao.clearAllEmployees();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

    }

}
