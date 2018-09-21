package bwapi.values;

public enum Latency {
	SinglePlayer(2),
	LanLow(5),
	LanMedium(7),
	LanHigh(9),
	BattlenetLow(14),
	BattlenetMedium(19);

	private int value;

	public int getValue(){
		return value;
	}

	Latency(int value){
		this.value = value;
	}

}
