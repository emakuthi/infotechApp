package models;

import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class SectionTest {
    @Test
    public void NewTaskObjectGetsCorrectlyCreated_true() throws Exception {
        Section section = setupNewTask();
        assertEquals(true, section instanceof Section);
    }

    @Test
    public void TaskInstantiatesWithDescription_true() throws Exception {
        Section section = setupNewTask();
        assertEquals("Mow the lawn", section.getDescription());
    }

    @Test
    public void isCompletedPropertyIsFalseAfterInstantiation() throws Exception {
        Section section = setupNewTask();
        assertEquals(false, section.getCompleted()); //should never start as completed
    }

    @Test
    public void getCreatedAtInstantiatesWithCurrentTimeToday() throws Exception {
        Section section = setupNewTask();
        assertEquals(LocalDateTime.now().getDayOfWeek(), section.getCreatedAt().getDayOfWeek());
    }

    //helper methods
    public Section setupNewTask(){
        return new Section("Mow the lawn", 1);
    }
}
