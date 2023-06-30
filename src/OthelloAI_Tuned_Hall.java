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

public class OthelloAI_Tuned_Hall implements OthelloAI {

	// TUNING PARAMETERS
	int ALPHA_PARAMETER = -10;
	int BETA_PARAMETER = 20;

	int DEPTH_TUNE = 6;
	int IT_DEEPENING_PARAMETER = 25;

	double SCORE_TUNE = 1;
	double MOBILITY_TUNE = 2;

	// CORNERS
	int AVAILABLE_CORNER_BONUS = 3;
	int CORNER_BONUS = 0;
	int ENEMY_CORNER_PENALTY = -2;

	// EDGES	
	int AVAILABLE_EDGE_BONUS = 2;
	int EDGE_BONUS = 0;
	int ENEMY_EDGE_PENALTY = -1;

	// END TUNING PARAMETERS

	// GENERAL GAME INFO
	boolean IS_BLACK_SIDE;
	int moves = 0;

	// HARDCODED SLOTS
	static ArrayList<OthelloMove> corners = new ArrayList<>();
	static ArrayList<OthelloMove> edges = new ArrayList<>();

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

		if (IS_BLACK_SIDE) {
			for (OthelloMove m : edges) {
				OthelloCell moveCell = s.getCell(m.getRow(), m.getColumn());
				if (moveCell == OthelloCell.BLACK) {
					if (corners.contains(m)) {
						// System.out.println("BLACK CORNER TAKEN");
						totalScoreOutput += CORNER_BONUS;
					} else {
						// System.out.println("BLACK EDGE TAKEN");
						totalScoreOutput += EDGE_BONUS;
					}
				} else if (moveCell == OthelloCell.WHITE) {
					if (corners.contains(m)) {
						// System.out.println("ENEMY CORNER TAKEN!");
						totalScoreOutput += ENEMY_CORNER_PENALTY;
					} else {
						// System.out.println("ENEMY EDGE TAKEN!");
						totalScoreOutput += ENEMY_EDGE_PENALTY;
					}
				}
			}
		} else {
			for (OthelloMove m : corners) {
				OthelloCell moveCell = s.getCell(m.getRow(), m.getColumn());
				if (moveCell == OthelloCell.WHITE) {
					if (corners.contains(m)) {
						// System.out.println("BLACK CORNER TAKEN");
						totalScoreOutput += CORNER_BONUS;
					} else {
						// System.out.println("BLACK EDGE TAKEN");
						totalScoreOutput += EDGE_BONUS;
					}
				} else if (moveCell == OthelloCell.BLACK) {
					if (corners.contains(m)) {
						// System.out.println("ENEMY CORNER TAKEN!");
						totalScoreOutput += ENEMY_CORNER_PENALTY;
					} else {
						// System.out.println("ENEMY EDGE TAKEN!");
						totalScoreOutput += ENEMY_EDGE_PENALTY;
					}
				}
			}
		}

