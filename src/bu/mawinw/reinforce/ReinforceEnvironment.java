package bu.mawinw.reinforce;

import bu.mawinw.util.utility;
import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.sprites.Mario;

public class ReinforceEnvironment {

	private float mx;	//mario x
	private float my;	//mario y
	private float mxs;	//mario x speed
	private float mys;	//mario y speed
	private float mxa;	//mario x acceleration
	private float mya;	//mario y acceleration
	private int mstate;	//mario state (small,big,fire)
	private int isRun; //mario x speed magnitude > thresh
	
	private float runTime;
	private int wasOnGround;
	private int onGround;
	private int mayJump;
	private int ducking;
	private int sliding;
	private float jumpTime;
	private float xJumpSpeed;
	private float yJumpSpeed;
	private int canShoot;
	
	private int shootAble; //mario fire && fireball in state <= 2 && prev action not run
	private int jumpAble; //mario is jump able
	private float coinX; //nearest coin x
	private float coinY; //nearest coin y
	private float relaCoinX; //relatively
	private float relaCoinY; //relatively
	private float enemyX;
	private float enemyY;
	private float relaEnemyX;
	private float relaEnemyY;
	private int coinCount;
	private int killCount;
	private int[] mactions; //mario previous actions
	
	private int facing;
	private int enemiesJumpedOn;
	private int enemiesKilled;
	private int coinsCollected;
	private int powerUpsCollected;
	private int otherTricks;
	
	private int isMarioCarrying;
	public float marioMaxX = 32;
	public float reward = 0;
	
	public float[] marioInfo;
	
	public int featureCount = 37;
	public int previousActionSize = 10;
	
	
	public ReinforceEnvironment() {
		reward = 0;
		marioMaxX = 32;
		
		marioInfo = new float[featureCount + previousActionSize];
		marioInfo[0] = mx = 32;	//mario x
		marioInfo[1] = my = 0;	//mario y
		marioInfo[2] = mxs = 0;	//mario x speed
		marioInfo[3] = mys = 0;	//mario y speed
		marioInfo[4] = mxa = 0;	//mario x acceleration
		marioInfo[5] = mya = 0;	//mario y acceleration
		marioInfo[6] = mstate = 2;	//mario state (small,big,fire)
		marioInfo[7] = isRun = 0; //mario x speed magnitude > thresh
		
		marioInfo[8] = runTime = 0; //
		marioInfo[9] = wasOnGround = 0; //
		marioInfo[10] = onGround = 0; //
		marioInfo[11] = mayJump = 0; //
		marioInfo[12] = ducking = 0; //
		marioInfo[13] = sliding = 0; //
		marioInfo[14] = jumpTime = 0; //
		marioInfo[15] = xJumpSpeed = 0; //
		marioInfo[16] = yJumpSpeed = 0; //
		marioInfo[17] = canShoot = 0; 
		
		marioInfo[18] = shootAble = 1; //mario fire && fireball in state <= 2 && prev action not run
		marioInfo[19] = jumpAble = 1; //mario is jump able
		marioInfo[20] = coinX = -1; //nearest coin x
		marioInfo[21] = coinY = -1; //nearest coin y
		marioInfo[22] = relaCoinX = -1; //relatively
		marioInfo[23] = relaCoinY = -1; //relatively
		marioInfo[24] = enemyX = -1;
		marioInfo[25] = enemyY = -1;
		marioInfo[26] = relaEnemyX = -1;
		marioInfo[27] = relaEnemyY = -1;
		marioInfo[28] = coinCount = 0;
		marioInfo[29] = killCount = 0;
		
		marioInfo[30] = facing = 1;
		marioInfo[31] = enemiesJumpedOn = 0;
		marioInfo[32] = enemiesKilled = 0;
		marioInfo[33] = coinsCollected = 0;
		marioInfo[34] = powerUpsCollected = 0;
		marioInfo[35] = otherTricks = 0;
		marioInfo[36] = isMarioCarrying = 0;
		
		mactions = new int[previousActionSize]; //mario previous actions
		
		for(int i=0;i<previousActionSize;i++) {
			marioInfo[featureCount+i] = mactions[i]; //31-40
		}
	}
	
