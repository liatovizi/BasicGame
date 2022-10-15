import java.util.Random;

//Put a power up on the stage every 20thtime. If the player steps on it, the enemy starts to run away, runs to the opposite direction.
//if the player catches the enemy, player wins.

public class BasicGame {
	
	static final int HEIGHT= 15;	
	static final int WIDTH = 15;
	static final int GAME_LOOP_NUMBER = 100;
	static final Random RANDOM = new Random();	
	
	public static void main(String[] args) throws InterruptedException {

		String[][] level = new String[HEIGHT][WIDTH];		
		initLevel(level);
		addRandomWalls(level);
		
		String playerMark = "O";
		int[] playerStartingCoordinates = getRandomStartingCoordinates(level);
		int playerRow= playerStartingCoordinates[0];
		int playerColumn =playerStartingCoordinates[1];
		Direction playerDirection = Direction.RIGHT;
		
		String enemyMark = "-";
		int[] enemyStartingCoordinates = getRandomStartingCoordinatesCertainDistance(level, playerStartingCoordinates, 10);
		int enemyRow= enemyStartingCoordinates[0];
		int enemyColumn =enemyStartingCoordinates[1];	
		Direction enemyDirection = Direction.LEFT;

		String powerUpMark = "*";
		int[] powerUpStartingCoordinates = getRandomStartingCoordinates(level);
		int powerUpRow= powerUpStartingCoordinates [0];
		int powerUpColumn =powerUpStartingCoordinates [1];	
		boolean powerUpPresentOnLevel = false;
		int powerUpPresenceCounter = 0;
		boolean powerUpActive = false;
		int powerUpActiveCounter = 0;
		
		GameResult gameResult=GameResult.TIE;
		 
		for(int iterationNumber = 1; iterationNumber<=GAME_LOOP_NUMBER; iterationNumber++) {
			//player
			if(powerUpActive) {
				playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerColumn, enemyRow, enemyColumn);
			}else { 
				if(powerUpPresentOnLevel) {
					playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerColumn, powerUpRow, powerUpColumn);
				}else {
					if (iterationNumber %15 == 0) {
						playerDirection = changeDirection(playerDirection);	
				}
				}
				
			}
			int [] playerCoordinates = makeMove(playerDirection, level, playerRow, playerColumn);
			playerRow = playerCoordinates[0];
			playerColumn = playerCoordinates [1];
			
			//enemy directions 
			if(powerUpActive) {
				Direction directionTowardsPlayer = changeDirectionTowards(level,enemyDirection, enemyRow, enemyColumn, playerRow, playerColumn);
				enemyDirection = getEscapeDirection(level, enemyRow, enemyColumn, directionTowardsPlayer);
			}else { 
				enemyDirection = changeDirectionTowards(level,enemyDirection, enemyRow, enemyColumn, playerRow, playerColumn);		
			}
			if(iterationNumber%2==0) {
				int [] enemyCoordinates = makeMove(enemyDirection, level, enemyRow, enemyColumn);
				enemyRow = enemyCoordinates[0];
				enemyColumn = enemyCoordinates [1];
			}
			
			//power-up 
			if(powerUpActive) {
				powerUpActiveCounter++;
			}else {
				powerUpPresenceCounter ++;
			}
			
			if(powerUpPresenceCounter >= 20) {
				if(powerUpPresentOnLevel) {
					powerUpStartingCoordinates = getRandomStartingCoordinates(level);
					powerUpRow= powerUpStartingCoordinates [0];
					powerUpColumn =powerUpStartingCoordinates [1];	
				}
				powerUpPresentOnLevel = !powerUpPresentOnLevel;
				powerUpPresenceCounter = 0;
			}
			if(powerUpActiveCounter>=20) {
				powerUpActive = false;
				powerUpActiveCounter = 0;
				powerUpStartingCoordinates = getRandomStartingCoordinates(level);
				powerUpRow= powerUpStartingCoordinates [0];
				powerUpColumn =powerUpStartingCoordinates [1];	
			}
			
			//jplayer - power up interaction
			if(powerUpPresentOnLevel && playerRow == powerUpRow && playerColumn == powerUpColumn) {
				powerUpActive = true;
				powerUpPresentOnLevel = false;
				powerUpPresenceCounter = 0;
				
			}
			
			draw(level, playerMark, playerRow, playerColumn, enemyMark, enemyRow, enemyColumn, powerUpMark,powerUpRow, powerUpColumn, powerUpPresentOnLevel, powerUpActive);
			
			addSomeDelay(200L, iterationNumber);
			
			// they catch each other
			if(playerRow == enemyRow && playerColumn == enemyColumn) {
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

	static int[] getRandomStartingCoordinatesCertainDistance(String[][] level, int[] playerStartingCoordinates, int distance) {
		int playerStartingRow = playerStartingCoordinates[0];
		int playerStartingColumn = playerStartingCoordinates[1];
		
		int randomRow;
		int randomColumn;
		int counter = 0;
		do {
			randomRow = RANDOM.nextInt(HEIGHT);
			randomColumn = RANDOM.nextInt(WIDTH);
		}while(counter ++<1000 
				&& (!level[randomRow][randomColumn].equals(" ") 
				|| calculateDistance(randomRow, randomColumn, playerStartingRow, playerStartingColumn)< distance ));
		return new int[] {randomRow, randomColumn};
		
	}

	static int calculateDistance(int row1, int column1, int row2, int column2) {
		int rowDifference = Math.abs(row1-row2);
		int columnDifference = Math.abs(column1-column2);
		return rowDifference + columnDifference;
	}

	static int[] getRandomStartingCoordinates(String[][] level) {
		int randomRow;
		int randomColumn;
		
		do {
			randomRow = RANDOM.nextInt(HEIGHT);
			randomColumn = RANDOM.nextInt(WIDTH);
		}while(!level[randomRow][randomColumn].equals(" "));
		return new int[] {randomRow, randomColumn};
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
		addRandomWalls(level, 3,2);
	}
	
	static void addRandomWalls(String [][] level, int numberOfHorizontalWalls, int numberOfVerticalWalls) {
	// TODO fal ne keruljon a jatekos v az ellenfelre
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
	
	static int[] makeMove (Direction direction, String[][] level, int row, int column){
		switch (direction) {
		case UP: if (level[row-1][column].equals(" ")) {row--;} break;
		case DOWN: if (level[row+1][column].equals(" ")) { row ++;} break;
		case LEFT: if (level[row][column-1].equals(" ")) {column --;} break;
		case RIGHT: if (level[row][column+1].equals(" ")) {column ++;} break;
		}
		return new int [] {row, column};
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

	static void draw(String[][]board, String playerMark, int playerRow, int playerColumn, String enemyMark, int enemyRow, int enemyColumn, String powerUpMark, int powerUpRow, 
			int powerUpColumn, boolean powerUpPresentOnLevel, boolean PowerUpActive ) {
		for(int row = 0; row < HEIGHT; row++) {
			for(int column = 0; column < WIDTH; column++) {
				if(row == playerRow && column==playerColumn) {
					System.out.print(playerMark);
				} else if(row == enemyRow && column==enemyColumn) {
					System.out.print(enemyMark);
				} else if(powerUpPresentOnLevel && row == powerUpRow && column==powerUpColumn) {
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



