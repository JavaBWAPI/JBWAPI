package bwapi.values;

public enum Shape {
	None(0),
	Text(1),
	Box(2),
	Triangle(3),
	Circle(4),
	Ellipse(5),
	Dot(6);

	private int value;

	public int getValue(){
		return value;
	}

	Shape(int value){
		this.value = value;
	}
}
