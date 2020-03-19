package ch.idsia.mario.engine.level;

import ch.idsia.mario.engine.sprites.Enemy;
import ch.idsia.utils.ParameterContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class LevelGenerator
{
    public static final int TYPE_OVERGROUND = 0;
    public static final int TYPE_UNDERGROUND = 1;
    public static final int TYPE_CASTLE = 2;

    private static Random levelSeedRandom = new Random();
    public static long lastSeed;
    public static final int LevelLengthMinThreshold = 50;

    public static Level createLevel(int width, int height, long seed, int difficulty, int type)
    {
        LevelGenerator levelGenerator = new LevelGenerator(width, height);
        //
        //
        return levelGenerator.createLevel(seed, difficulty, type);
        //return levelGenerator.createLevel(seed, difficulty, type); //edited
    }

    private int width;
    private int height;
    Level level = new Level(width, height);
    Random random;

    private static final int ODDS_STRAIGHT = 0;
    private static final int ODDS_HILL_STRAIGHT = 1;
    private static final int ODDS_TUBES = 2;
    private static final int ODDS_JUMP = 3;
    private static final int ODDS_CANNONS = 4;
    private int[] odds = new int[5];
    private int totalOdds;
    private int difficulty;
    private int type;

    private LevelGenerator(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    private Level createCustomizedLevel(long seed, int difficulty, int type) {
    	Level level = new Level(width, height); 
    	this.type = type;
        ///////////// EDITED
        odds[ODDS_STRAIGHT] = 1;
        odds[ODDS_HILL_STRAIGHT] = 1;
        odds[ODDS_TUBES] = 1;
        odds[ODDS_JUMP] = 1;
        odds[ODDS_CANNONS] = 1;
        ////////////

        if (type != LevelGenerator.TYPE_OVERGROUND) //NOT OVERGROUND = no Hills
        {
            odds[ODDS_HILL_STRAIGHT] = 0;
        }

        for (int i = 0; i < odds.length; i++)
        {
            if (odds[i] < 0) odds[i] = 0;
            totalOdds += odds[i];
            odds[i] = totalOdds - odds[i];
        }

        lastSeed = seed;
        level = new Level(width, height);
        random = new Random(seed);

        
        int length = 0;
        length += buildStraight(0, level.width, true); //build all straight
        
        while (length < level.width - 64) //build zone until near finish line
        {
            //length += buildZone(length, level.width - length); //fix this function
            length += buildZoneSequence(length, level.width - length); 
        }

        // build finish line
        int floor = height - 1 - random.nextInt(4); //height over ground 1-4 pixel

        level.xExit = length + 8;
        level.yExit = floor;

        for (int x = length; x < level.width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
            }
        }

        if (type == LevelGenerator.TYPE_CASTLE || type == LevelGenerator.TYPE_UNDERGROUND)
        {
            int ceiling = 0;
            int run = 0;
            for (int x = 0; x < level.width; x++)
            {
                if (run-- <= 0 && x > 4)
                {
                    ceiling = random.nextInt(4);
                    run = random.nextInt(4) + 4;
                }
                for (int y = 0; y < level.height; y++)
                {
                    if ((x > 4 && y <= ceiling) || x < 1)
                    {
                        level.setBlock(x, y, (byte) (1 + 9 * 16));
                    }
                }
            }
        }

        fixWalls();
        return level;
    }

    private Level createLevel(long seed, int difficulty, int type)
    {
        this.type = type;
        this.difficulty = difficulty;
        odds[ODDS_STRAIGHT] = 20;
        odds[ODDS_HILL_STRAIGHT] = 10;
        odds[ODDS_TUBES] = 2 + 1 * difficulty;
        odds[ODDS_JUMP] = 2 * difficulty;
        odds[ODDS_CANNONS] = -10 + 5 * difficulty;
        ///////////// EDITED
        odds[ODDS_STRAIGHT] = 1;
        odds[ODDS_HILL_STRAIGHT] = 1;
        odds[ODDS_TUBES] = 0;
        odds[ODDS_JUMP] = 0;
        odds[ODDS_CANNONS] = 0;
        ////////////

        if (type != LevelGenerator.TYPE_OVERGROUND)
        {
            odds[ODDS_HILL_STRAIGHT] = 0;
        }

        for (int i = 0; i < odds.length; i++)
        {
            if (odds[i] < 0) odds[i] = 0;
            totalOdds += odds[i];
            odds[i] = totalOdds - odds[i];
        }
        //type = 0; //mawinw: 1,2 have ceiling
        lastSeed = seed;
        level = new Level(width, height);
        random = new Random(seed);

        int length = 0;
        length += buildStraight(0, level.width, true);
        while (length < level.width - 128)
        {
            //length += buildZone(length, level.width - length);
            length += buildZoneSequence(length, level.width - length); 
        }

        int floor = height - 2;

        level.xExit = length + 8;
        level.yExit = floor;

        for (int x = length; x < level.width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
            }
        }

        if (type == LevelGenerator.TYPE_CASTLE || type == LevelGenerator.TYPE_UNDERGROUND)
        {
            int ceiling = 0;
            int run = 0;
            for (int x = 0; x < level.width; x++)
            {
                if (run-- <= 0 && x > 4)
                {
                    ceiling = random.nextInt(4);
                    run = random.nextInt(4) + 4;
                }
                for (int y = 0; y < level.height; y++)
                {
                    if ((x > 4 && y <= ceiling) || x < 1)
                    {
                        level.setBlock(x, y, (byte) (1 + 9 * 16));
                    }                                                        
                    
                }
            }
        }
        
        addAllCoin();
        addBrick();
        fixWalls(); //mawinw: here
        return level;
    }

    private int buildZone(int x, int maxLength)
    {
        int t = random.nextInt(totalOdds);
        int type = 0;
        for (int i = 0; i < odds.length; i++)
        {
            if (odds[i] <= t)
            {
                type = i;
            }
        }

        switch (type)
        {
            case ODDS_STRAIGHT:
                return buildStraight(x, maxLength, false);
            case ODDS_HILL_STRAIGHT:
                return buildHillStraight(x, maxLength);
            case ODDS_TUBES:
                return buildTubes(x, maxLength);
            case ODDS_JUMP:
                return buildJump(x, maxLength);
            case ODDS_CANNONS:
                return buildCannons(x, maxLength);
        }
        return 0;
    }
    
    private int buildZoneSequence(int x, int maxLength)
    {
    	type = 0;
    	int l = 0;
        l += buildStraight(x, maxLength, true, 10, 2);
    	addEnemy(x+l-10+12, 3, Enemy.ENEMY_GREEN_KOOPA);
        l += buildHillStraight(x+l, maxLength-l, 24, 2, 1, 2, 18);
        /*
        public static final int ENEMY_GREEN_KOOPA = 1;
        public static final int ENEMY_RED_KOOPA = 0;
        public static final int ENEMY_GOOMBA = 2;
        public static final int ENEMY_SPIKY = 3;
        public static final int ENEMY_FLOWER = 4;
        */
        for(int i = 3; i < 12; i++) {
        	addEnemy(x+l+i-16, 5, Enemy.ENEMY_GREEN_KOOPA);
        }
        l += buildStraight(x+l, maxLength-l, true, 6, 2);
        l += buildHillStraight(x+l, maxLength-l, 10, 2, 0, 4, 7);
        l += buildStraight(x+l, maxLength-l, true, 64, 2);
    	addEnemy(x+l-64+43, 3, Enemy.ENEMY_GREEN_KOOPA);
        

        l += buildStraight(x+l, maxLength-l, true, 7, 3);
        l += buildStraight(x+l, maxLength-l, true, 6, 4);
        l += buildStraight(x+l, maxLength-l, true, 3, 5);
        l += buildStraight(x+l, maxLength-l, true, 18, 6);
    	addEnemy(x+l-18+15, 7, Enemy.ENEMY_GREEN_KOOPA);
    	
        l += buildStraight(x+l, maxLength-l, true, 20, 2);
        l += buildTubes(x+l, maxLength-l, 2, 2, 0, 2, true);
        l += buildTubes(x+l, maxLength-l, 6, 2, 0, 3, true);
        l += buildStraight(x+l, maxLength-l, true, 10, 2);
        l += buildHillStraight(x+l, maxLength-l, 10, 2, 0, 4, 7);
        l += buildHillStraight(x+l-5, maxLength-l, 10, 2, 0, 7, 7);
        l += buildHillStraight(x+l-11, maxLength-l, 13, 2, 0, 4, 9);
        l -= 11;
        l += buildStraight(x+l, maxLength-l, true, 13, 2);
        l += buildHillStraight(x+l, maxLength-l, 15, 2, 0, 6, 12);
        l += buildHillStraight(x+l-8, maxLength-l, 18, 2, 0, 3, 10);
        l -= 15;
        l += buildJump(x+l, maxLength-l, 15, 2, 0, 2);
        l += buildTubes(x+l, maxLength-l, 11, 2, 0, 2, true);
        l += buildTubes(x+l, maxLength-l, 19, 2, 0, 3, true);
        l += buildTubes(x+l, maxLength-l, 3,  2, 0, 2, true);
        l += buildTubes(x+l, maxLength-l, 9,  2, 0, 3, false);
        l += buildStraight(x+l, maxLength-l, true, 10, 2);
        l += buildHillStraight(x+l, maxLength-l, 18, 2, 0, 3, 10);
        return l;
        /*
        switch (type)
        {
            case ODDS_STRAIGHT:
                return buildStraight(x, maxLength, false);
            case ODDS_HILL_STRAIGHT:
                return buildHillStraight(x, maxLength);
            case ODDS_TUBES:
                return buildTubes(x, maxLength);
            case ODDS_JUMP:
                return buildJump(x, maxLength);
            case ODDS_CANNONS:
                return buildCannons(x, maxLength);
        }
        return 0;
        */
    }

    private int addAllCoin() { //     											*   *						  *  *
    	ArrayList<Integer> coinX = new ArrayList<Integer>((Arrays.asList(11,18, 48, 50,60,61,62,63,64,65,66, 71, 72, 92, 93, 94, 95, 95, 100, 107, 108, 109, 109, 110, 111, 118)));
    	ArrayList<Integer> coinY = new ArrayList<Integer>((Arrays.asList(4,  3,  5, 5, 7, 7,  7, 7, 7, 7,7,  5,  5,  3,  4,  4,  4,  5,   4,   5,   6,  6,    7,   6,   5,   4)));
    	for(int i = 0; i<coinX.size(); i++) {
    			addCoin(coinX.get(i),15-coinY.get(i));
    	}
    	coinX = new ArrayList<Integer>((Arrays.asList(128,130,130,133,165,170)));
    	coinY = new ArrayList<Integer>((Arrays.asList(  4,  5,  6,  4,  8,  9)));
    	for(int i = 0; i<coinX.size(); i++) {
    			addCoin(coinX.get(i),15-coinY.get(i));
    	}											//	   *   *   *     										 
    	coinX = new ArrayList<Integer>((Arrays.asList(190,210,211,212,214,224,225,226,229,230,231, 236, 237, 238, 261,261,262,262,263,263,264,264,265,265,266,266)));
    	coinY = new ArrayList<Integer>((Arrays.asList(  4,  4,  4,  4,  3,  7,  7,  7, 10, 10, 10,   7,   7,   7,   9, 10,  9, 10,  9, 10,  9, 10,  9, 10,  9, 10)));
    	for(int i = 0; i<coinX.size(); i++) {
    			addCoin(coinX.get(i),15-coinY.get(i));
    	}											//	   			*   *   *   *   *   *   *   *   *    S   S   S   S   S   S   S   S   S   S   S   S										 
    	coinX = new ArrayList<Integer>((Arrays.asList(280,286,294, 328,328,328,329,329,329,330,330,330, 332,333,334,335,336,337,338,339,340,341,342,343)));
    	coinY = new ArrayList<Integer>((Arrays.asList(  4,  5,  5,   5,  6,  7,  5,  6,  7,  5,  6,  7,   6,  7,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8)));
    	for(int i = 0; i<coinX.size(); i++) {
    			addCoin(coinX.get(i),15-coinY.get(i));
    	}
    	
    	return 0;
    }
    private int addBrick() {
    	//22 = Pup block, 23 = Coin block
    	//16 = brick 17 = coin brick 18 = pup brick
    	ArrayList<Integer> coinX = new ArrayList<Integer>((Arrays.asList(48, 50, 71, 72,210,211,212 )));
    	ArrayList<Integer> coinY = new ArrayList<Integer>((Arrays.asList( 5,  5,  5,  5,  5,  5,  5 )));
    	ArrayList<Integer> block = new ArrayList<Integer>((Arrays.asList(23, 23, 23, 22, 23, 22, 23 )));
    	for(int i = 0; i<coinX.size(); i++) {
    			setBlocks(coinX.get(i),15-coinY.get(i), block.get(i)); //edited block
			//addCoin(coinX.get(i),15-coinY.get(i));
    	}
    	coinX = new ArrayList<Integer>((Arrays.asList(165,170)));
    	coinY = new ArrayList<Integer>((Arrays.asList(  8,  9)));
    	block = new ArrayList<Integer>((Arrays.asList( 23, 23 )));
    	for(int i = 0; i<coinX.size(); i++) {
			//setBlocks(coinX.get(i),15-coinY.get(i), block.get(i));
    	}
    	coinX = new ArrayList<Integer>((Arrays.asList(237, 238)));
    	coinY = new ArrayList<Integer>((Arrays.asList(9,   9)));
    	block = new ArrayList<Integer>((Arrays.asList(23,  23 )));
    	for(int i = 0; i<coinX.size(); i++) {
			setBlocks(coinX.get(i),15-coinY.get(i), block.get(i)); //edited block
			//addCoin(coinX.get(i),15-coinY.get(i));
    	}								 
    	coinX = new ArrayList<Integer>((Arrays.asList(332,333,334,335,336,337,338,339,340,341,342,343)));
    	coinY = new ArrayList<Integer>((Arrays.asList(  5,  6,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7)));
    	block = new ArrayList<Integer>((Arrays.asList( 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23)));
    	for(int i = 0; i<coinX.size(); i++) {
			setBlocks(coinX.get(i),15-coinY.get(i), block.get(i)); //edited block
			//addCoin(coinX.get(i),15-coinY.get(i));
    	}
		
    	coinX = new ArrayList<Integer>((Arrays.asList(233,234,268,269,233,233,233,268,268)));
    	coinY = new ArrayList<Integer>((Arrays.asList(  6,  6,  5,  5,  5,  4,  3,  4,  3)));
    	block = new ArrayList<Integer>((Arrays.asList(-124,-123,-124,-123,-108,-108,-108,-108,-108)));
    	for(int i = 0; i<coinX.size(); i++) {
    			setBlocks(coinX.get(i),15-coinY.get(i), block.get(i)); //edited block
    			//addCoin(coinX.get(i),15-coinY.get(i));
    	}
    	
    	return 0;
    }
    private int addAllEnemy() {
    	ArrayList<Integer> enemyX = new ArrayList<Integer>((Arrays.asList(11,18, 48, 50, 70,71)));
    	ArrayList<Integer> enemyY = new ArrayList<Integer>((Arrays.asList(4,  3,  5,   5,  5, 5)));
    	
    	/*for(int i = 0; i<coinx.size(); i++) {
    			addCoin(coinx.get(i),15-coiny.get(i));
    	}
    	*/
    	return 0;
    }
    private int buildJump(int xo, int maxLength)
    {
        int js = random.nextInt(4) + 2;
        int jl = random.nextInt(2) + 2;
        int length = js * 2 + jl;

        boolean hasStairs = random.nextInt(3) == 0;

        int floor = height - 1 - random.nextInt(4);
        for (int x = xo; x < xo + length; x++)
        {
            if (x < xo + js || x > xo + length - js - 1)
            {
                for (int y = 0; y < height; y++)
                {
                    if (y >= floor)
                    {
                        level.setBlock(x, y, (byte) (1 + 9 * 16));
                    }
                    else if (hasStairs)
                    {
                        if (x < xo + js)
                        {
                            if (y >= floor - (x - xo) + 1)
                            {
                                level.setBlock(x, y, (byte) (9 + 0 * 16));
                            }
                        }
                        else
                        {
                            if (y >= floor - ((xo + length) - x) + 2)
                            {
                                level.setBlock(x, y, (byte) (9 + 0 * 16));
                            }
                        }
                    }
                }
            }
        }

        return length;
    }    
    
    private int buildJump(int xo, int maxLength, int len, int hi, int jx, int jlen)
    {
    	if (maxLength < 10) {
    		return 10;
    	}
        int js = len;
        int length = len;

        boolean hasStairs = random.nextInt(3) == 0;
        hasStairs = false;

        int floor = height - hi;
        for (int x = xo; x < xo + length; x++)
        {
            if (x >= xo + jx && x <= xo + jx + jlen - 1)
            {
                for (int y = 0; y < height; y++)
                {
                    if (y >= floor)
                    {
                        level.setBlock(x, y, (byte) (0));
                    }
                }
            }
            else {
                for (int y = 0; y < height; y++)
                {
                    if (y >= floor)
                    {
                        level.setBlock(x, y, (byte) (1 + 9 * 16));
                    }
                }
            	
            }
        }

        return length;
    }

    private int buildCannons(int xo, int maxLength)
    {
        int length = random.nextInt(10) + 2;
        if (length > maxLength) length = maxLength;

        int floor = height - 1 - random.nextInt(4);
        int xCannon = xo + 1 + random.nextInt(4);
        for (int x = xo; x < xo + length; x++)
        {
            if (x > xCannon)
            {
                xCannon += 2 + random.nextInt(4);
            }
            if (xCannon == xo + length - 1) xCannon += 10;
            int cannonHeight = floor - random.nextInt(4) - 1;

            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
                else
                {
                    if (x == xCannon && y >= cannonHeight)
                    {
                        if (y == cannonHeight)
                        {
                            level.setBlock(x, y, (byte) (14 + 0 * 16));
                        }
                        else if (y == cannonHeight + 1)
                        {
                            level.setBlock(x, y, (byte) (14 + 1 * 16));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (14 + 2 * 16));
                        }
                    }
                }
            }
        }

        return length;
    }
    private int buildCannons(int xo, int maxLength, int len, int hi)
    {
    	if (maxLength < 10) {
    		return 10;
    	}
        int length = len;
        if (length > maxLength) length = maxLength;

        int floor = height - hi;
        int xCannon = xo + 1 + random.nextInt(4);
        for (int x = xo; x < xo + length; x++)
        {
            if (x > xCannon)
            {
                xCannon += 2 + random.nextInt(4);
            }
            if (xCannon == xo + length - 1) xCannon += 10;
            int cannonHeight = floor - random.nextInt(4) - 1;

            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
                else
                {
                    if (x == xCannon && y >= cannonHeight)
                    {
                        if (y == cannonHeight)
                        {
                            level.setBlock(x, y, (byte) (14 + 0 * 16));
                        }
                        else if (y == cannonHeight + 1)
                        {
                            level.setBlock(x, y, (byte) (14 + 1 * 16));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (14 + 2 * 16));
                        }
                    }
                }
            }
        }

        return length;
    }

    private int buildHillStraight(int xo, int maxLength)
    {
        int length = random.nextInt(10) + 10;
        if (length > maxLength) length = maxLength;
        if (maxLength < 10) {
        	return 10;
        }

        int floor = height - 1 - random.nextInt(4);
        for (int x = xo; x < xo + length; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
            }
        }

        addEnemyLine(xo + 1, xo + length - 1, floor - 1);

        int h = floor;

        boolean keepGoing = true;

        boolean[] occupied = new boolean[length];
        while (keepGoing)
        {
            h = h - 2 - random.nextInt(3);

            if (h <= 0)
            {
                keepGoing = false;
            }
            else
            {
                int l = random.nextInt(5) + 3;
                int xxo = random.nextInt(length - l - 2) + xo + 1;

                if (occupied[xxo - xo] || occupied[xxo - xo + l] || occupied[xxo - xo - 1] || occupied[xxo - xo + l + 1])
                {
                    keepGoing = false;
                }
                else
                {
                    occupied[xxo - xo] = true;
                    occupied[xxo - xo + l] = true;
                    addEnemyLine(xxo, xxo + l, h - 1);
                    if (random.nextInt(4) == 0)
                    {
                        decorate(xxo - 1, xxo + l + 1, h);
                        keepGoing = false;
                    }
                    for (int x = xxo; x < xxo + l; x++)
                    {
                        for (int y = h; y < floor; y++)
                        {
                            int xx = 5;
                            if (x == xxo) xx = 4;
                            if (x == xxo + l - 1) xx = 6;
                            int yy = 9;
                            if (y == h) yy = 8;

                            if (level.getBlock(x, y) == 0)
                            {
                                level.setBlock(x, y, (byte) (xx + yy * 16));
                            }
                            else
                            {
                                if (level.getBlock(x, y) == (byte) (4 + 8 * 16)) level.setBlock(x, y, (byte) (4 + 11 * 16));
                                if (level.getBlock(x, y) == (byte) (6 + 8 * 16)) level.setBlock(x, y, (byte) (6 + 11 * 16));
                            }
                        }
                    }
                }
            }
        }

        return length;
    }
    private int buildHillStraight(int xo, int maxLength, int len, int hi, int h1x, int h1y, int h1len)
    {
    	if (maxLength < 10) {
    		return 10;
    	}
        int length = len;
        if (length > maxLength) length = maxLength;

        int floor = height - hi;
        for (int x = xo; x < xo + length; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
            }
        }

        int h = floor;

        boolean keepGoing = true;

        boolean[] occupied = new boolean[length];
        while (keepGoing)
        {
            //h = h - 2 - random.nextInt(3);
            h = floor - h1y;

            if (h <= 0)
            {
                keepGoing = false;
            }
            else
            {
                int l = random.nextInt(5) + 3;
                int xxo = random.nextInt(length - l - 2) + xo + 1;
                //l = h1len;
                xxo = xo + 1 + h1x;
                l = h1len;
                
                if (occupied[xxo - xo] || occupied[xxo - xo + l] || occupied[xxo - xo - 1] || occupied[xxo - xo + l + 1])
                {
                    keepGoing = false;
                }
                else
                {
                    occupied[xxo - xo] = true;
                    occupied[xxo - xo + l] = true;
                    //addEnemyLine(xxo, xxo + l, h - 1);
                    if (random.nextInt(4) == 0)
                    {
                        //decorate(xxo - 1, xxo + l + 1, h);
                        keepGoing = false;
                    }
                    for (int x = xxo; x < xxo + l; x++)
                    {
                        for (int y = h; y < floor; y++)
                        {
                            int xx = 5;
                            if (x == xxo) xx = 4;
                            if (x == xxo + l - 1) xx = 6;
                            int yy = 9;
                            if (y == h) yy = 8;

                            if (level.getBlock(x, y) == 0)
                            {
                                level.setBlock(x, y, (byte) (xx + yy * 16));
                            }
                            else
                            {
                                if (level.getBlock(x, y) == (byte) (4 + 8 * 16)) level.setBlock(x, y, (byte) (4 + 11 * 16));
                                if (level.getBlock(x, y) == (byte) (6 + 8 * 16)) level.setBlock(x, y, (byte) (6 + 11 * 16));
                            }
                        }
                    }
                }
            }
        }

        return length;
    }
    private void addEnemyLine(int x0, int x1, int y)
    {
        for (int x = x0; x < x1; x++)
        {
            if (random.nextInt(35) < difficulty + 1)
            {
                int type = random.nextInt(4);
                if (difficulty < 1)
                {
                    type = Enemy.ENEMY_GOOMBA;
                }
                else if (difficulty < 3)
                {
                    type = random.nextInt(3);
                }
                level.setSpriteTemplate(x, y, new SpriteTemplate(type, random.nextInt(35) < difficulty));
            }
        }
    }
    
    private void addEnemy(int x, int y, int type)
    {
        level.setSpriteTemplate(x, 15-y, new SpriteTemplate(type, false));
    }
    private void addCoin(int x, int y)
    {
    	level.setBlock(x, y, (byte) (34));
    }
    private void setBlocks(int x, int y, int type)
    {
    	level.setBlock(x, y, (byte) (type));
    }

    private int buildTubes(int xo, int maxLength)
    {
        int length = random.nextInt(10) + 5;
        if (length > maxLength) length = maxLength;

        int floor = height - 1 - random.nextInt(4);
        int xTube = xo + 1 + random.nextInt(4);
        int tubeHeight = floor - random.nextInt(2) - 2;
        for (int x = xo; x < xo + length; x++)
        {
            if (x > xTube + 1)
            {
                xTube += 3 + random.nextInt(4);
                tubeHeight = floor - random.nextInt(2) - 2;
            }
            if (xTube >= xo + length - 2) xTube += 10;

            if (x == xTube && random.nextInt(11) < difficulty + 1)
            {
                level.setSpriteTemplate(x, tubeHeight, new SpriteTemplate(Enemy.ENEMY_FLOWER, false));
            }

            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
                else
                {
                    if ((x == xTube || x == xTube + 1) && y >= tubeHeight)
                    {
                        int xPic = 10 + x - xTube;
                        if (y == tubeHeight)
                        {
                            level.setBlock(x, y, (byte) (xPic + 0 * 16));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (xPic + 1 * 16));
                        }
                    }
                }
            }
        }

        return length;
    }
    
    private int buildTubes(int xo, int maxLength, int len, int hi, int tx, int thi, boolean safe)
    {
        int length = len;
        if (length > maxLength) length = maxLength;

        int floor = height - hi;
        int xTube = xo + 0 + tx;
        int tubeHeight = floor - thi;
        for (int x = xo; x < xo + length; x++)
        {
            if (x > xTube + 1)
            {
                //xTube += 3 + random.nextInt(4);
                //tubeHeight = floor - random.nextInt(2) - 2;
            }
            //if (xTube >= xo + length - 2) xTube += 10;

            //if (x == xTube && random.nextInt(11) < difficulty + 1)
            if (!safe)
            {
                level.setSpriteTemplate(xo+tx, tubeHeight, new SpriteTemplate(Enemy.ENEMY_FLOWER, false));
                safe = true;
            }

            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
                else
                {
                    if ((x == xTube || x == xTube + 1) && y >= tubeHeight)
                    {
                        int xPic = 10 + x - xTube;
                        if (y == tubeHeight)
                        {
                            level.setBlock(x, y, (byte) (xPic + 0 * 16));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (xPic + 1 * 16));
                        }
                    }
                }
            }
        }

        return length;
    }
    
    private int buildStraight(int xo, int maxLength, boolean safe, int len, int hi)
    {
    	if (maxLength < 10) {
    		return 10;
    	}
        int length = len;
        if (safe) length += random.nextInt(5);
        if (length > maxLength) length = maxLength;

        int floor = height - hi;
        for (int x = xo; x < xo + length; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
            }
        }

        if (!safe)
        {
            if (length > 5)
            {
                decorate(xo, xo + length, floor);
            }
        }

        return length;
    }

    private int buildStraight(int xo, int maxLength, boolean safe)
    {
    	if (maxLength < 10) {
    		return 10;
    	}
        int length = random.nextInt(10) + 2;
        if (safe) length = 10 + random.nextInt(5);
        if (length > maxLength) length = maxLength;

        int floor = height - 1 - random.nextInt(4);
        for (int x = xo; x < xo + length; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (y >= floor)
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16));
                }
            }
        }

        if (!safe)
        {
            if (length > 5)
            {
                decorate(xo, xo + length, floor);
            }
        }

        return length;
    }

    private void decorate(int x0, int x1, int floor)
    {
        if (floor < 1) return;

        //        boolean coins = random.nextInt(3) == 0;
        boolean rocks = true;

        //addEnemyLine(x0 + 1, x1 - 1, floor - 1);

        int s = random.nextInt(4);
        int e = random.nextInt(4);

        if (floor - 2 > 0)
        {
            if ((x1 - 1 - e) - (x0 + 1 + s) > 1)
            {
                for (int x = x0 + 1 + s; x < x1 - 1 - e + 1; x++)
                {
                    level.setBlock(x, floor - 3, (byte) (0 + 1 * 16));
                    x++;
                }
                for (int x = x0 + 1 + s + 1 - 20; x < x1 - 1 - e + 0; x++)
                {
                    level.setBlock(x, floor - 5, (byte) (1 + 1 * 16));
                    x++;
                }
                for (int x = x0 + 1 + s + 2; x < x1 - 1 - e - 1; x++)
                {
                    level.setBlock(x, floor - 6, (byte) (2 + 1 * 16));
                    x++;
                    x++;
                }
            }
        }

        s = random.nextInt(4);
        e = random.nextInt(4);
/*
        if (floor - 4 > 0)
        {
            if ((x1 - 1 - e) - (x0 + 1 + s) > 2)
            {
                for (int x = x0 + 1 + s; x < x1 - 1 - e; x++)
                {
                    if (rocks)
                    {
                        if (x != x0 + 1 && x != x1 - 2 && random.nextInt(3) == 0)
                        {
                            if (random.nextInt(4) == 0)
                            {
                                level.setBlock(x, floor - 4, (byte) (4 + 2 + 1 * 16));
                            }
                            else
                            {
                                level.setBlock(x, floor - 4, (byte) (4 + 1 + 1 * 16));
                            }
                        }
                        else if (random.nextInt(4) == 0)
                        {
                            if (random.nextInt(4) == 0)
                            {
                                level.setBlock(x, floor - 4, (byte) (2 + 1 * 16));
                            }
                            else
                            {
                                level.setBlock(x, floor - 4, (byte) (1 + 1 * 16));
                            }
                        }
                        else
                        {
                            level.setBlock(x, floor - 4, (byte) (0 + 1 * 16));
                        }
                    }
                }
            }
        }
*/
        int length = x1 - x0 - 2;

        /*        if (length > 5 && rocks)
         {
         decorate(x0, x1, floor - 4);
         }*/
    }

    private void fixWalls()
    {
        boolean[][] blockMap = new boolean[width + 1][height + 1];
        for (int x = 0; x < width + 1; x++)
        {
            for (int y = 0; y < height + 1; y++)
            {
                int blocks = 0;
                for (int xx = x - 1; xx < x + 1; xx++)
                {
                    for (int yy = y - 1; yy < y + 1; yy++)
                    {
                        if (level.getBlockCapped(xx, yy) == (byte) (1 + 9 * 16)) blocks++;
                    }
                }
                blockMap[x][y] = blocks == 4;
            }
        }
        blockify(level, blockMap, width + 1, height + 1);
        
    }

    private void blockify(Level level, boolean[][] blocks, int width, int height)
    {
        int to = 0;
        if (type == LevelGenerator.TYPE_CASTLE)
        {
            to = 4 * 2;
        }
        else if (type == LevelGenerator.TYPE_UNDERGROUND)
        {
            to = 4 * 3;
        }

        boolean[][] b = new boolean[2][2];
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                for (int xx = x; xx <= x + 1; xx++)
                {
                    for (int yy = y; yy <= y + 1; yy++)
                    {
                        int _xx = xx;
                        int _yy = yy;
                        if (_xx < 0) _xx = 0;
                        if (_yy < 0) _yy = 0;
                        if (_xx > width - 1) _xx = width - 1;
                        if (_yy > height - 1) _yy = height - 1;
                        b[xx - x][yy - y] = blocks[_xx][_yy];
                    }
                }

                if (b[0][0] == b[1][0] && b[0][1] == b[1][1])
                {
                    if (b[0][0] == b[0][1])
                    {
                        if (b[0][0])
                        {
                            level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
                        }
                        else
                        {
                            // KEEP OLD BLOCK!
                        }
                    }
                    else
                    {
                        if (b[0][0])
                        {
                            level.setBlock(x, y, (byte) (1 + 10 * 16 + to));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (1 + 8 * 16 + to));
                        }
                    }
                }
                else if (b[0][0] == b[0][1] && b[1][0] == b[1][1])
                {
                    if (b[0][0])
                    {
                        level.setBlock(x, y, (byte) (2 + 9 * 16 + to));
                    }
                    else
                    {
                        level.setBlock(x, y, (byte) (0 + 9 * 16 + to));
                    }
                }
                else if (b[0][0] == b[1][1] && b[0][1] == b[1][0])
                {
                    level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
                }
                else if (b[0][0] == b[1][0])
                {
                    if (b[0][0])
                    {
                        if (b[0][1])
                        {
                            level.setBlock(x, y, (byte) (3 + 10 * 16 + to));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (3 + 11 * 16 + to));
                        }
                    }
                    else
                    {
                        if (b[0][1])
                        {
                            level.setBlock(x, y, (byte) (2 + 8 * 16 + to));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (0 + 8 * 16 + to));
                        }
                    }
                }
                else if (b[0][1] == b[1][1])
                {
                    if (b[0][1])
                    {
                        if (b[0][0])
                        {
                            level.setBlock(x, y, (byte) (3 + 9 * 16 + to));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (3 + 8 * 16 + to));
                        }
                    }
                    else
                    {
                        if (b[0][0])
                        {
                            level.setBlock(x, y, (byte) (2 + 10 * 16 + to));
                        }
                        else
                        {
                            level.setBlock(x, y, (byte) (0 + 10 * 16 + to));
                        }
                    }
                }
                else
                {
                    level.setBlock(x, y, (byte) (0 + 1 * 16 + to));
                }
            }
        }
    }
}