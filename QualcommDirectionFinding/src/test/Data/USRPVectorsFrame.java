package test.Data;

public class USRPVectorsFrame {

	/**
	 * Assuming order of frame data is A1, A2, B1, B2
	 * 
	 * 10 frames from all 4 Anntena elements
	 * 
	 * JUNK-Guessed/Assumed values
	 */
	private int[][] frames = {
		{1,2,4,2},
		{2,4,2,1},
		{1,2,4,2},
		{1,2,4,2},
		{2,4,2,1},
		{4,2,1,1},
		{2,4,2,1},
		{1,2,4,2},
		{1,2,4,2},
		{1,1,2,4},
	}; 
	
	private int time =-1;
	//LOADS and returns an
	public int[] getVectors(){
		time++;
		if(time<10){
			return null;
		}
		return frames[time];
	}
	
}
