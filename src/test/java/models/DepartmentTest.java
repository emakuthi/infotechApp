
package models;

import org.junit.Test;
import static org.junit.Assert.*;

public class DepartmentTest {

    @Test
    public void NewCategoryObjectGetsCorrectlyCreated_true() throws Exception {
        Department department = setupNewCategory();
        assertEquals(true, department instanceof Department);
    }

    @Test
    public void CategoryInstantiatesWithName_school() throws Exception {
        Department department = setupNewCategory();
        assertEquals("school", department.getName());
    }

    //helper methods
    public Department setupNewCategory(){
        return new Department("school");
    }
}