package cs2103.t14j1.logic.reminder;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;

public class ReminderTest {
    
    private TaskList ls;

    @Before
    public void setUp() throws Exception {
        ls = new TaskList("ReminderTest");
        
        Date date = new Date();
        
        date.setTime(date.getTime() + 20000);
        
        // add task
        String name = "new task 1";
        String place = null;
        String list = "Inbox";
        Priority priority = Priority.IMPORTANT;
        Date startDateTime = null;
        Date endDateTime = null;
        Date deadline = null;
        Long duration = null;
        boolean status = Task.INCOMPLETE;
        Task newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
        ls.addTask(newTask);
        
        name = "new task 2";
        newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
        ls.addTask(newTask);
        
        name = "new task 3";
        newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
        ls.addTask(newTask);
        
        name = "new task 4";
        newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
        ls.addTask(newTask);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        Reminder r = new Reminder();
        
        Date date = new Date();
        
        for (Task t : ls) {
            date.setTime(date.getTime() + 9000);
            r.addReminder(t, date);
            System.out.println("set " + t.getName() + " on " + date.toString());
            System.out.println(r.getSize() + " reminders");
        }
        
        try {
            Thread.sleep(45000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertEquals(0, r.getSize());
    }
}