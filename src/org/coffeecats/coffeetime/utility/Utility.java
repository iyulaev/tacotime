/** This static class just holds some useful "utility" methods that get used in several places, 
 * usually to carry out some algorithm-specific calculation.
 */

package org.coffeecats.coffeetime.utility;

public class Utility {
	/** Calculate the direction heading given an X and Y vector; return DIRECTION_NULL if
	 * no heading exists (both vectors equal to 0). All directions returned are from the enum 
	 * in DirectionBitmapMap.
	 * 
	 * @param vector_x The x component of the current heading.
	 * @param vector_y The y component of the current heading.
	 * @return
	 */
	public static int calculateHeadingDirection(int vector_x, int vector_y) {
		if(vector_x == 0 && vector_y == 0) return (DirectionBitmapMap.DIRECTION_NULL);
		
		if(Math.abs(vector_x) > Math.abs(vector_y)) {
			if(vector_x > 0) return DirectionBitmapMap.DIRECTION_EAST;
			else return DirectionBitmapMap.DIRECTION_WEST;
		}
		else {
			if(vector_y > 0) return DirectionBitmapMap.DIRECTION_SOUTH;
			else return DirectionBitmapMap.DIRECTION_NORTH;
		}
	}
}
