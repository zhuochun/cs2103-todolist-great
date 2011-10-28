package cs2103.t14j1.logic.reminder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cs2103.t14j1.storage.Task;

/**
 * a thread reminder of task
 * 
 * @author Zhuochun
 */
public class Reminder {
    
    private ReminderDialog   dialog;
    private ArrayList<Timer> timers; // A list of timers
    
    /**
     * constructor
     * 
     * @param shell the shell of application
     */
    public Reminder() {
        this.timers = new ArrayList<Timer>();
        this.dialog = new ReminderDialog();
    }
    
    public Timer addReminder(Task task, Date date) {
        Timer newTimer = new Timer();
        
        newTimer.schedule(new ReminderTask(task, newTimer), date);
        
        timers.add(newTimer);
        
        return newTimer;
    }
    
    public boolean removeReminder(Timer timer) {
        timer.cancel();
        
        timers.remove(timer);
        
        return true;
    }
    
    /**
     * check is any reminder been set
     * 
     * @return true if no reminder is set
     */
    public boolean isEmpty() {
        return timers.isEmpty();
    }
    
    /**
     * number of reminders have been set
     * 
     * @return number of reminders
     */
    public int getSize() {
        return timers.size();
    }
    
    class ReminderTask extends TimerTask {
        Task  task;
        Timer timer;
        
        public ReminderTask(Task task, Timer timer) {
            this.task  = task;
            this.timer = timer;
        }
        
        public void run() {
            System.out.println(task.getName() + " is up!");
            
            timer.cancel();
            
            timers.remove(timer);
            
            openDialog();
        }
    }
    
    void openDialog() {
        try {
            ReminderDialog window = new ReminderDialog();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}