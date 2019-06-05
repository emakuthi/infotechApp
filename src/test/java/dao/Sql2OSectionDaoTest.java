package dao;

import models.Section;
import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;

public class Sql2OSectionDaoTest {
    private Sql2OSectionDao taskDao; //ignore me for now. We'll create this soon.
    private Connection conn; //must be sql2o class conn

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        taskDao = new Sql2OSectionDao(sql2o); //ignore me for now
        conn = sql2o.open(); //keep connection open through entire test so it does not get erased
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingTaskSetsId() throws Exception {
        Section section = setupNewTask();
        int originalTaskId = section.getId();
        taskDao.add(section);
        assertNotEquals(originalTaskId, section.getId()); //how does this work?
    }

    @Test
    public void existingTasksCanBeFoundById() throws Exception {
        Section section = setupNewTask();
        taskDao.add(section); //add to dao (takes care of saving)
        Section foundSection = taskDao.findById(section.getId()); //retrieve
        assertEquals(section, foundSection); //should be the same
    }

    @Test
    public void addedTasksAreReturnedFromgetAll() throws Exception {
        Section section = setupNewTask();
        taskDao.add(section);
        assertEquals(1, taskDao.getAll().size());
    }

    @Test
    public void noTasksReturnsEmptyList() throws Exception {
        assertEquals(0, taskDao.getAll().size());
    }

    @Test
    public void updateChangesTaskContent() throws Exception {
        String initialDescription = "mow the lawn";
        Section section = new Section(initialDescription, 1);
        taskDao.add(section);
        taskDao.update(section.getId(),"brush the cat", 1);
        Section updatedSection = taskDao.findById(section.getId()); //why do I need to refind this?
        assertNotEquals(initialDescription, updatedSection.getDescription());
    }

    @Test
    public void deleteByIdDeletesCorrectTask() throws Exception {
        Section section = setupNewTask();
        taskDao.add(section);
        taskDao.deleteById(section.getId());
        assertEquals(0, taskDao.getAll().size());
    }

    @Test
    public void clearAllClearsAll() throws Exception {
        Section section = setupNewTask();
        Section otherSection = new Section("brush the cat", 2);
        taskDao.add(section);
        taskDao.add(otherSection);
        int daoSize = taskDao.getAll().size();
        taskDao.clearAllTasks();
        assertTrue(daoSize > 0 && daoSize > taskDao.getAll().size()); //this is a little overcomplicated, but illustrates well how we might use `assertTrue` in a different way.
    }

    @Test
    public void categoryIdIsReturnedCorrectly() throws Exception {
        Section section = setupNewTask();
        int originalCatId = section.getCategoryId();
        taskDao.add(section);
        assertEquals(originalCatId, taskDao.findById(section.getId()).getCategoryId());
    }

    public Section setupNewTask(){
        return new Section("mow the lawn", 1);
    }
}