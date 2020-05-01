package bu.mawinw.reinforce;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nd4j.linalg.api.ndarray.INDArray;

import bu.mawinw.util.utility;

public class Recorder {
	
	private String fileName = "episode";
	private int episode = 0;
	private List<String> frames;
	private String path="C:\\Users\\Mawin\\Desktop\\Eclispe_Workspace\\marioai_AI_MARP\\marioai\\marioai\\src\\bu\\mawinw\\record\\";

	public Recorder() {
		this.frames = new ArrayList<String>();
	}
	
	public Recorder(String fileName, int episode) {
		this.fileName = fileName;
		this.episode = episode;
		this.frames = new ArrayList<String>();
	}
	
	public void remember(byte[][] state,float[] marioInfo, int action, float reward, byte[][] nextState, float[] nextMarioInfo, boolean done) {
		String d = "0";
		if(done) d = "1";
		String delimeter = "|";
		INDArray aState = utility.stateToINDArray(state);
		INDArray aNextState = utility.stateToINDArray(nextState);
		String sState = aState.toString();
		String sNextState = aNextState.toString();
		sState.replaceAll("\n","");
		sNextState.replaceAll("\n","");
		sState.replaceAll("\r","");
		sNextState.replaceAll("\r","");

		String s = sState+delimeter+Arrays.toString(marioInfo)+delimeter+Integer.toString(action)+delimeter+Float.toString(reward)+delimeter+sNextState+delimeter+Arrays.toString(nextMarioInfo)+delimeter+d;
		//append
		frames.add(s);

		if(done) saveEp();
	}
	
	public void saveEp() {
		//save file
		String saveName = fileName+"_"+Integer.toString(episode)+".csv";
		try {
			System.out.println("write episode record: "+path+saveName);
			FileWriter writer = new FileWriter(path+saveName);
			for(String frame:frames) {
				writer.append(frame).append("\r\n");
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.episode += 1;
		this.frames = new ArrayList<String>();
	}
}