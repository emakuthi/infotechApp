package dao;

import models.Department;
import models.Section;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Sql2ODepartmentDaoTest {
    private Sql2ODepartmentDao categoryDao;
    private Sql2OSectionDao taskDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        categoryDao = new Sql2ODepartmentDao(sql2o);
        taskDao = new Sql2OSectionDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingCategorySetsId() throws Exception {
        Department department = setupNewCategory();
        int originalCategoryId = department.getId();
        categoryDao.add(department);
        assertNotEquals(originalCategoryId, department.getId());
    }

    @Test
    public void existingCategoriesCanBeFoundById() throws Exception {
        Department department = setupNewCategory();
        categoryDao.add(department);
        Department foundDepartment = categoryDao.findById(department.getId());
        assertEquals(department, foundDepartment);
    }

    @Test
    public void addedCategoriesAreReturnedFromGetAll() throws Exception {
        Department department = setupNewCategory();
        categoryDao.add(department);
        assertEquals(1, categoryDao.getAll().size());
    }

    @Test
    public void noCategoriesReturnsEmptyList() throws Exception {
        assertEquals(0, categoryDao.getAll().size());
    }

    @Test
    public void updateChangesCategoryContent() throws Exception {
        String initialDescription = "Yardwork";
        Department department = new Department(initialDescription);
        categoryDao.add(department);
        categoryDao.update(department.getId(),"Cleaning");
        Department updatedDepartment = categoryDao.findById(department.getId());
        assertNotEquals(initialDescription, updatedDepartment.getName());
    }

    @Test
    public void deleteByIdDeletesCorrectCategory() throws Exception {
        Department department = setupNewCategory();
        categoryDao.add(department);
        categoryDao.deleteById(department.getId());
        assertEquals(0, categoryDao.getAll().size());
    }

    @Test
    public void clearAllClearsAllCategories() throws Exception {
        Department department = setupNewCategory();
        Department otherDepartment = new Department("Cleaning");
        categoryDao.add(department);
        categoryDao.add(otherDepartment);
        int daoSize = categoryDao.getAll().size();
        categoryDao.clearAllCategories();
        assertTrue(daoSize > 0 && daoSize > categoryDao.getAll().size());
    }

    @Test
    public void getAllTasksByCategoryReturnsTasksCorrectly() throws Exception {
        Department department = setupNewCategory();
        categoryDao.add(department);
        int categoryId = department.getId();
        Section newSection = new Section("mow the lawn", categoryId);
        Section otherSection = new Section("pull weeds", categoryId);
        Section thirdSection = new Section("trim hedge", categoryId);
        taskDao.add(newSection);
        taskDao.add(otherSection); //we are not adding task 3 so we can test things precisely.
        assertEquals(2, categoryDao.getAllTasksByCategory(categoryId).size());
        assertTrue(categoryDao.getAllTasksByCategory(categoryId).contains(newSection));
        assertTrue(categoryDao.getAllTasksByCategory(categoryId).contains(otherSection));
        assertFalse(categoryDao.getAllTasksByCategory(categoryId).contains(thirdSection)); //things are accurate!
    }

    // helper method
    public Department setupNewCategory(){
        return new Department("Yardwork");
    }
}