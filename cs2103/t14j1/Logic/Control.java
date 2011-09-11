package cs2103.t14j1.Logic;

class Control {
	
	/**
	 * @param input - The input string passed by the GUI
	 * returns a boolean value which denotes whether the desired input operation was 
	 * successful or not 
	 */
	public Boolean processInput (String input) {
		SmartBar.extractCommand();
		executeCommand()
	}
}
