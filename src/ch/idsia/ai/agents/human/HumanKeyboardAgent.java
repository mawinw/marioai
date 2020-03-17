package ch.idsia.ai.agents.human;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bu.mawinw.util.utility;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 29, 2009
 * Time: 12:19:49 AM
 * Package: ch.idsia.ai.agents.ai;
 */
public class HumanKeyboardAgent extends KeyAdapter implements Agent
{
    List<boolean[]> history = new ArrayList<boolean[]>();
    private boolean[] Action = null;
    private String Name = "HumanKeyboardAgent";

    public HumanKeyboardAgent()
    {
        this.reset ();
//        RegisterableAgent.registerAgent(this);
    }

    public void reset()
    {
        // Just check you keyboard. Especially arrow buttons and 'A' and 'S'!
        Action = new boolean[Environment.numberOfButtons];
    }

    public boolean[] getAction(Environment observation)
    {

     	byte[][] scene = observation.getLevelSceneObservationZ(0);
     	byte[][] enemy = observation.getEnemiesObservationZ(0);
     	byte[][] merged = observation.getMergedObservationZ(0, 0);
    	float[] enemies = observation.getEnemiesFloatPos();
		float[] realMarioPos = observation.getMarioFloatPos();
		String bitmapOb = observation.getBitmapEnemiesObservation();
		System.out.println("------------------------------------------------------------------");
		//utility.printScene(scene);
		//utility.printScene(enemy);
		System.out.println("MERGED OBSERVATION:");
		utility.printScene(merged);
		System.out.println("\n\n");
		

		System.out.println("ENEMIES FLOST POS:");
		utility.printArray(enemies);
		System.out.println("\n\n");
		

		System.out.println("REAL MARIO POS");
		utility.printArray(realMarioPos);
		System.out.println("\n\n");
		
		
		System.out.println("BITMAP OB");
		System.out.println(bitmapOb);
		System.out.println("\n\n");
        return Action;
    }

    public AGENT_TYPE getType() {        return AGENT_TYPE.HUMAN;    }

    public String getName() {   return Name; }

    public void setName(String name) {        Name = name;    }


    public void keyPressed (KeyEvent e)
    {
        toggleKey(e.getKeyCode(), true);
        System.out.println("sdf");
    }

    public void keyReleased (KeyEvent e)
    {
        toggleKey(e.getKeyCode(), false);
    }


    private void toggleKey(int keyCode, boolean isPressed)
    {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                Action[Mario.KEY_LEFT] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                Action[Mario.KEY_RIGHT] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                Action[Mario.KEY_DOWN] = isPressed;
                break;

            case KeyEvent.VK_S:
                Action[Mario.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                Action[Mario.KEY_SPEED] = isPressed;
                break;
        }
    }

   public List<boolean[]> getHistory () {
       return history;
   }
}
