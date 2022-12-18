import java.util.Random;

public class BasicGame {
	
	static final int HEIGHT= 40;	
	static final int WIDTH = 40;
	static final int GAME_LOOP_NUMBER = 50;
	static final Random RANDOM = new Random(100L);		
	
	public static void main(String[] args) throws InterruptedException {
		String[][] level = new String[HEIGHT][WIDTH];
		int counter = 0;
		do {
			initLevel(level);
			addRandomWalls(level);
			counter++;
		}while(!isPassable(level));
		System.out.println(counter + " Is passable");
		
		
		String playerMark = "O";
		Coordinates playerCoordinates = getRandomStartingCoordinates(level);
		Coordinates playerEscapeCoordinates = getFarthestCorner(level, playerCoordinates);
		Direction playerDirection = Direction.RIGHT;
		
		String enemyMark = "-";
		Coordinates enemyCoordinates = getRandomStartingCoordinatesCertainDistance(level, playerCoordinates, 10);	
		Coordinates enemyEscapeCoordinates = getFarthestCorner(level,enemyCoordinates);
		Direction enemyDirection = Direction.LEFT;

		String powerUpMark = "*";
		Coordinates powerUpCoordinates = getRandomStartingCoordinates(level);	
		boolean powerUpPresentOnLevel = false;
		int powerUpPresenceCounter = 0;
		boolean powerUpActive = false;
		int powerUpActiveCounter = 0;
		
		GameResult gameResult=GameResult.TIE;
		 
		for(int iterationNumber = 1; iterationNumber<=GAME_LOOP_NUMBER; iterationNumber++) {
			//player
			if (powerUpActive){
				playerDirection = getShortestPath(level, playerDirection,playerCoordinates, enemyCoordinates);
			} else {
				if(powerUpPresentOnLevel) {
					playerDirection = getShortestPath(level, playerDirection, playerCoordinates, powerUpCoordinates);
				}else {
					if (iterationNumber %100 == 0) {
						playerEscapeCoordinates = getFarthestCorner(level, playerCoordinates);
					}
					playerDirection = getShortestPath(level, playerDirection, playerCoordinates, playerEscapeCoordinates);
				}
			}
			playerCoordinates = makeMove(playerDirection, level, playerCoordinates);
			
			//enemy directions 
			if(powerUpActive) {
				if(iterationNumber % 100 == 0) {
					enemyEscapeCoordinates = getFarthestCorner(level, enemyCoordinates);
				}
				enemyDirection = getShortestPath(level, enemyDirection, enemyCoordinates, enemyEscapeCoordinates);
			}else {
				enemyDirection = getShortestPath(level, enemyDirection, enemyCoordinates, playerCoordinates);		
			}
			if(iterationNumber%2==0) {
				enemyCoordinates = makeMove(enemyDirection, level, enemyCoordinates);
			}
			
			//power-up 
			if(powerUpActive) {
				powerUpActiveCounter++;
			}else {
				powerUpPresenceCounter ++;
			}
			if(powerUpPresenceCounter >= 60) {
				if(powerUpPresentOnLevel) {
					powerUpCoordinates = getRandomStartingCoordinates(level);	
				}
				powerUpPresentOnLevel = !powerUpPresentOnLevel;
				powerUpPresenceCounter = 0;
			}
			if(powerUpActiveCounter>=60) {
				powerUpActive = false;
				powerUpActiveCounter = 0;
				powerUpCoordinates = getRandomStartingCoordinates(level);
				playerEscapeCoordinates = getFarthestCorner(level,playerCoordinates);
			}
			
			//jplayer - power up interaction
			if(powerUpPresentOnLevel && playerCoordinates.isSameAs(powerUpCoordinates)) {
				powerUpActive = true;
				powerUpPresentOnLevel = false;
				powerUpPresenceCounter = 0;
				enemyEscapeCoordinates = getFarthestCorner(level, enemyCoordinates);
			}
			
			draw(level, playerMark, playerCoordinates, enemyMark, enemyCoordinates, powerUpMark,powerUpCoordinates, powerUpPresentOnLevel, powerUpActive);
			
			addSomeDelay(200L, iterationNumber);
			
			// they catch each other
			if(playerCoordinates.isSameAs(enemyCoordinates)) {
				if(powerUpActive) {
					gameResult = GameResult.WIN;
				}else {
					gameResult = GameResult.LOSE;
				}
				break;
			}
	}
	
	switch(gameResult){
	case WIN:
		System.out.println("Congrat, you win");
		break;
	case LOSE:
		System.out.println("You lost");
		break;
	default: 
		System.out.println("Tie");
	}
	
	System.out.println("Game end");
	}
	
