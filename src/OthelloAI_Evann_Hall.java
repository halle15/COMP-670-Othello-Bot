// OthelloAI.java
//
// COMP 670 AI Game Project: Othello
//
// This is an individual project.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OthelloAI_Evann_Hall implements OthelloAI {

	int eval(OthelloGameState s){


		return 0;
	}

	public boolean isLeaf(OthelloGameState s){
		
	}

	int search(OthelloGameState s, int depth){

		/* if depth == 0 or leaf node
		 * 		return eval of s
		 * else
		 * 		if my turn
		 * 			for all valid moves of mine
		 * 				make move on s to give us next state
		 * 				search next state with depth of - 1
		 * 			
		 * 			return max value from recursive search calls
		 * 
		 * 		else
		 * 			for eaech valid move of my opponent
		 * 				make move on s giving next state
		 * 				search next state with depth of - 1
		 * 
		 * 			return minimum value from recursive search
		*/


		return 1;
	}


	// TODO: need a heuristic class
	public static int getRandomNumber(int x) {
		Random random = new Random();
		return random.nextInt(x);
	}


	// chooseMove() takes an OthelloGameState and chooses the best move,
	// returning an OthelloMove that indicates what the move is. For
	// example, if the appropriate move is to place a tile in row 0 column 3,
	// you'd return a new OthelloMove with row 0 and column 3.
	public OthelloMove chooseMove(OthelloGameState state) {

		boolean isBlackTurn = state.isBlackTurn();

		HashMap<OthelloMove, Heuristic> heuristicMap = findValidMoves(state);

		System.out.println(heuristicMap.toString());

		OthelloMove bestMove = null;
		// first pass should always be better, bs and ws at least 2.
		Heuristic bestHeuristic = new Heuristic(0, 0);

		if(checkCorners(state) != null){

		}

		if (isBlackTurn) {
			for (OthelloMove m : heuristicMap.keySet()) {
				Heuristic possibleMoveHeuristic = heuristicMap.get(m);
				if(possibleMoveHeuristic.getBlackScore() > bestHeuristic.getBlackScore()){
					bestMove = m;
					bestHeuristic = possibleMoveHeuristic;
				}
			}
		} else {
			for (OthelloMove m : heuristicMap.keySet()) {
				Heuristic possibleMoveHeuristic = heuristicMap.get(m);
				if(possibleMoveHeuristic.getWhiteScore() > bestHeuristic.getWhiteScore()){
					bestMove = m;
					bestHeuristic = possibleMoveHeuristic;
				}
			}
		}
		
		return bestMove;
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
	}

}
