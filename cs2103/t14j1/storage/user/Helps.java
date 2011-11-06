package cs2103.t14j1.storage.user;

public class Helps {
    
    private static int index = 0;
    
    private static final String[] tips = {
        "Welcome to TaskMeter!\nClick next to see all the tips.",
        "To add a TASK via TaskMeter smartbar:\n\"add + spacebar + TASK\"\n\nadd go jogging",
        "In addition to TASK, many details can be specified.\nThis includes LOCATION, DATE/TIME, DURATION, PRIORITY and LIST.",
        "TaskMeter awares it's important to know the LOCATION of your appointment; An easy way to do so is provided.",
        "Specifying LOCATION using '@':\n\"add + TASK + @ + LOCATION\"\n\nadd jogging @home\nRemarks: Use () if there is space like @(my home)",
        "Knowing which day your appointments are on is very important, hence, TaskMeter makes it simple to specify DATE.",
        "Specifying DATE:\n\"add + TASK + DATE\"\n\nadd my father's birthday 20 December\nRemarks: Day comes before Month before Year (optional)",
        "When no Year is indicated, TaskMeter takes the soonest Year with the Day/Month specified as task's Year",
        "Specifying DAY IN WEEK:\n\"add + TASK + DAY IN WEEK\"\n\nadd GUI workshop on Thursday",
        "Keywords you might consider when specifying DAY IN WEEK are \"this\"(by default) and \"next\".",
        "To make it even more comprehensive, specifying TIME can also be done easily.",
        "Specifying TIME: \"add + TASK + TIME\"\n\nadd Skype interview with Microsoft 10pm.\nRemarks: Both AM/PM and 24:00 formats are recognized",
        "Specifying START TIME/END TIME using '~':\n\"add + TASK + START TIME ~ END TIME\"\n\nadd study marathon 10:00 ~ 22:00\nOR\nadd study marathon 10am ~ 10pm",
        "Nevertheless, it happens from time to time that a TASK doesn't have a specific START TIME/END TIME.\n\nFor instance, 2 hours is required for finishing an assignment at any time before next Monday.",
        "Specifying DURATION using \"for\":\"add + TASK + for + DURATION\"\n\nadd hangout with friends for 4 hours",
        "Want to separate work and personal life? It's convenient to catagorize tons of TASKs using LIST feature.",
        "Specifying LIST using '#': \"add + TASK + # + LIST\"\n\nadd dinner with my parents #family.\nRemarks: Use () if there is space like #(high school friends)",
        "If LIST is not specified, the TASK entered will be stored in default \"Inbox LIST\".",
        "Furthermore, if the LIST specified doesn't exist, it will be created accordingly.",
        "In daily life, there're many things we have to do. Sometimes we can't do them all and want to skip some. Go on with PRIORITY feature.",
        "Specifying PRIORITY using '!':\"add + TASK + ! + PRIORITY\"\n\nadd dating with girl Z !1\nRemarks: 1 for Important, 2 for Normal and 3 for Low priorities respectively.",
        "When PRIORITY is not indicated, TaskMeter takes it as Normal PRIORITY by default.",
        "Beside adding a TASK, deleting and editing could also be done very easily.",
        "Take note that TaskMeter indexes all TASKs it has. This is useful when deleting and editing TASKs",
        "Deleting TASK using \"del\":\n\"del + TASK's index\"\n\ndel 3\nThe 3rd TASK shown is deleted.",
        "Editing TASK using \"edit\":\n\"edit + TASK's index\"\n\nedit 5\nThe details of 5th TASK shown will be displayed on a separate windows for you to edit.",
        "TaskMeter adopts color-coding to make it easier for users to distinguish different type of tasks as follows:\nWHITE = DEFAULT\nRED = IMPORTANT\nPURPLE = OVERDUE\nGREY = COMPLETED."
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