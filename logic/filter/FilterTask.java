package logic.filter;

import java.util.Date;

import storage.Priority;
import storage.Task;

import logic.DateFormat;


/**
 * filter tasks according to parameter set
 * 
 * @author Zhuochun
 */
public class FilterTask {
    
    private static Filter filter     = Filter.FILTER_ALL;
    
    private static Date[] todayDate  = getDate(0, 1);
    private static Date[] tmlDate    = getDate(1, 1);
    private static Date[] weekDate   = getDate(0, 7);
    
    public static void setFilter(Filter f) {
        filter     = f;
        todayDate  = getDate(0, 1);
        tmlDate    = getDate(1, 1);
        weekDate   = getDate(0, 7);
    }
    
    private static Date[] getDate(int afterToday, int length) {
        Date today = DateFormat.todayDate;

        Date start = DateFormat.getDateAfter(today, afterToday);
        Date end   = DateFormat.getDateAfter(start, length);
        
        Date[] result = {start, end};
        
        return result;
    }
    
    public static boolean filter(Task task) {
        if (filter == Filter.FILTER_ALL) {
            return true;
        } else if (filter == Filter.FILTER_IMPORTANT && task.getPriority() == Priority.IMPORTANT) {
            return true;
        } else if (filter == Filter.FILTER_COMPLETED && task.isCompleted()) {
            return true;
        } else if (filter == Filter.FILTER_OVERDUE && !task.isCompleted()
                && task.compareDeadline(DateFormat.getNow()) < 0) {
            return true;
        } else if (filter == Filter.FILTER_TODAY && task.isWithinPeriod(todayDate[0], todayDate[1])) {
            return true;
        } else if (filter == Filter.FILTER_TOMORROW && task.isWithinPeriod(tmlDate[0], tmlDate[1])) {
            return true;
        } else if (filter == Filter.FILTER_NEXT_DAYS && task.isWithinPeriod(weekDate[0], weekDate[1])) {
            return true;
        } else if (filter == Filter.FILTER_WITHOUT_DATE && task.getStartDateTime() == null) {
            return true;
        }
        
        return false;
    }

}
