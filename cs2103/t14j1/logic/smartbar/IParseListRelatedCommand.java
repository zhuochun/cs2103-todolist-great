package cs2103.t14j1.logic.smartbar;

/**
 * @author SongYY
 * Command Involves:
 * 		MOVE_TASK, 		ADD_LIST, 		EDIT_LIST, 		RENAME_LIST, 
 *  	DELETE_LIST, 	SWITCH_LIST,	DISPLAY_LISTS
 */
public interface IParseListRelatedCommand {
	public String extractListName();
	public String extractNewListName();
}
