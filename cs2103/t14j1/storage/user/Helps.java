package cs2103.t14j1.storage.user;

public class Helps {

	private static int index = 0;

	// TODO: try to write as many as tips, and tips can formed into paragraphs
	// (change the getHelp() into a better format if you want)
	private static final String[] tips = {
			"Welcome to TaskMeter, press global hotkey \"Ctrl + Alt + T\" to open or minimize TaskMeter window.",
			"Alternatively, typing \"add + spacebar + Task Name\" onto TaskMeter smartbar also works fine.",
			"In addition to Task Name, Location, Date/Time, Duration, List and Priority of a task can be specified.",
			"TaskMeter awares it's important to know the location of your appointment; An easy way to do so is provided (next tip).",
			"Specifying Location using '@': \"add jogging @home\". Use () if there is space like @(my home)",
			"",
			"Specifying Date/Time",
			"It happens from time to time that a Task doesn't have a specific time period. For instance, 2 hours is required for finishing an assignment at any time before next Monday.",
			"Specifying Duration",
			"Want to separate work and personal life? It's convenient to catagorize tons of Tasks using List feature.",
			"Specifying List using '#': \"add dinner with my parents #family\". Again, use () if there is space like #(high school friends)",
			"If List is not specified, the Task entered will be stored in default \"Inbox List\".",
			"Furthermore, if the List specified doesn't exist, it will be created accordingly.",
			"In daily life, there're many things we have to do. Sometimes we can't do them all and want to skip some. Go on with Priority feature.",
			"Specifying Priority using '!': \"add dating with girl Z !1\" 1 for Important, 2 for Normal and 3 for Low priorities respectively.",
			"When Priority is not indicated, TaskMeter takes it as Normal Priority by default.",
			"Beside adding a Task, searching, editing and deleting could also be done very easily."
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