package bu.mawinw.reinforce;

import java.io.File;
import java.util.Random;

import org.deeplearning4j.arbiter.util.ClassPathResource;
import org.deeplearning4j.examples.download.DownloaderUtility;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LossLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bu.mawinw.util.utility;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import java.io.File;

public class ReinforceModel {

	public ComputationGraph modelA;
	public MultiLayerNetwork modelB;

	private double gamma = 0.95;	//discounted reward used at training time?
	private double epsilon = 0.95;
	private double epsilonDecay = 0.8;
	private double epsilonMin = 0.1;
	private double numAction = 12;
	//public double learningRate = 0.005; //defined at model compile time
	
	
	public ReinforceModel() {
		try {
			//try modelA or modelB here
			modelB = loadModelConv2D();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadModelInception() throws Exception{
		System.out.println("load model . . .");
		String jsonPath = "C:\\Users\\Mawin\\eclipse\\ModelWeightFiles\\infinite_mario_model.json";
		String weightPath = "C:\\Users\\Mawin\\eclipse\\ModelWeightFiles\\infinite_mario_model.hdf5";
        final String jsonFile = new File(jsonPath).getAbsolutePath();
        final String weightFile = new File(weightPath).getAbsolutePath();
		System.out.println("file path OK . . .");
		
        //File file = new File(fileName);
		//importKerasModelAndWeights(String modelJsonFilename, String weightsHdf5Filename)
		ComputationGraph model = KerasModelImport.importKerasModelAndWeights(
				jsonPath, 
				weightPath);

	}
	
	
	
	public MultiLayerNetwork loadModelConv2D() throws Exception{
		//String jsonPath = "C:\\Users\\Mawin\\eclipse\\ModelWeightFiles\\infinite_mario_model.json";
		//String weightPath = "C:\\Users\\Mawin\\eclipse\\ModelWeightFiles\\infinite_mario_model.hdf5";
		String h5Path = "C:\\Users\\Mawin\\eclipse\\ModelWeightFiles\\simple_conv2d_model.h5";

        final String h5File = new File(h5Path).getAbsolutePath();

        MultiLayerNetwork model = KerasModelImport.importKerasSequentialModelAndWeights(h5File);

        model.init();
        //System.out.println(model.summary());
        return model;
	}
	
	public int act(byte[][] state) {

		INDArray myArray = stateToINDArray(state);
		int action = 4;
		//utility.printArray(output);
		//System.out.println(myArray);
		if(Math.random() < epsilon) {
			action = (int) (Math.random()*numAction);
		}
		else {
			int[] output = modelB.predict(myArray);
			action = output[0];
		}
		return action;
	}
	
	public INDArray stateToINDArray(byte[][] state) {
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
	
	public boolean[] actionToBoolean(int a) {
		/*
		//COMPLEX_MOVEMENT
	    //[['NOOP'], ['right'], ['right', 'A'], ['right', 'B'], ['right', 'A', 'B'], ['A'], ['left'], ['left', 'A'], ['left', 'B'], ['left', 'A', 'B'], ['down'], ['up']]
	    */
	    int KEY_LEFT = 0;
	    int KEY_RIGHT = 1;
	    int KEY_DOWN = 2;
	    int KEY_JUMP = 3; //A
	    int KEY_SPEED = 4; //B
		boolean[] ret = new boolean[5]; 
		if (a==0)     /*<noaction>*/	return ret;
		else if (a==1)/* R     */ {	ret[KEY_RIGHT] = true;}
		else if (a==2)/* R J   */ {	ret[KEY_RIGHT] = true;	ret[KEY_JUMP] = true;}
		else if (a==3)/* R   S */ {	ret[KEY_RIGHT] = true;	ret[KEY_SPEED] = true;}
		else if (a==4)/* R J S */ {	ret[KEY_RIGHT] = true;	ret[KEY_JUMP] = true;	ret[KEY_SPEED] = true;}
		else if (a==5)/*   J   */ {	ret[KEY_JUMP] = true;}
		else if (a==6)/* L     */ {	ret[KEY_LEFT] = true;}
		else if (a==7)/* L J   */ {	ret[KEY_LEFT] = true;	ret[KEY_JUMP] = true;}
		else if (a==8)/* L   S */ {	ret[KEY_LEFT] = true;	ret[KEY_SPEED] = true;}
		else if (a==9)/* L J S */ {	ret[KEY_LEFT] = true;	ret[KEY_JUMP] = true;	ret[KEY_SPEED] = true;}
		else if (a==10) {	ret[KEY_DOWN] = true;}
		else if (a==11) {	ret[KEY_RIGHT] = true;}
		return ret;
	}
	public int booleanToAction(boolean[] action) {
		/*
		//COMPLEX_MOVEMENT
	    //[['NOOP'], ['right'], ['right', 'A'], ['right', 'B'], ['right', 'A', 'B'], ['A'], ['left'], ['left', 'A'], ['left', 'B'], ['left', 'A', 'B'], ['down'], ['up']]
	    */
	    int KEY_LEFT = 0;
	    int KEY_RIGHT = 1;
	    int KEY_DOWN = 2;
	    int KEY_JUMP = 3; //A
	    int KEY_SPEED = 4; //B
		boolean[] ret = new boolean[5]; 
		if		(action[KEY_LEFT]==false&&action[KEY_RIGHT]==false&&action[KEY_DOWN]==false&&action[KEY_JUMP]==false&&action[KEY_SPEED]==false)/*noaction*/ return 0;
		else if (action[KEY_LEFT]==false&&action[KEY_RIGHT]==true &&action[KEY_DOWN]==false&&action[KEY_JUMP]==false&&action[KEY_SPEED]==false)/* R     */ return 1;
		else if (action[KEY_LEFT]==false&&action[KEY_RIGHT]==true &&action[KEY_DOWN]==false&&action[KEY_JUMP]==true &&action[KEY_SPEED]==false)/* R J   */ return 2;
		else if (action[KEY_LEFT]==false&&action[KEY_RIGHT]==true &&action[KEY_DOWN]==false&&action[KEY_JUMP]==false&&action[KEY_SPEED]==true )/* R   S */ return 3;
		else if (action[KEY_LEFT]==false&&action[KEY_RIGHT]==true &&action[KEY_DOWN]==false&&action[KEY_JUMP]==true &&action[KEY_SPEED]==true )/* R J S */ return 4;
		else if (action[KEY_LEFT]==false&&action[KEY_RIGHT]==false&&action[KEY_DOWN]==false&&action[KEY_JUMP]==true &&action[KEY_SPEED]==false)/*   J   */ return 5;
		else if (action[KEY_LEFT]==true &&action[KEY_RIGHT]==false&&action[KEY_DOWN]==false&&action[KEY_JUMP]==false&&action[KEY_SPEED]==false)/* L     */ return 6;
		else if (action[KEY_LEFT]==true &&action[KEY_RIGHT]==false&&action[KEY_DOWN]==false&&action[KEY_JUMP]==true &&action[KEY_SPEED]==false)/* L J   */ return 7;
		else if (action[KEY_LEFT]==true &&action[KEY_RIGHT]==false&&action[KEY_DOWN]==false&&action[KEY_JUMP]==false&&action[KEY_SPEED]==true )/* L   S */ return 8;
		else if (action[KEY_LEFT]==true &&action[KEY_RIGHT]==false&&action[KEY_DOWN]==false&&action[KEY_JUMP]==true &&action[KEY_SPEED]==true )/* L J S */ return 9;
		else if (action[KEY_LEFT]==false&&action[KEY_RIGHT]==false&&action[KEY_DOWN]==true &&action[KEY_JUMP]==false&&action[KEY_SPEED]==false) return 10;
		else if (action[KEY_LEFT]==false&&action[KEY_RIGHT]==false&&action[KEY_DOWN]==false&&action[KEY_JUMP]==false&&action[KEY_SPEED]==false) return 11;
		return 11;
	}
	
	public void nextEp() {
		//set epsilon
		if(epsilon * epsilonDecay < epsilonMin)
			epsilon = epsilonMin;
		else
			epsilon = epsilon * epsilonDecay;
	}
}
