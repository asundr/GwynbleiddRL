package rltut;

/** 
 * The Updatable interface is implemented by classes that can be updated by the {@linkplain EventOrganizer}.
 * Such classes should keep track of an AP score that is reduced during an update.
 * @author Arun Sundaram
 *
 */
 
public interface Updatable {
	
	/** Returns the current AP score. */
	public int ap();
	
	/** Increases the AP score without notifying the EventOrganizer to reorder the queue.
	 * This should be called by the EventOrganizer once every updatable has exhausted its AP. */
	public void refreshAP();

	/** Allows this Updatable to perform actions that expend AP */
	public void update();
	
	/** Returns {@code true} if updatable should remain in the EventOrganizer for another update. */
	public boolean updatePending();
	
}
