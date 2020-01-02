package ch.idsia.scenarios;

import bu.marp.StrollingAgent;
import competition.cig.robinbaumgarten.AStarAgent;
import competition.cig.sergeykarakovskiy.SergeyKarakovskiy_JumpingAgent;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.human.CheaterKeyboardAgent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import bu.mawinw.agent.SimpleMawAgent;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class Play {

    public static void main(String[] args) {
        //Agent controller = new HumanKeyboardAgent();
        //Agent controller = new CheaterKeyboardAgent();
        //Agent controller = new AStarAgent();
        //Agent controller = new StrollingAgent();
        Agent controller = new SimpleMawAgent();
        if (args.length > 0) {
            controller = AgentsPool.load (args[0]);
            AgentsPool.addAgent(controller);
        }
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        Task task = new ProgressTask(options);
        options.setMaxFPS(false); //default false
        options.setVisualization(true);
        options.setNumberOfTrials(1);
        options.setMatlabFileName("");
        //options.setLevelRandSeed((int) (Math.random () * Integer.MAX_VALUE)); //NORMALLY RANDOM
        options.setLevelRandSeed((int) (1));  //  1 = Project's default seed number 0 = DEFAULT
        options.setLevelDifficulty(1);
        options.setLevelLength(380); //set level length
        task.setOptions(options);

        System.out.println ("Score: " + task.evaluate (controller)[0]);
    }
}