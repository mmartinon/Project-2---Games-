package c4.players;

import java.util.ArrayList;

import c4.mvc.ConnectFourModel;

public class ConnectFourAIPlayer extends ConnectFourPlayer {
    private ConnectFourModel model;
    private int maxDepth;

    /**
     * Constructor with default depth
     */
    public ConnectFourAIPlayer(ConnectFourModel model) {
        this.model = model;
        this.maxDepth = 6; // default max search depth
    }

    /**
     * Constructor with customizable depth
     */
    public ConnectFourAIPlayer(ConnectFourModel model, int maxDepth) {
        this.model = model;
        this.maxDepth = maxDepth;
    }

    /**
     * Determines the best move using Alpha-Beta search algorithm
     */
    @Override
    public int getMove() {
        return alphaBetaSearch(model.getGrid(), maxDepth);
    }

    /**
     * Alpha-Beta search algorithm to find the best move
     */
    public int alphaBetaSearch(int[][] state, int max) {
        int bestMove = -1;
        int bestValue = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (int action : actions(state)) { // get available moves
            int[][] newState = result(state, action);

            int value = minValue(newState, alpha, beta, 1, maxDepth);

            if (value > bestValue) {
                bestValue = value;
                bestMove = action;
            }
            alpha = Math.max(alpha, bestValue);
        }
        return bestMove;
    }

    /**
     * Maximizing function for Alpha-Beta pruning
     */
    public int maxValue(int[][] state, int alpha, int beta, int depth, int maxDepth) {
        if (terminalTest(state) || depth >= maxDepth) {
            return utility(state);
        }

        int value = Integer.MIN_VALUE;
        for (int action : actions(state)) {
            value = Math.max(value, minValue(result(state, action), alpha, beta, depth + 1, maxDepth));
            if (value >= beta)
                return value;
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    /**
     * Minimizing function for Alpha-Beta pruning
     */
    public int minValue(int[][] state, int alpha, int beta, int depth, int maxDepth) {
        if (terminalTest(state) || depth >= maxDepth) {
            return utility(state);
        }

        int value = Integer.MAX_VALUE;
        for (int action : actions(state)) {
            value = Math.min(value, maxValue(result(state, action), alpha, beta, depth + 1, maxDepth));
            if (value <= alpha)
                return value;
            beta = Math.min(beta, value);
        }
        return value;
    }

    /**
     * Evaluates a board state for the ai player
     */
    public int utility(int[][] state) {
        int winner = model.checkForWinner();
        int currentPlayer = model.getTurn();
        int opponent = (currentPlayer == 1) ? 2 : 1;
    
        if (winner == currentPlayer) return 1000;
        if (winner == opponent) return -1000;
        if (model.checkForDraw()) return 0;
    
        return evaluateBoard(state, currentPlayer) - evaluateBoard(state, opponent) * 2;
    }
    
    /**
     * Evaluates board state to assign a score 
     */
    public int evaluateBoard(int[][] state, int player) {
        int score = 0;
    
        score += evaluateDirection(state, player, 1, 0); // Horizontal
        score += evaluateDirection(state, player, 0, 1); // Vertical
        score += evaluateDirection(state, player, 1, 1); // Diagonal \
        score += evaluateDirection(state, player, 1, -1); // Diagonal /
    
        score += centerControl(state, player); // Center control advantage
    
        return score;
    }
    
    /**
     * Evaluates potential four-in-a-row positions for the player.
     */
    public int evaluateDirection(int[][] state, int player, int dX, int dY) {
        int score = 0;
    
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                int count = 0, spaces = 0;
    
                for (int i = 0; i < 4; i++) { // Check 4-in-a-row windows
                    int x = col + dX * i;
                    int y = row + dY * i;
    
                    if (x >= 0 && x < 7 && y >= 0 && y < 6) {
                        if (state[x][y] == player) count++;
                        else if (state[x][y] == 0) spaces++;
                    }
                }
    
                if (count == 4) score += 1000; // Winning move
                else if (count == 3 && spaces == 1) score += 50; // Almost winning
                else if (count == 2 && spaces == 2) score += 10; // Good potential move
                else if (count == 1 && spaces == 3) score += 1; // Small advantage
            }
        }
    
        return score;
    }

    /**
     * Prioritizes controlling the center column
     */
    public int centerControl(int[][] state, int player) {
        int centerColumn = 3;
        int score = 0;
    
        for (int row = 0; row < 6; row++) {
            if (state[centerColumn][row] == player) {
                score += 5; // Prioritizing control of center
            }
        }
    
        return score;
    }

    public boolean terminalTest(int[][] state) {
        return model.checkForWinner() > 0 || model.checkForDraw();
    }

    /**
     * Generates a list of available moves (non-full columns).
     */
    public int[] actions(int[][] state) {
        ArrayList<Integer> moves = new ArrayList<Integer>();

        for (int col = 0; col < 7; col++) {
            if (state[col][0] == -1) {
                moves.add(col);
            }
        }
        return moves.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Simulates the board state after making a move.
     */
    public int[][] result(int[][] state, int action) {
        int[][] newState = new int[7][6];

        // copy the state
        for (int col = 0; col < 7; col++) {
            System.arraycopy(state[col], 0, newState[col], 0, 6);
        }

        // Drop the piece in the lowest available row
        for (int row = 5; row >= 0; row--) {
            if (newState[action][row] == -1) {
                newState[action][row] = model.getTurn();
                break;
            }
        }

        return newState;
    }

    /**
     * Print board for debugging
     */
    public void printBoard(int[][] state) {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                System.out.print((state[col][row] == -1 ? "." : state[col][row]) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
