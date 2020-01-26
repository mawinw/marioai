package bu.mawinw.util;

import java.util.Arrays;
import java.lang.Math;

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
					   blockString = "[M]";
				   }
				   else if (blockString == "34") {
					   blockString = "[C]";
				   }
				   System.out.print(padString(blockString));
			   }
			   System.out.println("");
	   
		   }
   }
	public static void printArray(float[] array)
	{
			System.out.println(Arrays.toString(array));
	}
	
	public static float calculateDistance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	public static float calculateDistanceFromCenter(float coinX, float coinY) {
		return (float) Math.sqrt((coinX-11)*(coinX-11)+(coinY-11)*(coinY-11));
	}
	public static String padString(String s) {
		int length = s.length();
		switch(length) {
		case 1:
			return "  "+s+"  ";
		case 2:
			return  " "+s+"  ";
		case 3:
			return  " "+s+" ";
		case 4:
			return   ""+s+" ";
		case 5:
			return   ""+s+"";
			
		}
		return s;
		
	}
	public static String padString(byte s) {
		String bs = java.lang.Integer.toString(s);
		return padString(bs);
		
	}
}
