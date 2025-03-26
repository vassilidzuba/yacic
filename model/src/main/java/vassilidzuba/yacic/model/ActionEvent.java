package vassilidzuba.yacic.model;

/**
 * events signaled to listeners.
 */
public enum ActionEvent {
	/**
	 * start of an action 
	 */
	START,
	/**
	 * successful completion of an action.
	 */
	COMPLETE, 
	/**
	 * unsuccessful completion of an action. 
	 */
	FAILS
}
