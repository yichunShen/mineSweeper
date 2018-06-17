/** 
 * File: pair.java
 * <p>Mr. Anandarajan
 * <br/>ICS4U1
 * <br/>May 7, 2018
 * 
 * <p>Minesweeper Assignment
 * <br/>Description: The class which describes object pair used in mineSweeper.
 * It contains x and y coordinates of a unit in a map.
 * 
 * @author Benny Shen
 * @author Martin Xu
 */
public class pair implements Comparable<pair>{
	private int a,b;//declare int variable to store the coordinates
	pair(int x,int y){
		a=x;
		b=y;
	}//end constructor
	/**
	 * Getter method of x-coordinate.
	 * @return x-coordinate
	 */
	public int getA(){
		return a;
	}//end method
	/**
	 * Getter method of y-coordinate.
	 * @return y-coordinate
	 */
	public int getB(){
		return b;
	}//end method
	/**
	 * Setter method of x-coordinate.
	 * @param A2 new x-coordinate of the unit
	 */
	public void setA(int A2){
		a = A2;
	}//end method
	/**
	 * Setter method of y-coordinate.
	 * @param B2 new y-coordinate of the unit
	 */
	public void setB(int B2){
		b = B2;
	}//end method
	/**
	 * compareTo method used in priority queue in revealUnit() in mineSweeper
	 */
	public int compareTo(pair p) {
		if(this.a<p.a)
			return -1;
		else if(this.a==p.a){
			if(this.b<p.b)
				return -1;
			else
				return 1;
		}//end if
		else
			return 1;
	}//end method
}//end class
