// OthelloAI.java
//
// COMP 670 AI Game Project: Othello
//
// This is an individual project.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OthelloAI_Evann_Hall implements OthelloAI {

	/**
	 * The maximum allowed search time for the search algorithm.
	 */
	long MAX_SEARCH_TIME = 4750;

	/**
	 * The alpha value for alpha-beta pruning.
	 */
	int ALPHA_PARAMETER = -99;

	/**
	 * The beta value for alpha-beta pruning.
	 */
	int BETA_PARAMETER = 99;

	/**
	 * The depth parameter for the search algorithm.
	 */
	int DEPTH_TUNE = 6;

	/**
	 * The parameter for iterating deepening during the game.
	 */
	int IT_DEEPENING_PARAMETER = 10; // rudimentary, update

	/**
	 * The tuning factor for the score in the evaluation function.
	 */
	double SCORE_TUNE = 2;

	/**
	 * The tuning factor for the mobility in the evaluation function.
	 */
	double MOBILITY_TUNE = 0.3;

	/**
	 * CORNER TUNING
	 */

	/**
	 * The bonus points for occupying a corner.
	 */
	int CORNER_BONUS = 5;
	/**
	 * The penalty points for the opponent occupying a corner.
	 */
	int ENEMY_CORNER_PENALTY = -15;

	// EDGES

	/**
	 * The bonus points for occupying an available edge.
	 */
	int AVAILABLE_EDGE_BONUS = 1;

	/**
	 * The bonus points for occupying an edge.
	 */
	int EDGE_BONUS = 2;

	/**
	 * The penalty points for the opponent occupying an edge.
	 */
	int ENEMY_EDGE_PENALTY = -3;

	// GENERAL GAME INFO

	/**
	 * Indicates whether the current side is the black side.
	 */
	boolean IS_BLACK_SIDE;

	/**
	 * STATISTICS
	 */

	/**
	 * The number of moves made in the game.
	 */
	int moves = 0;

	/**
	 * The number of states evaluated during the search.
	 */
	int states = 0;

	/**
	 * The timestamp when the search or move selection started.
	 */
	long time;

	/*
	 * HARDCODED SLOTS
	 */

	/**
	 * The list of corner moves.
	 */
	static ArrayList<OthelloMove> corners = new ArrayList<>();

	/**
	 * The list of edge moves.
	 */
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

	/**
	 * Starts the timer for measuring search or move selection time.
	 */
	void startTime() {
		time = System.nanoTime();
	}

	/**
	 * Checks the elapsed time since the timer started.
	 *
	 * @return The elapsed time in milliseconds.
	 */
	long checkTime() {
		return (System.nanoTime() - time) / 1_000_000; // return as ms
	}

	/**
	 * Determines the score of the current side based on corner and edge occupancy.
	 *
	 * @param s The game state to evaluate.
	 * @return The score based on corner and edge occupancy.
	 */
	int sideHasCorners(OthelloGameState s) {
		int totalScoreOutput = 0; // init evaluation score

		if (IS_BLACK_SIDE) { // check which side
			for (OthelloMove m : edges) { // get moves from static edge list
				OthelloCell moveCell = s.getCell(m.getRow(), m.getColumn()); // get move's cell
				if (moveCell == OthelloCell.BLACK) {
					if (corners.contains(m)) { // if our list contains a given move
						totalScoreOutput += CORNER_BONUS; // give the corner bonus
					} else {
						totalScoreOutput += EDGE_BONUS; // otherwise give the edge bonus
					}
				} else if (moveCell == OthelloCell.WHITE) { // if it is the enemy
					if (corners.contains(m)) { // if its in our list and a corner
						totalScoreOutput += ENEMY_CORNER_PENALTY; // give corner penalty
					} else {
						totalScoreOutput += ENEMY_EDGE_PENALTY; // otherwise give edge penalty
					}
				}
			}
		} else {
			for (OthelloMove m : edges) { // get moves from edge list
				OthelloCell moveCell = s.getCell(m.getRow(), m.getColumn()); // get move's cell
				if (moveCell == OthelloCell.WHITE) { // if ours
					if (corners.contains(m)) { // if it is a corner
						totalScoreOutput += CORNER_BONUS; // apply corner bonus
					} else {// apply edge bonus otherwise
						totalScoreOutput += EDGE_BONUS;
					}
				} else if (moveCell == OthelloCell.BLACK) { // if our enemy
					if (corners.contains(m)) { // if it is a corner
						totalScoreOutput += ENEMY_CORNER_PENALTY; // apply corner penalty
					} else {
						totalScoreOutput += ENEMY_EDGE_PENALTY; // apply edge penalty otherwise
					}
				}
			}
		}
		return totalScoreOutput; // return finished score calculation
	}

	/**
	 * Calculates the game advantage based on the difference in scores.
	 *
	 * @param s The game state to evaluate.
	 * @return The game advantage score.
	 */
	int calculateGameAdvantage(OthelloGameState s) {
		return IS_BLACK_SIDE ? s.getBlackScore() - s.getWhiteScore() : s.getWhiteScore() - s.getBlackScore();
		// simple ternary op to subtract our score from enemies.
	}

	/**
	 * Calculates the mobility advantage based on the number of valid moves.
	 *
	 * @param s The game state to evaluate.
	 * @return The mobility advantage score.
	 */
	int calculateMobilityAdvantage(OthelloGameState s) {
		double mobilityScore = findValidMoves(s).size() * MOBILITY_TUNE; // find how many moves we can do, make it more
																			// "important" based on the tune param.
		return (int) mobilityScore;
	}

	/**
	 * Evaluates the given game state and returns a score representing the advantage
	 * for the current side.
	 * 
	 * Simply calculates the sum of all helper functions, tuned by their parameter
	 * weights.
	 *
	 * @param s The game state to evaluate.
	 * @return The advantage score for the current side.
	 */
	int eval(OthelloGameState s) {
		states++;

		int totalScore = 0;

		int gameAdvantage = (int) (calculateGameAdvantage(s) * SCORE_TUNE);

		int mobilityAdvantage = calculateMobilityAdvantage(s);

		int sideAndCornerScore = sideHasCorners(s);

		totalScore += (gameAdvantage + mobilityAdvantage + sideAndCornerScore);

		return totalScore;
	}

	/**
	 * Performs a alpha/beta search to recursively search for the best game state
	 * while simultaneously cutting off branches deemed not useable.
	 *
	 * @param s     The current game state.
	 * @param depth The depth of the search tree.
	 * @param alpha The alpha value for alpha-beta pruning.
	 * @param beta  The beta value for alpha-beta pruning.
	 * @return The heuristic value of the best move.
	 */
	int search(OthelloGameState s, int depth, int alpha, int beta) {
		boolean ourTurn = (s.isBlackTurn() == IS_BLACK_SIDE); // check if our turn

		if (depth == 0 || isTerminalNode(s)) { // base case, terminate and return in this case
			return eval(s);
		}

		ArrayList<OthelloMove> validMoves = findValidMoves(s); // get our list of valid moves

		if (ourTurn) { // MAXIMIZE SCORE
			int bestHeuristic = Integer.MIN_VALUE;

			for (OthelloMove m : validMoves) { // for our valid moves
				long elapsedTime = checkTime();
				if (elapsedTime >= MAX_SEARCH_TIME) { // if we are out of time, break out and give the best solution.
					break;
				}

				OthelloGameState nextState = s.clone(); // close our sdtate
				nextState.makeMove(m.getRow(), m.getColumn()); // change the state to make the move

				int branchEval = search(nextState, depth - 1, alpha, beta); // recurse on the search function

				if (branchEval > bestHeuristic) { // if we find it's better then set our best heuristic to it
					bestHeuristic = branchEval;
				}

				alpha = Math.max(alpha, bestHeuristic); // set alpha
				if (alpha >= beta) { // cut off if we go past beta
					break; // Beta cut-off
				}
			}

			return bestHeuristic;
		} else { // MINIMIZE SCORE
			int worstHeuristic = Integer.MAX_VALUE; // init worst case

			for (OthelloMove m : validMoves) { // for their valid moves
				long elapsedTime = checkTime();
				if (elapsedTime >= MAX_SEARCH_TIME) { // ensure we aren't out of time, break if so
					break;
				}
				OthelloGameState nextState = s.clone(); // clone state
				nextState.makeMove(m.getRow(), m.getColumn()); // make move on clone

				int branchEval = search(nextState, depth - 1, alpha, beta); // recurse on search function

				if (branchEval < worstHeuristic) { // if it's worse, set our worst heuristic
					worstHeuristic = branchEval;
				}

				beta = Math.min(beta, worstHeuristic); // set beta
				if (beta <= alpha) { // if beta less than alpha, cut off
					break; // Alpha cut-off
				}
			}

			return worstHeuristic;
		}
	}

	/**
	 * Helper function to check if the given game state is a terminal node.
	 *
	 * @param s The game state to check.
	 * @return True if the state is a terminal node, False otherwise.
	 */
	public boolean isTerminalNode(OthelloGameState s) {
		if (findValidMoves(s).size() <= 1) { // TODO: see if there is a way to improve this, maybe set a flag?
			return true;
		}
		return false;
	}

	/**
	 * Finds valid moves in the given game state.
	 * TODO: Implement priority queue or treemap?
	 *
	 * @param state The game state to find valid moves in.
	 * @return An ArrayList of valid moves.
	 */
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

	/**
	 * Chooses the best move for the current game state.
	 *
	 * @param state The current game state.
	 * @return The best move to make.
	 */

	public OthelloMove chooseMove(OthelloGameState state) {
		moves++;
		startTime();

		// very rudimentary depth tuning
		if (moves == 6) {
			DEPTH_TUNE -= 1;
		}

		// very rudimentary depth tuning
		// if moves hits the cycle number of IT_DEEPENING_PARAMETER, focus more on
		// score, search further, focus more on edges a bit
		if (moves % IT_DEEPENING_PARAMETER == 0) {
			SCORE_TUNE += 4;
			EDGE_BONUS += 1;
			DEPTH_TUNE++;
		}

		IS_BLACK_SIDE = state.isBlackTurn(); // figure out if we are black or not

		// take corner without thinking
		// may want to change, might lead to undesirable outcomes in the long run
		for (OthelloMove m : corners) {
			if (state.isValidMove(m.getRow(), m.getColumn())) {
				time = (System.nanoTime() - time) / 1_000_000;
				return m;
			}
		}

		ArrayList<OthelloMove> validMoves = findValidMoves(state); // prepare a list of valid moves

		HashMap<OthelloMove, Integer> heuristicMap = new HashMap<OthelloMove, Integer>(); // prepare a map of heuristics
																							// compared to the moves

		states = 0; // reset state stats.

		// for all of our moves, put in the heuristic calculated for the given move into
		// our map
		for (OthelloMove m : validMoves) {
			OthelloGameState clonedState = state.clone();
			clonedState.makeMove(m.getRow(), m.getColumn());
			heuristicMap.put(m, search(clonedState, DEPTH_TUNE, ALPHA_PARAMETER, BETA_PARAMETER));
		}

		OthelloMove bestMove = null; // prepare best move
		int highestValue = Integer.MIN_VALUE; // prepare highest value of our move

		List<OthelloMove> topMoves = new ArrayList<>();

		// fo all of our entries in our move set/heuristic map
		for (Map.Entry<OthelloMove, Integer> entry : heuristicMap.entrySet()) {
			int currentValue = entry.getValue(); // get the heuristic
			if (currentValue > highestValue) { // if it is higher than the currently recorded value
				highestValue = currentValue; // set our highest value
				bestMove = entry.getKey(); // set our best move
				topMoves.clear(); // clear our top moves list? CHECK IF THIS IS RIGHT
				topMoves.add(bestMove); // add our best move to the move map
			} else if (currentValue == highestValue) {
				topMoves.add(entry.getKey()); // if it happens to be equal to the highestValue, add it to this list to
												// be sorted later
			}
		}

		// in the case of a tie
		if (topMoves.size() > 1) {

			// Perform a quick 3-depth search on tied moves
			int bestHeuristic = Integer.MIN_VALUE; // init bestHeuristic
			for (OthelloMove move : topMoves) { // for every move in our map
				OthelloGameState clonedState = state.clone(); // clone our state to perform the search
				clonedState.makeMove(move.getRow(), move.getColumn()); // make the move on the state to get it ready for
																		// search
				int heuristic = search(clonedState, 3, ALPHA_PARAMETER, BETA_PARAMETER); // search it!
				if (heuristic > bestHeuristic) { // if we find it, make the move!
					bestHeuristic = heuristic;
					bestMove = move;
				}
			}

		}

		time = checkTime(); // end our time recording

		return bestMove;
	}

}
