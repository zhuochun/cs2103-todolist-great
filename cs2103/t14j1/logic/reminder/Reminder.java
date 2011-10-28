package cs2103.t14j1.logic.reminder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Shell;

import cs2103.t14j1.storage.Task;

public class Reminder {
    
    private final Shell      shell;
    private ArrayList<Timer> timers; // A list of timers
    
    /**
     * constructor
     * 
     * @param shell the shell of application
     */
    public Reminder(Shell shell) {
        this.shell  = shell;
        this.timers = new ArrayList<Timer>();
    }
    
    /**
     * this constructor is for testing
     */
    public Reminder() {
        shell       = null;
        this.timers = new ArrayList<Timer>();
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
        }
    }
}