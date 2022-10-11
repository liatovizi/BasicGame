import java.util.Random;


public class BasicGame {
	
	static final int HEIGHT= 15;	
	static final int WIDTH = 15;
	static final int GAME_LOOP_NUMBER = 30;
	static final Random RANDOM = new Random();	
	
	public static void main(String[] args) throws InterruptedException {

		String[][] level = new String[HEIGHT][WIDTH];		
		initLevel(level);
		addRandomWalls(level);
		
		String playerMark = "O";
		int[] playerStartingCoordinates = getRandomStartingCoordinatesForPlayer(level);
		int playerRow= playerStartingCoordinates[0];
		int playerColumn =playerStartingCoordinates[1];
		Direction playerDirection = Direction.RIGHT;
		
		String enemyMark = "-";
		int[] enemyStartingCoordinates = getRandomStartingCoordinatesForEnemy(level, playerStartingCoordinates);
		int enemyRow= enemyStartingCoordinates[0];
		int enemyColumn =enemyStartingCoordinates[1];	
		Direction enemyDirection = Direction.LEFT;
		
		 
		for(int iterationNumber = 1; iterationNumber<=GAME_LOOP_NUMBER; iterationNumber++) {
			//player
			if (iterationNumber %15 == 0) {
				playerDirection = changeDirection(playerDirection);		
			}
			int [] playerCoordinates = makeMove(playerDirection, level, playerRow, playerColumn);
			playerRow = playerCoordinates[0];
			playerColumn = playerCoordinates [1];
			
			//enemy directions 
			enemyDirection = changeEnemyDirection(level,enemyDirection, playerRow, playerColumn, enemyRow, enemyColumn);		
			if(iterationNumber%2==0) {
				int [] enemyCoordinates = makeMove(enemyDirection, level, enemyRow, enemyColumn);
				enemyRow = enemyCoordinates[0];
				enemyColumn = enemyCoordinates [1];
			}
		
			draw(level, playerMark, playerRow, playerColumn, enemyMark, enemyRow, enemyColumn);
			
			addSomeDelay(200L, iterationNumber);
			
			if(playerRow == enemyRow && playerColumn == enemyColumn) {
				break;
			}
		}
		System.out.println("Game end");
	}
	
	static int[] getRandomStartingCoordinatesForEnemy(String[][] level, int[] playerStartingCoordinates) {
		int playerStartingRow = playerStartingCoordinates[0];
		int playerStartingColumn = playerStartingCoordinates[1];
		
		int randomRow;
		int randomColumn;
		
		do {
			randomRow = RANDOM.nextInt(HEIGHT);
			randomColumn = RANDOM.nextInt(WIDTH);
		}while(!level[randomRow][randomColumn].equals(" ") || calculateDistance(randomRow, randomColumn, playerStartingRow, playerStartingColumn)< 10 );
		return new int[] {randomRow, randomColumn};
		
	}

	static int calculateDistance(int row1, int column1, int row2, int column2) {
		int rowDifference = Math.abs(row1-row2);
		int columnDifference = Math.abs(column1-column2);
		return rowDifference + columnDifference;
	}

	static int[] getRandomStartingCoordinatesForPlayer(String[][] level) {
		int randomRow;
		int randomColumn;
		
		do {
			randomRow = RANDOM.nextInt(HEIGHT);
			randomColumn = RANDOM.nextInt(WIDTH);
		}while(!level[randomRow][randomColumn].equals(" "));
		return new int[] {randomRow, randomColumn};
	}

	static Direction changeEnemyDirection(String[][]level, Direction originalEnemyDirection, int playerRow, int playerColumn, int enemyRow, int enemyColumn) {
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

	static void draw(String[][]board, String playerMark, int playerRow, int playerColumn, String enemyMark, int enemyRow, int enemyColumn) {
		for(int row = 0; row < HEIGHT; row++) {
			for(int column = 0; column < WIDTH; column++) {
				if(row == playerRow && column==playerColumn) {
					System.out.print(playerMark);
				} else if(row == enemyRow && column==enemyColumn) {
					System.out.print(enemyMark);
				} else {
					System.out.print(board[row][column]);
				}
			}
			System.out.println();
		}
	}
}



