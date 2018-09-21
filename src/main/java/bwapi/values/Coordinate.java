package bwapi.values;

public enum Coordinate {
	None(0),
	Screen(1),
	Map(2),
	Mouse(3);

	private int value;

	public int getValue(){
		return value;
	}

	Coordinate(int value){
		this.value = value;
	}
}
