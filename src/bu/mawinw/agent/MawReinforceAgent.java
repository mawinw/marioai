package bu.mawinw.agent;


import java.util.Arrays;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import competition.cig.robinbaumgarten.astar.AStarSimulatorK;
import competition.cig.robinbaumgarten.astar.LevelScene;
import bu.mawinw.util.utility;
import bu.mawinw.reinforce.Recorder;
import bu.mawinw.reinforce.ReinforceEnvironment;
import bu.mawinw.reinforce.ReinforceModel;

public class MawReinforceAgent implements Agent
{
    private String name;
    private boolean[] action;
    private AStarSimulatorK sim;
    private ReinforceModel model;
    private float lastX = 0;
    private float lastY = 0;
    private Recorder recorder;
    private ReinforceEnvironment RLEnv;
    private boolean isDone = false;

    private int ep = 1;
    private byte[][] state;
    private float[] marioInfo;
    
    private boolean isNew = true;
    
    public MawReinforceAgent()
    {
        setName("MawReinforceAgent");
        reset();
        isNew = false;
    }
    public MawReinforceAgent(String epsilon)
    {
        setName("MawReinforceAgent");
        reset();
        model.setEpsilon(epsilon);
        isNew = false;
    }

    public void reset()
    
    {
        action = new boolean[Environment.numberOfButtons];
        sim = new AStarSimulatorK();
        if(isNew) {
            model = new ReinforceModel();
        }
        else {
        	model.nextEp();
        }
        recorder = new Recorder("Reinforce_Model_Episode",ep);
        RLEnv = new ReinforceEnvironment();
        state = new byte[22][22];
        marioInfo = new float[RLEnv.featureCount+RLEnv.previousActionSize];
    }

    public boolean[] getAction(Environment observation)
    {
    	// This is the main function that is called by the mario environment.
    	// we're supposed to compute and return an action in here.
    	
    	long startTime = System.currentTimeMillis();
    	
    	boolean[] ac = new boolean[Environment.numberOfButtons];
    	ac[Mario.KEY_RIGHT] = true; //mawinw: edit
    	ac[Mario.KEY_SPEED] = true; //mawinw: original= both true
    	
    	// get the environment and enemies from the Mario API
     	byte[][] scene = observation.getLevelSceneObservationZ(0);
     	byte[][] nextState = observation.getCompleteObservation(); //does not work with current simulator
    	float[] enemies = observation.getEnemiesFloatPos();
		float[] realMarioPos = observation.getMarioFloatPos();
		
		if (sim.levelScene.verbose > 0) {
			utility.printScene(nextState);
			utility.printArray(enemies);
			System.out.print("Mario Position: ");
			utility.printArray(realMarioPos);
		}
    	if (sim.levelScene.verbose > 2) System.out.println("Simulating using action: " + sim.printAction(action));
        
    	// Advance the simulator to the state of the "real" Mario state
    	sim.advanceStep(action);   
       		
		// Handle desynchronisation of mario and the environment.
		if (sim.levelScene.mario.x != realMarioPos[0] || sim.levelScene.mario.y != realMarioPos[1])
		{
			// Stop planning when we reach the goal (just assume we're in the goal when we don't move)
			if (realMarioPos[0] == lastX && realMarioPos[1] == lastY) {
				//isDone = true;
			}

			// Set the simulator mario to the real coordinates (x and y) and estimated speeds (xa and ya)
			sim.levelScene.mario.x = realMarioPos[0];
			sim.levelScene.mario.xa = (realMarioPos[0] - lastX) *0.89f;
			if (Math.abs(sim.levelScene.mario.y - realMarioPos[1]) > 0.1f)
				sim.levelScene.mario.ya = (realMarioPos[1] - lastY) * 0.85f;// + 3f;

			sim.levelScene.mario.y = realMarioPos[1];
		}
		
		// Update the internal world to the new information received
		sim.setLevelPart(scene, enemies);
        
		lastX = realMarioPos[0];
		lastY = realMarioPos[1];

		if(sim.levelScene.mario.status == Mario.STATUS_WIN || sim.levelScene.mario.status == Mario.STATUS_DEAD) {
			isDone = true;
		}
		// This is the call to the simulator (where all the planning work takes place)
        //action = sim.optimise();
        int actionNo = model.act(nextState, marioInfo);
		float[] nextMarioInfo = RLEnv.getObservation(observation, sim.levelScene, actionNo);
        action = model.actionToBoolean(actionNo);
        float reward = RLEnv.reward;
        recorder.remember(state, marioInfo, actionNo, reward, nextState,nextMarioInfo, isDone);
        if(isDone) {
        	ep++;
        	isDone = false;
        	String status = "RUNNING";
    		if(sim.levelScene.mario.status == Mario.STATUS_WIN) {
    			status = 	"  WIN  ";
    		}
    		if(sim.levelScene.mario.status == Mario.STATUS_DEAD) {
    			status = 	"  LOSE ";
    		}
    		String maxX = Float.toString(RLEnv.marioMaxX);
    		String coins = Float.toString(sim.levelScene.coinsCollected);
    		String kills = Integer.toString(observation.getKillsTotal());
    		String epsilon = Double.toString(model.epsilon);
        	System.out.println("END! status : "+status+" maxX : "+maxX+" coins : "+coins+" kills : "+kills+" epsilon : "+epsilon);
        }
        state = nextState;
        marioInfo = nextMarioInfo;
        // Some time budgeting, so that we do not go over 40 ms in average.
        sim.timeBudget += 39 - (int)(System.currentTimeMillis() - startTime);
        return action;
    }


    public Agent.AGENT_TYPE getType()
    {
        return Agent.AGENT_TYPE.AI;
    }

    public String getName() {        return name;    }

    public void setName(String Name) { this.name = Name;    }

}
