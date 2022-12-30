import java.util.Random;

public class BasicGame {
	
	static final int HEIGHT= 40;	
	static final int WIDTH = 40;
	static final int GAME_LOOP_NUMBER = 100;
	static final Random RANDOM = new Random(100L);		
	
	public static void main(String[] args) throws InterruptedException {
		Level level;
		int counter = 0;
		do {
			level = new Level(RANDOM, HEIGHT, WIDTH);
			level.addRandomWalls();
			counter++;
		}while(!level.isPassable());
		System.out.println(counter + " Is passable");
		level.isPassable(true);
		
		Coordinates playerCoordinates = level.getRandomCoordinates();
		MovingEntity player = new MovingEntity("O", playerCoordinates,level.getFarthestCorner(playerCoordinates),Direction.RIGHT, level );
		
		Coordinates enemyCoordinates = level.getRandomCoordinatesCertainDistance(player.getCoordinates(), 10);	
		MovingEntity enemy = new MovingEntity("-", enemyCoordinates, level.getFarthestCorner(enemyCoordinates), Direction.LEFT, level);

		PowerUp powerup = new PowerUp("*", level.getRandomCoordinates(), level);
		
		GameResult gameResult=GameResult.TIE;
		 
		for(int iterationNumber = 1; iterationNumber<=GAME_LOOP_NUMBER; iterationNumber++) {
			//player
			if (powerup.isActive()){
				player.setDirection(level.getShortestPath(player.getDirection(),player.getCoordinates(), enemy.getCoordinates()));
			} else {
				if(powerup.isPresentOnLevel()) {
					player.setDirection(level.getShortestPath(player.getDirection(), player.getCoordinates(), powerup.getCoordinates()));
				}else {
					if (iterationNumber %100 == 0) {
						player.setEscapeCoordinates(level.getFarthestCorner(player.getCoordinates()));
					}
					player.setDirection(level.getShortestPath(player.getDirection(), player.getCoordinates(), player.getEscapeCoordinates()));
				}
			}
			player.update();
		
			
			//enemy directions 
			if(powerup.isActive()) {
				if(iterationNumber % 100 == 0) {
					enemy.setEscapeCoordinates(level.getFarthestCorner(enemy.getCoordinates()));
				}
				enemy.setDirection(level.getShortestPath(enemy.getDirection(), enemy.getCoordinates(), enemy.getEscapeCoordinates()));
			}else {
				enemy.setDirection(level.getShortestPath(enemy.getDirection(), enemy.getCoordinates(), player.getCoordinates()));		
			}
			if(iterationNumber%2==0) {
				enemy.update();
			}
			
			if(powerup.update()) {
				player.setEscapeCoordinates(level.getFarthestCorner(player.getCoordinates()));
			}
			
			//player - power up interaction
			if(powerup.isPresentOnLevel() && player.getCoordinates().isSameAs(powerup.getCoordinates())) {
				powerup.activate();
				powerup.hideOnLevel();
				powerup.resetPresenceCounter();
				enemy.setEscapeCoordinates(level.getFarthestCorner( enemy.getCoordinates()));
			}
			
			draw(level, player, enemy, powerup);
			
			addSomeDelay(500L, iterationNumber);
			
			// they catch each other
			if(playerCoordinates.isSameAs(enemy.getCoordinates())) {
				if(powerup.isActive()) {
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
	


	
	private static void addSomeDelay(long timeout, int iteratonNumber) throws InterruptedException {
		System.out.println("-----" + iteratonNumber +"-------");
		Thread.sleep(timeout);
	}
	



	static void draw(Level level, Entity player, Entity enemy, PowerUp powerup ) {
		for(int row = 0; row < HEIGHT; row++) {
			for(int column = 0; column < WIDTH; column++) {
				Coordinates coordinatesToDraw = new Coordinates(row, column);
				if(coordinatesToDraw.isSameAs(player.getCoordinates())) {
					System.out.print(player.getMark());
				} else if(coordinatesToDraw.isSameAs(enemy.getCoordinates())) {
					System.out.print(enemy.getMark());
				} else if(powerup.isPresentOnLevel() && coordinatesToDraw.isSameAs(powerup.getCoordinates())) {
					System.out.print(powerup.getMark());
				} else {
					System.out.print(level.getCell(coordinatesToDraw));
				}
			}
			System.out.println();
		}
		if(powerup.isActive()) {
			System.out.println("Power-up activated");
		}
	}
}



