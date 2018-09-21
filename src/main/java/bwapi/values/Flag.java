package bwapi.values;

public enum Flag {
	CompleteMapInformation(0),
	UserInput(1);

	private int value;

	public int getValue(){
		return value;
	}

	Flag(int value){
		this.value = value;
	}
}