	private void updateInfo() {

		marioInfo[0] = mx;	//mario x
		marioInfo[1] = my;	//mario y
		marioInfo[2] = mxs;	//mario x speed
		marioInfo[3] = mys;	//mario y speed
		marioInfo[4] = mxa;	//mario x acceleration
		marioInfo[5] = mya;	//mario y acceleration
		marioInfo[6] = mstate;	//mario state (small,big,fire)
		marioInfo[7] = isRun; //mario x speed magnitude > thresh
		
		marioInfo[8] = runTime; //
		marioInfo[9] = wasOnGround; //
		marioInfo[10] = onGround; //
		marioInfo[11] = mayJump; //
		marioInfo[12] = ducking; //
		marioInfo[13] = sliding; //
		marioInfo[14] = jumpTime; //
		marioInfo[15] = xJumpSpeed; //
		marioInfo[16] = yJumpSpeed; //
		marioInfo[17] = canShoot; 
		
		marioInfo[18] = shootAble; //mario fire && fireball in state <= 2 && prev action not run
		marioInfo[19] = jumpAble; //mario is jump able
		marioInfo[20] = coinX; //nearest coin x
		marioInfo[21] = coinY; //nearest coin y
		marioInfo[22] = relaCoinX; //relatively
		marioInfo[23] = relaCoinY; //relatively
		marioInfo[24] = enemyX;
		marioInfo[25] = enemyY;
		marioInfo[26] = relaEnemyX;
		marioInfo[27] = relaEnemyY;
		marioInfo[28] = coinCount; //coins in screen
		marioInfo[29] = killCount; 
		
		marioInfo[30] = facing;
		
		marioInfo[31] = enemiesJumpedOn;
		marioInfo[32] = enemiesKilled;
		marioInfo[33] = coinsCollected;
		marioInfo[34] = powerUpsCollected;
		marioInfo[35] = otherTricks;
		
		marioInfo[36] = isMarioCarrying;
		for(int i=0;i<previousActionSize;i++) {
			marioInfo[featureCount+i] = mactions[i]; //20-29
		}
	}
	
	public float[] getObservation(Environment observation, LevelScene levelScene, int action) {

    	calculateReward(observation, levelScene);
		float[] marioXY = observation.getMarioFloatPos();
		mxa = (marioXY[0]-mx)-mxs;
		mya = (marioXY[1]-my)-mys;
		mxs = marioXY[0]-mx;
		mys = marioXY[1]-my;
		mx = marioXY[0];
		my = marioXY[1];
		byte[][] completeScene = observation.getCompleteObservation(); //does not work with current simulator

    	Mario mario = levelScene.mario;
    	mstate = observation.getMarioMode();
    	isRun = (mxs > 2 || mxs < -2)? 1:0;
    	
    	runTime = mario.runTime;
    	wasOnGround = mario.wasOnGround? 1 : 0;
    	onGround = observation.isMarioOnGround()? 1 : 0;
    	mayJump = observation.mayMarioJump()? 1 : 0;
    	ducking = mario.ducking? 1 : 0;
    	sliding = mario.sliding? 1 : 0;
    	jumpTime = mario.jumpTime;
    	xJumpSpeed = mario.xJumpSpeed;
    	yJumpSpeed = mario.yJumpSpeed;
    	canShoot = mario.canShoot? 1 : 0;
    	
    	shootAble = checkIsShootable(mstate, mx, my, completeScene);
    	jumpAble = checkIsJumpAble();
    	
    	float[] nearestCoinInfo = scanForNearest(34, mx, my, completeScene);
    	coinX = nearestCoinInfo[2];
    	coinY = nearestCoinInfo[3];
    	relaCoinX = marioXY[0] - coinX;
    	relaCoinY = marioXY[1] - coinY;
    	
    	float[] nearestEnemyInfo = scanForNearestFloat(mx, my, observation.getEnemiesFloatPos());
    	enemyX = nearestEnemyInfo[2];
    	enemyY = nearestEnemyInfo[3];
    	relaEnemyX = marioXY[0] - enemyX;
    	relaEnemyY = marioXY[1] - enemyY;
    	

        enemiesJumpedOn = observation.getKillsByStomp();
        enemiesKilled = observation.getKillsByFire();
        coinsCollected = levelScene.coinsCollected;
        powerUpsCollected = levelScene.powerUpsCollected;
        otherTricks = observation.getKillsByShell();
        
        coinCount = coinsCollected;
    	killCount = observation.getKillsTotal();
    	
    	facing = mario.facing;

    	isMarioCarrying = observation.isMarioCarrying()? 1 : 0;
    	
    	for(int i=1;i<previousActionSize;i++) {
    		mactions[i] = mactions[i-1];
    	}
    	mactions[0] = action;

    	updateInfo();
		return marioInfo;
	}

