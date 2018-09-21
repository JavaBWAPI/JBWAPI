package bwapi.values;

public enum MouseButton {
	M_LEFT(0),
	M_RIGHT(1),
	M_MIDDLE(2),
	M_MAX(3);

	private int value;

	public int getValue(){
		return value;
	}

	MouseButton(int value){
		this.value = value;
	}

}
