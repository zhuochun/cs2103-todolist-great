package cs2103.t14j1.storage.user;

public class Helps {
    
    private static int index = 0;
    
    // TODO: try to write as many as tips, and tips can formed into paragraphs (change the getHelp() into a better format if you want)
    private static final String[] tips = {
        "Welcome to TaskMeter, You can press global hotkey \"Ctrl + Alt + T\" to open or minize TaskMeter window.",
        "A very useful global hotkey is \"Ctrl + Alt + A\", which will open a quick add box to add task."
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