	private void calculateReward(Environment observation, LevelScene levelScene) {
		reward = -1;
		float marioX = levelScene.mario.x;
		if(marioX > marioMaxX) {
			reward += (marioX-marioMaxX);
			marioMaxX = marioX;
		}
		else reward -= 1;
		if(levelScene.mario.y >=192) {
			reward -= 2;
		}
        enemiesJumpedOn = observation.getKillsByStomp();
        enemiesKilled = observation.getKillsByFire();
        coinsCollected = levelScene.coinsCollected;
        powerUpsCollected = levelScene.powerUpsCollected;
        otherTricks = observation.getKillsByShell();

    	if((enemiesJumpedOn+enemiesKilled+otherTricks)-killCount > 0) {
    		reward += (enemiesJumpedOn+enemiesKilled+otherTricks-killCount)*500;
    	}
    	if(coinsCollected-coinCount > 0) {
    		reward += (coinsCollected-coinCount)*10;
    	}
    	if(observation.getMarioMode() < mstate) {
    		reward -= 10;
    	}
    	if(observation.getMarioMode() > mstate) {
    		reward += 10;
    	}
    	
    	if(((ch.idsia.mario.engine.MarioComponent) observation).getMarioStatus() == Mario.STATUS_WIN) {
    		reward += 20;
    	}
    			
    	else if(((ch.idsia.mario.engine.MarioComponent) observation).getMarioStatus() == Mario.STATUS_DEAD) {
    		reward -= 10;
		}
	}
	
	private int checkIsJumpAble() {
		int ret = 0;
		return ret;
	}
	
	private int checkIsShootable(int obsMState, float mx, float my, byte[][] completeScene) {
		int ret = 1;
		if(obsMState <= 1) {
			return 0;
		}
		float fireBallCount = scanForNearest(25, mx, my, completeScene)[4];
		ret = fireBallCount>=2? 0 : 1;
		return ret;
	}
	
	private float[] scanForNearest(int object, float mx, float my, byte[][] completeScene) {
    	float[] ret = new float[5]; //obj x, obj y
    	ret[0] = -1;
    	ret[1] = -1;
    	ret[2] = -1;
    	ret[3] = -1;
    	ret[4] = 0; //count
    			
    	int marioBlockX = (int) (mx/16);
    	int marioBlockY = (int) (my/16);
    	
		int minX = 0;
    	int maxX = 0;
    	int minY = 22;
    	int maxY = 22;
    	float minDistance = utility.calculateDistance(minX, minX, maxX, maxY);

        for (int i = minY; i < maxY; i++)
        {
            for (int j = minX; j < maxX; j++) { // I want to scan by x first
        		if(completeScene[j][i] != object) {
        		}
        		else {
        	    	ret[4]++;
	        		float d = utility.calculateDistance(11, 10, j*16, i*16); //mario central to item
	        		if (d < minDistance) {
	        			minDistance = d;
	        			ret[0] = (float) j;
	        			ret[1] = (float) i;
	        			ret[2] = (float) (j-11+marioBlockX)*16;
	        			ret[3] = (float) (i-10+marioBlockY)*16;
	        		}
        		}	
            }
        }
        return ret;
	}
	private float[] scanForNearestFloat(float mx, float my, float[] enemies) {
    	float[] ret = new float[5]; 
    	ret[0] = -1;
    	ret[1] = -1;
    	ret[2] = -1;
    	ret[3] = -1;
    	ret[4] = 0; //count
    			
    	int marioBlockX = (int) (mx/16);
    	int marioBlockY = (int) (my/16);
    	
    	float minDistance = 100000;

        for (int i = 0; i < enemies.length; i+=3) {
        	float etype = enemies[0];
        	float ex = enemies[1];
        	float ey = enemies[2];
	    	ret[4]++;
    		float d = utility.calculateDistance(mx, my, ex, ey);
    		if (d < minDistance) {
    			minDistance = d;
    			ret[0] = (float) ex/16;
    			ret[1] = (float) ey/16;
    			ret[2] = (float) ex;
    			ret[3] = (float) ey;
    		}

        }


        return ret;
	}
}
