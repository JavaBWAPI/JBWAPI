package bwapi.utils;

public class BWMath {

	// usage: y = swap(x, x=y);
	private static int swap(int a, int b) {
		return a;
	}

	public static int getApproxDistance(int x1, int y1, int x2, int y2) {
		int min = Math.abs(x1 - x2);
		int max = Math.abs(y1 - y2);
		if ( max < min ) max = swap(min, min=max);

		if ( min < (max >> 2)) return max;

		int minCalc = (3*min) >> 3;
		return (minCalc >> 5) + minCalc + max - (max >> 4) - (max >> 6);
	}
}
