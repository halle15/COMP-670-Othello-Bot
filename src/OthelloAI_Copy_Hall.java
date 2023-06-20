// OthelloAI.java
//
// COMP 670 AI Game Project: Othello
//
// This is an individual project.

import java.util.Random;

public class OthelloAI_Copy_Hall implements OthelloAI {

	public static int getRandomNumber(int x) {
		Random random = new Random();
		return random.nextInt(x + 1);
	}

	// chooseMove() takes an OthelloGameState and chooses the best move,
	// returning an OthelloMove that indicates what the move is. For
	// example, if the appropriate move is to place a tile in row 0 column 3,
	// you'd return a new OthelloMove with row 0 and column 3.
	public OthelloMove chooseMove(OthelloGameState state){
		
		int randX, randY;

		randX = getRandomNumber(7);
		randY = getRandomNumber(7);

		OthelloMove randomMove = new OthelloMove(0, 0);


		while(! state.isValidMove(randX, randY));{
			randX = getRandomNumber(7);
			randY = getRandomNumber(7);
			System.out.println("x: " + randX + "y: " + randY);

			System.out.println(state.isValidMove(randX, randY));
		}
		

		randomMove = new OthelloMove(7, 7);

		return randomMove;
	}
}
