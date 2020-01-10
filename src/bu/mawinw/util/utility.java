package bu.mawinw.util;

import java.util.Arrays;

public class utility {
	
	public static void printScene(byte[][] scene)
	{
		   for(int i = 0; i < 22; i++)
		   {
			   for(int j = 0; j < 22; j++)
			   {
				   int block = scene[i][j];
				   String blockString = java.lang.Integer.toString(block);
				   
				   if((i==10 && j==10)||(i==11 && j==10)) {
					   blockString = "["+blockString+"]";
					   int length =  blockString.length();
					   if(block == 0) {
						   blockString = " "+blockString+" ";
					   }					   
					   else {

						   for(int k = 0; k < 5-length; k++) {
							   blockString += " ";
						   }
					   }
				   }
				   else {
					   int length =  blockString.length();
					   if(block == 0) {
						   blockString = "  "+blockString+"  ";
					   }
					   else {
						   for(int k = 0; k < 5-length; k++) {
							   blockString += " ";
						   }
					   }
				   }
				   System.out.print(blockString);
			   }
			   System.out.println("");
		   }
	}
	public static void printArray(float[] array)
	{
			System.out.println(Arrays.toString(array));
	}
}
