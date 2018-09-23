package bwapi.values;

public enum MouseButton {
	M_LEFT(0),
	M_RIGHT(1),
	M_MIDDLE(2),
	M_MAX(3);

	public final int value;

	MouseButton(final int value){
		this.value = value;
	}

}