	static Coordinates getFarthestCorner(String[][] level, Coordinates from) {
		//Copy
		String[][] levelCopy = copy(level);
		
		//
		levelCopy[from.getRow()][from.getColumn()] = "*";
		
		int farthestRow =0;
		int farthestColumn = 0;
		
		while(spreadAsterisksWithCheck(levelCopy)) {
			outer:for(int row= 0; row < HEIGHT; row++) {
				for(int column = 0; column < WIDTH; column++) {
					if(" ".equals(levelCopy[row][column])) {
						farthestRow = row;
						farthestColumn = column;
						break outer;
					}
				}
			}
		}
		Coordinates farthestCorner = new Coordinates();
		farthestCorner.setRow(farthestRow);
		farthestCorner.setColumn(farthestColumn);
		return farthestCorner;
	}
	
	static Direction getShortestPath(String[][] level, Direction defaultDirection, Coordinates from, Coordinates to) {
		
		String[][] levelCopy = copy(level);
		
	
		levelCopy[to.getRow()][to.getColumn()] = "*";
		
		while(spreadAsterisksWithCheck(levelCopy)) {
			if("*".equals(levelCopy[from.getRow()-1][from.getColumn()])) {
				return Direction.UP;
			}
			if("*".equals(levelCopy[from.getRow()+1][from.getColumn()])) {
				return Direction.DOWN;
			}
			if("*".equals(levelCopy[from.getRow()][from.getColumn()-1])) {
				return Direction.LEFT;
			}
			if("*".equals(levelCopy[from.getRow()][from.getColumn()+1])) {
				return Direction.UP;
			}
		}
		
		return defaultDirection;
	}
	
	static boolean spreadAsterisksWithCheck(String[][] levelCopy) {
		boolean[][] mask = new boolean[HEIGHT][WIDTH];
		for(int row= 0; row <HEIGHT; row++) {
			for(int column = 0; column < WIDTH; column++) {
				if("*".equals(levelCopy[row][column])) {
					mask[row][column]=true;
				}
			}
		
		} return false;
	}

	static boolean isPassable(String[][] level) {
		
		String[][] levelCopy = copy(level);		
		
//		find the first free space and replace with *
		outer: for(int row=0; row < HEIGHT; row++) {
			for(int column=0; column < WIDTH; column++) {
				if(" ".equals(levelCopy[row][column])) {
					levelCopy[row][column] = "*";
					break outer;
				}
			}
		}
		
	
		while (spreadAsterisks(levelCopy)) {
			//TODO

		}
		
	
		for(int row=0; row < HEIGHT; row++) {
			for(int column=0; column < WIDTH; column++) {
				if(" ".equals(levelCopy[row][column])) {
					return false;
				}
			}
		}
		
		return true;
	}

	 static boolean spreadAsterisks(String[][] levelCopy) {
		boolean changed = false;
		 for(int row=0; row < HEIGHT; row++) {
			for(int column=0; column < WIDTH; column++) {
				
	
				if("*".equals(levelCopy[row][column])) {
					if(" ".equals(levelCopy[row-1][column])) {
						levelCopy[row-1][column] = "*";
						changed = true;
					}
					
					if(" ".equals(levelCopy[row+1][column])) {
						levelCopy[row+1][column] = "*";
						changed = true;
					}
					
					if(" ".equals(levelCopy[row][column-1])) {
						levelCopy[row][column-1] = "*";
						changed = true;
					}
					
					if(" ".equals(levelCopy[row][column+1])) {
						levelCopy[row][column+1] = "*";
						changed = true;
					}
				}	
				

			}
		} 
		return changed;
	}



	static String[][] copy(String[][] level) {
		String [][]copy = new String[HEIGHT][WIDTH];
		for(int row=0; row < HEIGHT; row++) {
			for(int column=0; column < WIDTH; column++) {
				copy[row][column] = level[row][column];
			}
		}
		return copy;
	}

