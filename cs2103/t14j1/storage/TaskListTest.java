package cs2103.t14j1.storage;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskListTest {
    
    TaskList tlist;

    @Before
    public void setUp() throws Exception {
        tlist = new TaskList("Test");
        
        String name = "new task";
        String place = null;
        String list = "Inbox";
        Priority priority = Priority.IMPORTANT;
        Date startDateTime = null;
        Date endDateTime = null;
        Date deadline = null;
        Long duration = new Long(20);
        boolean status = Task.INCOMPLETE;
        
        Task newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
        
        tlist.addTask(newTask);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetTask() {
        Task task = tlist.getTask(1);
        
        System.out.println("\t" + task.getName());
        System.out.println("\t" + task.getPriorityStr());
        System.out.println("\t" + task.getStatusStr());
        System.out.println("\t--------------");
    }

    @Test
    public void testDelete() {
        Task task = tlist.getTask(1);
        
        System.out.println("\t" + task.getName());
        System.out.println("\t" + task.getPriorityStr());
        System.out.println("\t" + task.getStatusStr());
        System.out.println("\t--------------");
        
        tlist.removeTask(1);
        
        System.out.println(tlist.getSize());
    }

}
