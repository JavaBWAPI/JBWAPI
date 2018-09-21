package bwapi.values;

public enum TextSize {

	Small(0),
	Default(1),
	Large(2),
	Huge(3);

	private int value;

	public int getValue(){
		return value;
	}

	TextSize(int value){
		this.value = value;
	}

}
