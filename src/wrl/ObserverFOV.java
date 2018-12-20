package rltut;

/**
 * This interface is used by non-player entities that have a {@linkplain FieldOfView} component to update the player's FieldOfView.
 * 
 * @author Arun Sundaram
 *
 */
public interface ObserverFOV {
	
	/** Updates and returns the {@linkplain FieldOfView} from the entity. */
	public FieldOfView updateFOV();

}
