
public class PowerUp extends Entity{
	
	private boolean presentOnLevel;
	private int presenceCounter;
	private boolean active;
	private int activeCounter;
	
	
	public PowerUp(String mark, Coordinates coordinates, Level level) {
		super(mark, coordinates, level);
	}

	public int incrementpresenceCounter(){
		return ++presenceCounter;
	}
	
	
	public void resetPresenceCounter() {
		presenceCounter = 0;
	}
	
	public int incrementActiveCounter(){
		return ++activeCounter;
	}
	
	public void resetActiveCounter() {
		activeCounter = 0;
	}

	public boolean isPresentOnLevel() {
		return presentOnLevel;
	}
	
	public void showOnLevel() {
		presentOnLevel = true;
	}
	
	public void hideOnLevel() {
		presentOnLevel = false;
	}
	
	public void activate() {
		active = true;
	}
	
	public void deactivate() {
		active = false;
	}
	
	public boolean isActive() {
		return active;
	}
	
	
	public boolean update() {
		
		if(active) {
			incrementActiveCounter();
		}else {
			incrementpresenceCounter();
		}
		if(presenceCounter >= 60) {
			if(isPresentOnLevel()) {
				setCoordinates(getLevel().getRandomCoordinates());	
			}
			hideOnLevel();
			resetPresenceCounter();;
		}
		if(activeCounter>=60) {
			deactivate();
			resetActiveCounter();
			setCoordinates(getLevel().getRandomCoordinates());
			return true;
		}
		return false;
	}
	
	
	
}
