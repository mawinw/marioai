package bu.mawinw.util;

import java.util.Arrays;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.lang.Math;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class utility {
	
	public static int verbose = 0;
	public static void printScene(byte[][] scene)
	{
		if (verbose < 1) return;
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
		if (verbose < 1) return;
			System.out.println(Arrays.toString(array));
	}

	public static void printArray(int[] array)
	{
		System.out.println(Arrays.toString(array));
	}
	
	public static float calculateDistance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	public static float calculateDistanceFromCenter(float coinX, float coinY) {
		return (float) Math.sqrt((coinX-11)*(coinX-11)+(coinY-11)*(coinY-11));
	}
	
	public static boolean detectCoinCollision(float mx, float my, float coinX, float coinY, boolean bigMario) {
		float marioSizeX = 16;
		float marioSizeY = bigMario ? 32:16;
		float coinSizeX = 32;
		float coinSizeY = 32;
		float mx1 = mx;
		float mx2 = mx + marioSizeX;
		float cx1 = coinX;
		float cx2 = coinX + coinSizeX;
		float my1 = my;
		float my2 = my + marioSizeY;
		float cy1 = coinY;
		float cy2 = coinY + coinSizeY;
		if ((mx2 < cx1) || (cx2 < mx1) || (my2 < cy1) || (cy2 < my1)) {
			return false;
		}
		return true;
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
	public static INDArray stateToINDArray(byte[][] state) {
        int nRows = 22;
        int nColumns = 22;
        int nDim = 1;
        int miniBatchSize = 1;
        INDArray myArray = Nd4j.zeros(miniBatchSize, nDim, nRows, nColumns);

		   for(int i = 0; i < 22; i++)
		   {
			   for(int j = 0; j < 22; j++)
			   {
				   int block = state[i][j];
				   
				   if((i==10 && j==10)||(i==11 && j==10)) {
					   block = 99; //mario
				   }
				   myArray.putScalar(0,0, i, j, block);
			   }
		   }
		return myArray;
	}
}