	static Direction getEscapeDirection(String[][] level, int enemyRow, int enemyColumn, Direction directionTowardsPlayer) {
		Direction escapeDirection = getOppositeDirection(directionTowardsPlayer);
		switch(escapeDirection) {
		case UP: 
			if(level[enemyRow-1][enemyColumn].equals(" ")) {
				return Direction.UP;
			} else if(level[enemyRow][enemyColumn-1].equals(" ")) {
				return Direction.LEFT;
			} else if(level[enemyRow][enemyColumn+1].equals(" ")) {
				return Direction.RIGHT;
			} else {
				return Direction.UP;
			}
			
		case DOWN: 
			if(level[enemyRow+1][enemyColumn].equals(" ")) {
				return Direction.DOWN;
			} else if(level[enemyRow][enemyColumn-1].equals(" ")) {
				return Direction.LEFT;
			} else if(level[enemyRow][enemyColumn+1].equals(" ")) {
				return Direction.RIGHT;
			} else {
				return Direction.DOWN;
			}
			
		case RIGHT: 
			if(level[enemyRow][enemyColumn+1].equals(" ")) {
				return Direction.RIGHT;
			} else if(level[enemyRow-1][enemyColumn].equals(" ")) {
				return Direction.UP;
			} else if(level[enemyRow+1][enemyColumn].equals(" ")) {
				return Direction.DOWN;
			} else {
				return Direction.RIGHT;
			}
			
		case LEFT: 
			if(level[enemyRow][enemyColumn-1].equals(" ")) {
				return Direction.LEFT;
			} else if(level[enemyRow-1][enemyColumn].equals(" ")) {
				return Direction.UP;
			} else if(level[enemyRow+1][enemyColumn].equals(" ")) {
				return Direction.DOWN;
			} else {
				return Direction.LEFT;
			}
			default: return escapeDirection;
			
		}
	}

	static Direction getOppositeDirection(Direction direction) {
		switch (direction) {
		case UP: return Direction.DOWN;
		case DOWN: return Direction.UP;
		case RIGHT: return Direction.RIGHT;
		case LEFT: return Direction.LEFT;
		default: return direction;
		}
	}

	static Coordinates getRandomStartingCoordinatesCertainDistance(String[][] level, Coordinates playerStartingCoordinates, int distance) {
		int playerStartingRow = playerStartingCoordinates.getRow();
		int playerStartingColumn = playerStartingCoordinates.getColumn();
		
		int randomRow;
		int randomColumn;
		int counter = 0;
		do {
			randomRow = RANDOM.nextInt(HEIGHT);
			randomColumn = RANDOM.nextInt(WIDTH);
		}while(counter ++<1000 
				&& (!level[randomRow][randomColumn].equals(" ") 
				|| calculateDistance(randomRow, randomColumn, playerStartingRow, playerStartingColumn)< distance ));
		Coordinates startingCoordinates = new Coordinates();
		startingCoordinates.setRow(randomRow);
		startingCoordinates.setColumn(randomColumn);;
		return startingCoordinates;
		
	}

	static int calculateDistance(int row1, int column1, int row2, int column2) {
		int rowDifference = Math.abs(row1-row2);
		int columnDifference = Math.abs(column1-column2);
		return rowDifference + columnDifference;
	}

	static Coordinates getRandomStartingCoordinates(String[][] level) {
		int randomRow;
		int randomColumn;
		
		do {
			randomRow = RANDOM.nextInt(HEIGHT);
			randomColumn = RANDOM.nextInt(WIDTH);
		}while(!level[randomRow][randomColumn].equals(" "));
		Coordinates startingCoordinates = new Coordinates();
		startingCoordinates.setRow(randomRow);
		startingCoordinates.setColumn(randomColumn);
		return startingCoordinates;
	}

	static Direction changeDirectionTowards(String[][]level, Direction originalEnemyDirection, int enemyRow, int enemyColumn, int playerRow, int playerColumn) {
		if (playerRow <enemyRow && level[enemyRow-1][enemyColumn].equals(" ")) {
			return Direction.UP;
		}
		if (playerRow >enemyRow && level[enemyRow+1][enemyColumn].equals(" ")) {
			return Direction.DOWN;
		}
		if (playerColumn <enemyColumn && level[enemyRow][enemyColumn -1].equals(" ")) {
			return Direction.LEFT;
		}
		if (playerColumn >enemyColumn && level[enemyRow][enemyColumn+1].equals(" ")) {
			return Direction.RIGHT;
		}
		return originalEnemyDirection;
	}
	
	static void addRandomWalls(String [][] level) {
		addRandomWalls(level, 10,10);
	}
	
