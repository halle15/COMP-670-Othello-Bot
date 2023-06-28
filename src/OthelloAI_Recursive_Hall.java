// OthelloAI.java
//
// COMP 670 AI Game Project: Othello
//
// This is an individual project.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OthelloAI_Recursive_Hall implements OthelloAI {

	// TUNING PARAMETERS

	int DEPTH_TUNE = 3;

	// CORNERS
	int AVAILABLE_CORNER_BONUS = 1;
	int CORNER_BONUS = 2;
	int ENEMY_CORNER_PENALTY = -3;

	// EDGES
	int AVAILABLE_EDGE_BONUS = 0;
	int EDGE_BONUS = 1;
	int ENEMY_EDGE_PENALTY = -2;

	// END TUNING PARAMETERS

	boolean IS_BLACK_SIDE;
	static ArrayList<OthelloMove> corners = new ArrayList<>();
	static ArrayList<OthelloMove> edges = new ArrayList<>();

	// HARDCODED SLOTS
	static {

		// corners (0,0), (0,7), (7,0), (7,7)
		corners.add(new OthelloMove(0, 0));
		corners.add(new OthelloMove(0, 7));
		corners.add(new OthelloMove(7, 0));
		corners.add(new OthelloMove(7, 7));

		// edges excluding corners
		for (int i = 1; i < 7; i++) {
			edges.add(new OthelloMove(0, i)); // top edge
			edges.add(new OthelloMove(7, i)); // bottom edge
			edges.add(new OthelloMove(i, 0)); // left edge
			edges.add(new OthelloMove(i, 7)); // right edge
		}
	}

	int sideHasCorners(OthelloGameState s) {
		int totalScoreOutput = 0;

		// Remove corners from edges, since they're already included in the corners list

		if (IS_BLACK_SIDE) {
			for (OthelloMove m : edges) {
				OthelloCell moveCell = s.getCell(m.getRow(), m.getColumn());
				if (moveCell == OthelloCell.BLACK) {
					if (corners.contains(m)) {
						//System.out.println("BLACK CORNER TAKEN");
						totalScoreOutput += CORNER_BONUS;
					} else {
						//System.out.println("BLACK EDGE TAKEN");
						totalScoreOutput += EDGE_BONUS;
					}
				} else if (moveCell == OthelloCell.WHITE) {
					if (corners.contains(m)) {
						//System.out.println("ENEMY CORNER TAKEN!");
						totalScoreOutput -= ENEMY_CORNER_PENALTY;
					} else {
						//System.out.println("ENEMY EDGE TAKEN!");
						totalScoreOutput -= ENEMY_EDGE_PENALTY;
					}
				}
			}
		} else {
			for (OthelloMove m : corners) {
				OthelloCell moveCell = s.getCell(m.getRow(), m.getColumn());
				if (moveCell == OthelloCell.WHITE) {
					if (corners.contains(m)) {
						//System.out.println("BLACK CORNER TAKEN");
						totalScoreOutput += CORNER_BONUS;
					} else {
						//System.out.println("BLACK EDGE TAKEN");
						totalScoreOutput += EDGE_BONUS;
					}
				} else if (moveCell == OthelloCell.BLACK) {
					if (corners.contains(m)) {
						//System.out.println("ENEMY CORNER TAKEN!");
						totalScoreOutput -= ENEMY_CORNER_PENALTY;
					} else {
						//System.out.println("ENEMY EDGE TAKEN!");
						totalScoreOutput -= ENEMY_EDGE_PENALTY;
					}
				}
			}
		}

		//System.out.println("WOULD RETURN " + totalScoreOutput);
		return totalScoreOutput;
	}

	int eval(OthelloGameState s) {
		int totalScore = 0;
		
		int gameAdvantage = IS_BLACK_SIDE ? s.getBlackScore() - s.getWhiteScore() : s.getWhiteScore() - s.getBlackScore();

		int sideAndCornerScore = sideHasCorners(s);

		return totalScore;
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

		boolean ourTurn = (s.isBlackTurn() == IS_BLACK_SIDE);

		//System.out.println("OUR TURN?: " + ourTurn);

		if (depth == 0 || isTerminalNode(s)) {
			return eval(s);
		}

		ArrayList<OthelloMove> validMoves = findValidMoves(s);

		if (ourTurn) { // MAXIMIZE SCORE

			int bestHeuristic = Integer.MIN_VALUE;

			for (OthelloMove m : validMoves) {

				OthelloGameState nextState = s.clone();
				nextState.makeMove(m.getRow(), m.getColumn());
				int branchEval = search(nextState, depth - 1);

				if (branchEval > bestHeuristic) {
					bestHeuristic = branchEval;
				}
			}

			return bestHeuristic;

		} else { // MINIMIZE SCORE

			int worstHeuristic = Integer.MAX_VALUE;

			for (OthelloMove m : validMoves) {

				OthelloGameState nextState = s.clone();
				nextState.makeMove(m.getRow(), m.getColumn());
				int branchEval = search(nextState, depth - 1);

				if (branchEval < worstHeuristic) {
					worstHeuristic = branchEval;
				}
			}

			return worstHeuristic;
		}
	}

	public boolean isTerminalNode(OthelloGameState s) {
		//System.out.println("MOVES: " + findValidMoves(s).size());
		if (findValidMoves(s).size() <= 1) {
			return true;
		}
		return false;
	}

	// TODO: Implement as a priority queue or a TreeMap
	public ArrayList<OthelloMove> findValidMoves(OthelloGameState state) {
		ArrayList<OthelloMove> validList = new ArrayList<OthelloMove>();

		// check for corners first

		for (int c = 0; c < 8; c++) {
			for (int r = 0; r < 8; r++) {
				if (state.isValidMove(r, c)) {

					// Clone the game data for the upcoming state, make the move.
					OthelloGameState nextState = state.clone();
					nextState.makeMove(r, c);

					// get heuristic values
					// pack heuristic values with the relevant next move
					validList.add(new OthelloMove(r, c));
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

		long time = System.nanoTime();

		IS_BLACK_SIDE = state.isBlackTurn();

		// TODO: take corner without even thinking

		for (OthelloMove m : corners) {
			if (state.isValidMove(m.getRow(), m.getColumn())) {
				return m;
			}
		}

		//System.out.println("WE ARE BLACK? " + IS_BLACK_SIDE);
		//System.out.println(isTerminalNode(state));

		ArrayList<OthelloMove> validMoves = findValidMoves(state);

		HashMap<OthelloMove, Integer> heuristicMap = new HashMap<OthelloMove, Integer>();

		for (OthelloMove m : validMoves) {
			OthelloGameState clonedState = state.clone();
			clonedState.makeMove(m.getRow(), m.getColumn());
			heuristicMap.put(m, search(clonedState, DEPTH_TUNE));
		}

		OthelloMove bestMove = null;
		int highestValue = Integer.MIN_VALUE;

		for (Map.Entry<OthelloMove, Integer> entry : heuristicMap.entrySet()) {
			if (entry.getValue() > highestValue) {
				highestValue = entry.getValue();
				bestMove = entry.getKey();
			}
		}

		time = (System.nanoTime() - time) / 1_000_000;

		//System.out.println("PICKING MOVE " + bestMove.hashCode());
		//System.out.println("TIME TAKEN: " + time);
		return bestMove;
	}

}
