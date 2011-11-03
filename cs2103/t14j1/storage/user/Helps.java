package cs2103.t14j1.storage.user;

/**
 * Tips for TaskMeter users
 * 
 * @author Tong
 */

public class Helps {
    
    private static int index = 0;
    
    // TODO: try to write as many as tips)
    private static final String[] tips = {
        "Welcome to TaskMeter, press global hotkey F6 to open or minimize TaskMeter window.",
        "Alternatively, typing \"add + spacebar + Task\" onto TaskMeter smartbar also works fine.",
        "In addition to Task, Location, Date/Time, Duration, List and Priority of a task can be specified.",
        "TaskMeter awares it's important to know the location of your appointment; An easy way to do so is provided (next tip).",
        "Specifying Location using '@': \"add jogging @home\". Use () if there is space like @(my home)",
        "Knowing which days your appointment are on is very important, hence, TaskMeter makes it simple to specify Date.",
        "Specifying Date: \"add my father's birthday 20 december\" Day comes before Month before Year (optional)",
        "When no Year is indicated, TaskMeters takes the soonest Year with Day/Month specified as task's Year",
        "Specifying Day in Week:\"add GUI workshop on Thursday\"",
        "Keywords you might consider when specifying Day in Week are \"this\"(by default) and \"next\".",
        "To make it even more comprehensive, specifying time can also be done easily.",
        "Specifying Time: \"add Skype interview with Microsoft 10pm\". Both AM/PM and 24:00 formats are recognized",
        "Specifying Start Time/End Time using '~': \"add study marathon 10:00 ~ 22:00\" or \"add study marathon 10am ~ 10pm\"",
        "Nevertheless, it happens from time to time that a Task doesn't have a specific time period. For instance, 2 hours is required for finishing an assignment at any time before next Monday.",
        "Specifying Duration using \"for\": \"add hangout with friends for 2 hours\"",
        "Want to separate work and personal life? It's convenient to catagorize tons of Tasks using List feature.",
        "Specifying List using '#': \"add dinner with my parents #family\". Again, use () if there is space like #(high school friends)",
        "If List is not specified, the Task entered will be stored in default \"Inbox List\".",
        "Furthermore, if the List specified doesn't exist, it will be created accordingly.",
        "In daily life, there're many things we have to do. Sometimes we can't do them all and want to skip some. Go on with Priority feature.",
        "Specifying Priority using '!': \"add dating with girl Z !1\" 1 for Important, 2 for Normal and 3 for Low priorities respectively.",
        "When Priority is not indicated, TaskMeter takes it as Normal Priority by default.",
        "Beside adding a Task, searching, editing and deleting could also be done very easily.",
        "TaskMeter adopts color-coding to make it easier for users to distinguish different type of tasks.",
        "WHITE = DEFAULT. A task in white has nothing special.",
        "RED = IMPORTANT. A task in red has high priority.",
        "PURPLE = OVERDUE. A task in purple shows that you’ve missed its deadline.",
        "GREY = COMPLETE. A task in grey is the task you’ve completed."
    };
    
    /**
     * get a random tip from all the tips
     * 
     * @return a tip
     */
    public static String getRandomTip() {
        return tips[(int) Math.random() % tips.length];
    }
    
    /**
     * get current tip
     * 
     * @return the tip under current index
     */
    public static String getTip() {
        return tips[index];
    }
    
    /**
     * get a tip from in sequence from the tips
     * 
     * @return a tip
     */
    public static String getNextTip() {
        if (hasNext()) {
            return tips[++index];
        } else {
            return null;
        }
    }
    
    /**
     * get a tip from in backward sequence from the tips
     * 
     * @return a tip
     */
    public static String getPrevTip() {
        if (hasPrev()) {
            return tips[--index];
        } else {
            return null;
        }
    }
    
    /**
     * get the help formed based on all the tips
     * 
     * @return help document
     */
    public static String getHelp() {
        StringBuffer paragraph = new StringBuffer();
        
        for (String tip : tips) {
            paragraph.append(tip);
            paragraph.append("\n");
        }
        
        return paragraph.toString();
    }
    
    /**
     * check the tip index is in range
     * 
     * @return true if yes
     */
    public static boolean hasNext() {
        return index < tips.length - 1;
    }
    
    /**
     * check the tip index is in range
     * 
     * @return true if yes
     */
    public static boolean hasPrev() {
        return index > 0;
    }
    
}