	static void addRandomWalls(String [][] level, int numberOfHorizontalWalls, int numberOfVerticalWalls) {
	
	for(int i = 0; i < numberOfHorizontalWalls; i ++) {
		addHorizontalWall(level);
	}
	for(int i = 0; i < numberOfVerticalWalls; i ++) {
		addVerticalWall(level);
	}
	
	}
	
	static void addHorizontalWall(String[][] level) {
		int wallWidth = RANDOM.nextInt(WIDTH-3);
		int wallRow = RANDOM.nextInt(HEIGHT - 2)+1;
		int wallColumn = RANDOM.nextInt(WIDTH - 2 - wallWidth);
		for (int i = 0; i <= wallWidth; i++) {
			level[wallRow][wallColumn + i]= "X";	
		}
	}
	
	static void addVerticalWall(String [][] level) {
		int wallHeight = RANDOM.nextInt(HEIGHT-3); //0-11
		int wallRow = RANDOM.nextInt(HEIGHT - 2 - wallHeight);
		int wallColumn = RANDOM.nextInt(WIDTH - 2)+1;
		for (int i = 0; i <= wallHeight; i++) {
			level[wallRow + i][wallColumn]= "X";
			
		}
	}

	private static void addSomeDelay(long timeout, int iteratonNumber) throws InterruptedException {
		System.out.println("-----" + iteratonNumber +"-------");
		Thread.sleep(timeout);
	}
	
	static Coordinates makeMove (Direction direction, String[][] level, Coordinates oldCoordinates){
		Coordinates newCoordinates = new Coordinates();
		newCoordinates.setRow(oldCoordinates.getRow());
		newCoordinates.setColumn(oldCoordinates.getColumn());
		switch (direction) {
		case UP: 
			if (level[oldCoordinates.getRow()-1][oldCoordinates.getColumn()].equals(" ")) {
				newCoordinates.setRow(oldCoordinates.getRow()-1);
				} 
				break;
		case DOWN: 
			if (level[oldCoordinates.getRow()+1][oldCoordinates.getColumn()].equals(" ")) { 
				newCoordinates.setRow(oldCoordinates.getRow()+1);
				} 
				break;
		case LEFT: 
			if (level[oldCoordinates.getRow()][oldCoordinates.getColumn()-1].equals(" ")) {
				newCoordinates.setColumn(oldCoordinates.getColumn()-1);
				} 
				break;
		case RIGHT: 
			if (level[oldCoordinates.getRow()][oldCoordinates.getColumn()+1].equals(" ")) {
				newCoordinates.setColumn(oldCoordinates.getColumn()+1);
				} 
				break;
		}
		return newCoordinates;
	}

	static void initLevel(String[][] level) {
		for(int row = 0; row < level.length; row++) {
			for(int column = 0; column < level[row].length; column++) {
				if (row == 0 || row == HEIGHT-1 || column== 0 || column == WIDTH-1) {
					level[row][column] = "x";	
				} else {
					level[row][column] = " ";
				}
				
			}
			
		}
	}

	 static Direction changeDirection(Direction direction) {
		switch(direction) {
		case RIGHT: direction = Direction.DOWN; break;
		case DOWN: direction = Direction.LEFT; break;
		case LEFT: direction = Direction.UP; break;
		case UP: direction = Direction.RIGHT; break;
		}
		return direction;
	}

	static void draw(String[][]board, String playerMark, Coordinates playerCoordinates, String enemyMark, Coordinates enemyCordinates, String powerUpMark,
			Coordinates powerUpCoordinates, boolean powerUpPresentOnLevel, boolean PowerUpActive ) {
		for(int row = 0; row < HEIGHT; row++) {
			for(int column = 0; column < WIDTH; column++) {
				Coordinates coordinatesToDraw = new Coordinates();
				coordinatesToDraw.setRow(row);
				coordinatesToDraw.setColumn(column);
				if(coordinatesToDraw.isSameAs(playerCoordinates)) {
					System.out.print(playerMark);
				} else if(coordinatesToDraw.isSameAs(enemyCordinates)) {
					System.out.print(enemyMark);
				} else if(powerUpPresentOnLevel && coordinatesToDraw.isSameAs(powerUpCoordinates)) {
					System.out.print(powerUpMark);
				} else {
					System.out.print(board[row][column]);
				}
			}
			System.out.println();
		}
		if(PowerUpActive) {
			System.out.println("Power-up activated");
		}
	}
}



