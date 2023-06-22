// OthelloAI.java
//
// COMP 670 AI Game Project: Othello
//
// This is an individual project.

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OthelloAI_Evann_Copy implements OthelloAI
{

	//TODO: need a heuristic class
	public static int getRandomNumber(int x) {
		Random random = new Random();
		return random.nextInt(x);
	}

	// implement this as a priority queue?
	public List<OthelloMove> findValidMoves(OthelloGameState state){

		// no way there is more than 30 valid moves, ever
		ArrayList<OthelloMove> validList = new ArrayList(30);

		for(int c=0; c<8; c++){
			for(int r=0; r<8; r++){
				if(state.isValidMove(r, c)){
					validList.add(new OthelloMove(r, c));
				}
			}
		}
		return validList;
	}

	// chooseMove() takes an OthelloGameState and chooses the best move,
	// returning an OthelloMove that indicates what the move is.  For
	// example, if the appropriate move is to place a tile in row 0 column 3,
	// you'd return a new OthelloMove with row 0 and column 3.
	public OthelloMove chooseMove(OthelloGameState state){

		List<OthelloMove> moveList = findValidMoves(state);

		return moveList.get(getRandomNumber(moveList.size()));
		
	}

	/*
	 * This class is responsible for being the value in a kv pair with the game state 
	 * in order to efficiently organize the game states by highest heuristic value
	 */
	public static class Heuristic {
		
	}

}
