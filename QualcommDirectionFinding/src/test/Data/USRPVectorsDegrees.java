package test.Data;

/*Assumeing we are not living with just straight Frame data, 
but actual computed corrdinates in a 360 degreee formate
*/
public class USRPVectorsDegrees extends USRPVectorsFrame {
/**
 * Degree numbers provided by the C/C++ scripts,
 *  illustrating a smooth number transition and returning back to 0
 *  
 *  35 total Entries
 */

	private int[] locationSmooth = {
			0,10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,
			170,160,150,140,130,120,110,100,90,80,70,60,50,40,30,20,10,0
			
		}; 
	/**
	 * Degree numbers provided by the C/C++ scripts,
	 *  illustrating a jagged or rapidly changing number 
	 *  transition and returning back to 0
	 *  
	 *  35 Entries total
	 */
	private int[] locationJagged = {
			0,5,15,23,30,50,45,46,75,90,95,120,127,135,140,150, 175, 180,
			175,150,140,135,127,120,95,90,75,46,45,50,30,23,15,5,0,
		}; 
	
	//LOADs data into the databae

	public void loadDB(int smOrJa){//1 or 0
		if(smOrJa >0){
			//load locationSmooth
		}
		else{
			//load locationJagaged
		}

	}
}