		// System.out.println("WOULD RETURN " + totalScoreOutput);
		return totalScoreOutput;
	}

	int calculateGameAdvantage(OthelloGameState s) {
		return IS_BLACK_SIDE ? s.getBlackScore() - s.getWhiteScore() : s.getWhiteScore() - s.getBlackScore();
	}

	int calculateMobilityAdvantage(OthelloGameState s) {
		double mobilityScore = findValidMoves(s).size() * MOBILITY_TUNE;
		return (int) mobilityScore;
	}

	int eval(OthelloGameState s) {
		int totalScore = 0;

		int gameAdvantage = (int) (calculateGameAdvantage(s) * SCORE_TUNE);

		int mobilityAdvantage = calculateMobilityAdvantage(s);

		int sideAndCornerScore = sideHasCorners(s);

		totalScore += (gameAdvantage + mobilityAdvantage + sideAndCornerScore);

		return totalScore;
	}

	int search(OthelloGameState s, int depth, int alpha, int beta) {
		boolean ourTurn = (s.isBlackTurn() == IS_BLACK_SIDE);

		if (depth == 0 || isTerminalNode(s)) {
			return eval(s);
		}

		ArrayList<OthelloMove> validMoves = findValidMoves(s);

		if (ourTurn) { // MAXIMIZE SCORE
			int bestHeuristic = Integer.MIN_VALUE;

			for (OthelloMove m : validMoves) {
				OthelloGameState nextState = s.clone();
				nextState.makeMove(m.getRow(), m.getColumn());

				int branchEval = search(nextState, depth - 1, alpha, beta);

				if (branchEval > bestHeuristic) {
					bestHeuristic = branchEval;
				}

				alpha = Math.max(alpha, bestHeuristic);
				if (alpha >= beta) {
					// System.out.println("beta cut off");
					break; // Beta cut-off
				}
			}

			return bestHeuristic;
		} else { // MINIMIZE SCORE
			int worstHeuristic = Integer.MAX_VALUE;

			for (OthelloMove m : validMoves) {
				OthelloGameState nextState = s.clone();
				nextState.makeMove(m.getRow(), m.getColumn());

				int branchEval = search(nextState, depth - 1, alpha, beta);

				if (branchEval < worstHeuristic) {
					worstHeuristic = branchEval;
				}

				beta = Math.min(beta, worstHeuristic);
				if (beta <= alpha) {
					// System.out.println("alpha cut off");
					break; // Alpha cut-off
				}
			}

			return worstHeuristic;
		}
	}

	public boolean isTerminalNode(OthelloGameState s) {
		// System.out.println("MOVES: " + findValidMoves(s).size());
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
		moves++;
		long time = System.nanoTime();

		if (moves % IT_DEEPENING_PARAMETER == 0) {
			SCORE_TUNE += 2;
			DEPTH_TUNE++;
		}

		IS_BLACK_SIDE = state.isBlackTurn();

		// TODO: take corner without even thinking

		for (OthelloMove m : corners) {
			if (state.isValidMove(m.getRow(), m.getColumn())) {
				time = (System.nanoTime() - time) / 1_000_000;

				// System.out.println("PICKING MOVE " + bestMove.hashCode());
				System.out.println("TIME TAKEN: " + time);
				System.out.println("EVALUATION OF OUR STATE: " + eval(state));
				System.out.println("CORNER! :)");
				return m;
			}
		}

		// System.out.println("WE ARE BLACK? " + IS_BLACK_SIDE);
		// System.out.println(isTerminalNode(state));

		ArrayList<OthelloMove> validMoves = findValidMoves(state);

		HashMap<OthelloMove, Integer> heuristicMap = new HashMap<OthelloMove, Integer>();

		for (OthelloMove m : validMoves) {
			OthelloGameState clonedState = state.clone();
			clonedState.makeMove(m.getRow(), m.getColumn());
			heuristicMap.put(m, search(clonedState, DEPTH_TUNE, ALPHA_PARAMETER, BETA_PARAMETER));
		}

		OthelloMove bestMove = null;
		int highestValue = Integer.MIN_VALUE;

		List<OthelloMove> topMoves = new ArrayList<>();

		for (Map.Entry<OthelloMove, Integer> entry : heuristicMap.entrySet()) {
			int currentValue = entry.getValue();
			if (currentValue > highestValue) {
				highestValue = currentValue;
				bestMove = entry.getKey();
				topMoves.clear();
				topMoves.add(bestMove);
			} else if (currentValue == highestValue) {
				topMoves.add(entry.getKey());
			}
		}

		if (topMoves.size() > 1) {
			// Perform a quick 3-depth search on tied moves
			int bestHeuristic = Integer.MIN_VALUE;
			for (OthelloMove move : topMoves) {
				OthelloGameState clonedState = state.clone();
				clonedState.makeMove(move.getRow(), move.getColumn());
				int heuristic = search(clonedState, 3, ALPHA_PARAMETER, BETA_PARAMETER);
				if (heuristic > bestHeuristic) {
					bestHeuristic = heuristic;
					bestMove = move;
				}
			}
		}

		time = (System.nanoTime() - time) / 1_000_000;

		// Print the chosen move and evaluation
		for (int i = 0; i < 50; i++) {
			System.out.println();
		}
		System.out.println(IS_BLACK_SIDE ? "BLACK TURN " + moves : "WHITE TURN " + moves);
		System.out.println("TIME TAKEN: " + time);
		System.out.println("EVALUATION OF OUR STATE: " + eval(state));
		return bestMove;
	}

}
