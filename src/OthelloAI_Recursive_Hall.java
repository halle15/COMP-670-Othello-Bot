// OthelloAI.java
//
// COMP 670 AI Game Project: Othello
//
// This is an individual project.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OthelloAI_Recursive_Hall implements OthelloAI {

	boolean IS_BLACK;

	int eval(OthelloGameState s) {
		int score = 0;
		if (IS_BLACK) {
			score = s.getBlackScore() - s.getWhiteScore();
		} else {
			score = s.getWhiteScore() - s.getBlackScore();
		}
		return score;
	}

	int search(OthelloGameState s, int depth) {

		/*
		 * if depth == 0 or leaf node
		 * return eval of s
		 * else
		 * if my turn
		 * for all valid moves of mine
		 * make move on s to give us next state
		 * search next state with depth of - 1
		 * 
		 * return max value from recursive search calls
		 * 
		 * else
		 * for eaech valid move of my opponent
		 * make move on s giving next state
		 * search next state with depth of - 1
		 * 
		 * return minimum value from recursive search
		 */

		return 1;
	}

	public boolean isTerminalNode(OthelloGameState s) {
		System.out.println("MOVES: " + findValidMoves(s).size());
		if (findValidMoves(s).size() <= 1) {
			return true;
		}
		return false;
	}

	// TODO: need a heuristic class
	public static int getRandomNumber(int x) {
		Random random = new Random();
		return random.nextInt(x);
	}

	// TODO: Implement as a priority queue or a TreeMap
	public HashMap<OthelloMove, Heuristic> findValidMoves(OthelloGameState state) {

		// no way there is more than 30 valid moves, ever
		// ArrayList<OthelloMove> validList = new ArrayList(30);
		HashMap<OthelloMove, Heuristic> validList = new HashMap<OthelloMove, Heuristic>();

		// check for corners first

		for (int c = 0; c < 8; c++) {
			for (int r = 0; r < 8; r++) {
				if (state.isValidMove(r, c)) {

					// Clone the game data for the upcoming state, make the move.
					OthelloGameState nextState = state.clone();
					nextState.makeMove(r, c);

					// System.out.println("EVALUATION OF " + nextState.hashCode() + ": " +
					// eval(nextState));

					// get heuristic values
					int bs = nextState.getBlackScore();
					int ws = nextState.getWhiteScore();
					Heuristic h = new Heuristic(bs, ws);

					// pack heuristic values with the relevant next move
					validList.put(new OthelloMove(r, c), h);

					// validList.add(new OthelloMove(r, c)); deprecated
				}
			}
		}
		return validList;
	}

	// chooseMove() takes an OthelloGameState and chooses the best move,
	// returning an OthelloMove that indicates what the move is. For
	// example, if the appropriate move is to place a tile in row 0 column 3,
	// you'd return a new OthelloMove with row 0 and column 3.
	public OthelloMove chooseMove(OthelloGameState state) {

		IS_BLACK = state.isBlackTurn();

		System.out.println("WE ARE BLACK? " + IS_BLACK);
		System.out.println(isTerminalNode(state));
	

	HashMap<OthelloMove, Heuristic> heuristicMap = findValidMoves(state);

	System.out.println(heuristicMap.toString());

	OthelloMove bestMove = null;
	// first pass should always be better, bs and ws at least 2.
	Heuristic bestHeuristic = new Heuristic(0, 0);

	/*
	 * if (checkCorners(state) != null) {
	 * 
	 * }
	 */

	if(IS_BLACK)
	{
		for (OthelloMove m : heuristicMap.keySet()) {
			Heuristic possibleMoveHeuristic = heuristicMap.get(m);
			if (possibleMoveHeuristic.getBlackScore() > bestHeuristic.getBlackScore()) {
				bestMove = m;
				bestHeuristic = possibleMoveHeuristic;
			}
		}
	}else
	{
		for (OthelloMove m : heuristicMap.keySet()) {
			Heuristic possibleMoveHeuristic = heuristicMap.get(m);
			if (possibleMoveHeuristic.getWhiteScore() > bestHeuristic.getWhiteScore()) {
				bestMove = m;
				bestHeuristic = possibleMoveHeuristic;
			}
		}
	}

	System.out.println("PICKING MOVE "+bestMove.hashCode()+" WITH EVAL "+heuristicMap.get(bestMove).toString());return bestMove;
}

/*
 * This class is responsible for being the value in a kv pair with the game
 * state
 * in order to efficiently organize the game states by highest heuristic value
 * 
 * if you are getting a certain move set, you are getting the next
 * state's heuristic values.
 */
public static class Heuristic {

	int blackScore;
	int whiteScore;

	public Heuristic(int blackScore, int whiteScore) {
		this.blackScore = blackScore;
		this.whiteScore = whiteScore;
	}

	public int getBlackScore() {
		return blackScore;
	}

	public void setBlackScore(int blackScore) {
		this.blackScore = blackScore;
	}

	public int getWhiteScore() {
		return whiteScore;
	}

	public void setWhiteScore(int whiteScore) {
		this.whiteScore = whiteScore;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + blackScore;
		result = prime * result + whiteScore;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Heuristic other = (Heuristic) obj;
		if (blackScore != other.blackScore)
			return false;
		if (whiteScore != other.whiteScore)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Heuristic [blackScore=" + blackScore + ", whiteScore=" + whiteScore + "]";
	}

}

}
