package c4.players;

import java.util.ArrayList;

import c4.mvc.ConnectFourModel;

public class ConnectFourAIPlayer extends ConnectFourPlayer {
    private ConnectFourModel model;
    private int maxDepth;

    /**
     * Constructor with default max search depth.
     * 
     * @param model
     */
    public ConnectFourAIPlayer(ConnectFourModel model) {
        this.model = model;
        this.maxDepth = 10; // default max search depth
    }

    /**
     * Constructor with customizable max search depth.
     * 
     * @param model
     * @param maxDepth
     */
    public ConnectFourAIPlayer(ConnectFourModel model, int maxDepth) {
        this.model = model;
        this.maxDepth = maxDepth;
    }

    /**
     * Determines the best move using alpha-beta search algorithm.
     * 
     * @return bestMove
     */
    @Override
    public int getMove() {
        int[][] state = copyGrid(model.getGrid()); // Copy the state of the current game grid
        System.out.println("AI is thinking...");
        int a = alphaBetaSearch(state, maxDepth);
        
        return a; // Find move with alpha-beta search
    }

    /**
     * Copies the current game grid.
     * 
     * @param grid
     * @return newGrid
     */
    private int[][] copyGrid(int[][] grid) {
        int[][] newGrid = new int[7][6];
        for (int col = 0; col < 7; col++) {
            System.arraycopy(grid[col], 0, newGrid[col], 0, 6);
        }
        return newGrid;
    }

    /**
     * Alpha-beta search algorithm to find the best move
     * 
     * @param state
     * @param maxDepth
     * @return bestMove
     */
    public int alphaBetaSearch(int[][] state, int maxDepth) {
        int bestMove = -1;
        int bestValue = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (int action : actions(state)) { // Iterate over available moves
            int[][] newState = result(state, action); // Simulate the move

            int value = minValue(newState, alpha, beta, 1, maxDepth); // Find the value of the move

            if (value > bestValue) { // Update the best move
                bestValue = value;
                bestMove = action;
            }
            alpha = Math.max(alpha, bestValue); // Update alpha
        }
        return bestMove;
    }

    /**
     * Returns a list of available moves.
     * 
     * @param state
     * @return
     */
    public int[] actions(int[][] state) {
        ArrayList<Integer> moves = new ArrayList<Integer>(); // Store available moves

        // Check for available moves
        for (int col = 0; col < 7; col++) {
            if (state[col][0] == -1) {
                moves.add(col);
            }
        }
        return moves.stream().mapToInt(i -> i).toArray(); // Convert to int array and return
    }

    /**
     * Returns new state after making a move.
     * 
     * @param state
     * @param action
     * @return
     */
    public int[][] result(int[][] state, int action) {
        int[][] newState = new int[7][6];

        // Copy the current state
        for (int col = 0; col < 7; col++) {
            System.arraycopy(state[col], 0, newState[col], 0, 6);
        }

        // Place the piece in the column
        for (int row = 5; row >= 0; row--) {
            if (newState[action][row] == -1) {
                int player = (countPieces(state) % 2 == 0) ? 1 : 2;
                newState[action][row] = player;
                break;
            }
        }

        return newState;
    }

    /**
     * Minimizing function for Alpha-Beta pruning
     * 
     * @param state
     * @param alpha
     * @param beta
     * @param depth
     * @param maxDepth
     * @return value
     */
    public int minValue(int[][] state, int alpha, int beta, int depth, int maxDepth) {
        if (terminalTest(state) || depth >= maxDepth) { // Check for terminal state
            return utility(state); // Return the utility value
        }

        int value = Integer.MAX_VALUE;
        for (int action : actions(state)) { // Iterate over available moves
            value = Math.min(value, maxValue(result(state, action), alpha, beta, depth + 1, maxDepth)); // Find the minimum value
            if (value <= alpha) // Prune the branch
                return value;
            beta = Math.min(beta, value); // Update beta
        }
        return value; // Return the minimum value
    }

    /**
     * Maximizing function for alpha-beta pruning
     * 
     * @param state
     * @param alpha
     * @param beta
     * @param depth
     * @param maxDepth
     * @return value
     */
    public int maxValue(int[][] state, int alpha, int beta, int depth, int maxDepth) {
        if (terminalTest(state) || depth >= maxDepth) { // Check for terminal state
            return utility(state); // Return the utility value
        }

        int value = Integer.MIN_VALUE;

        for (int action : actions(state)) { // Iterate over available moves
            value = Math.max(value, minValue(result(state, action), alpha, beta, depth + 1, maxDepth)); // Find the maximum value
            if (value >= beta) // Prune the branch
                return value;
            alpha = Math.max(alpha, value); // Update alpha
        }
        return value; // Return the maximum value
    }

    /**
     * Checks if the game is in a terminal state (win or draw).
     * 
     * @param state
     * @return
     */
    public boolean terminalTest(int[][] state) {
        return checkWinner(state) > 0 || isDraw(state);
    }

    /**
     * Utility function to evaluate a board state for the ai player.
     * 
     * @param state
     * @return score
     */
    public int utility(int[][] state) {
        int score = 0;
        int winner = checkWinner(state); // Check for winner
        int currentPlayer = (countPieces(state) % 2 == 0) ? 1 : 2; // Current player
        int opponent = (currentPlayer == 1) ? 2 : 1; // Opponent

        if (winner == currentPlayer)
            return 1000; // Winning move
        if (winner == opponent)
            return -1000; // Losing move
        if (isDraw(state))
            return 0; // Draw

        double opponentWeight = (countPieces(state) > 30) ? 1.2 : 0.8; // More defensive in late game
        score = evaluateBoard(state, currentPlayer) - (int)(opponentWeight * evaluateBoard(state, opponent));
       
        
        
        if (isForkMove(state, currentPlayer)) score += 100;
        if (isForkMove(state, opponent)) score -= 100;
        

        return score;
    }

    /**
     * Checks for a winner in the current state and returns the winning player.
     * 
     * @param state
     * @return player
     */
    public int checkWinner(int[][] state) {
        // Check horizontal, vertical, and diagonal wins
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                int player = state[col][row];
                if (player == -1)
                    continue; // Ignore empty cells

                // Check horizontal (→)
                if (col + 3 < 7 &&
                        state[col + 1][row] == player &&
                        state[col + 2][row] == player &&
                        state[col + 3][row] == player) {
                    return player;
                }

                // Check vertical (↓)
                if (row + 3 < 6 &&
                        state[col][row + 1] == player &&
                        state[col][row + 2] == player &&
                        state[col][row + 3] == player) {
                    return player;
                }

                // Check diagonal (↘)
                if (col + 3 < 7 && row + 3 < 6 &&
                        state[col + 1][row + 1] == player &&
                        state[col + 2][row + 2] == player &&
                        state[col + 3][row + 3] == player) {
                    return player;
                }

                // Check diagonal (↙)
                if (col - 3 >= 0 && row + 3 < 6 &&
                        state[col - 1][row + 1] == player &&
                        state[col - 2][row + 2] == player &&
                        state[col - 3][row + 3] == player) {
                    return player;
                }
            }
        }
        return 0; // No winner
    }

    /**
     * Counts the number of pieces on the board.
     * 
     * @param state
     * @return count
     */
    private int countPieces(int[][] state) {
        int count = 0;
        for (int[] column : state) {
            for (int cell : column) {
                if (cell != -1)
                    count++;
            }
        }
        return count;
    }

    /**
     * Checks if the current state is a draw.
     * 
     * @param state
     * @return boolean
     */
    public boolean isDraw(int[][] state) {
        for (int col = 0; col < 7; col++) {
            if (state[col][0] == -1) { // If any column is not full, it's not a draw
                return false; // State is not a draw
            }
        }
        return true; // All columns are full, state is a draw
    }

    /**
     * Evaluates board state to assign a score
     * 
     * @param state
     * @param player
     * @return score
     */
    public int evaluateBoard(int[][] state, int player) {
        int score = 0;

        score += evaluateDirection(state, player, 1, 0); // Horizontal
        score += evaluateDirection(state, player, 0, 1); // Vertical
        score += evaluateDirection(state, player, 1, 1); // Diagonal (↘)
        score += evaluateDirection(state, player, 1, -1); // Diagonal (↙)

        score += centerControl(state, player); // Center control advantage

        return score;
    }

    /**
     * Evaluates potential 4 in a row positions for the player
     * 
     * @param state
     * @param player
     * @param dX
     * @param dY
     * @return score
     */
    public int evaluateDirection(int[][] state, int player, int dX, int dY) {
        int score = 0;

        // Iterate over the board
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                int count = 0, spaces = 0;

                for (int i = 0; i < 4; i++) { // Check 4 in a row windows
                    int x = col + dX * i;
                    int y = row + dY * i;

                    if (x >= 0 && x < 7 && y >= 0 && y < 6) { // Check bounds
                        if (state[x][y] == player)
                            count++; // Count player pieces
                        else if (state[x][y] == -1)
                            spaces++; // Count empty spaces
                    }
                }

                if (count == 4)
                    score += 1000; // Winning move
                else if (count == 3 && spaces == 1)
                    score += 50; // 3 pieces  and 1 empty space
                else if (count == 2 && spaces == 2)
                    score += 10; // 2 pieces and 2 empty spaces
                else if (count == 1 && spaces == 3)
                    score += 1; // 1 piece and 3 empty spaces
            }
        }

        return score;
    }

    /**
     * Prioritizes controlling the center column
     * 
     * @param state
     * @param player
     * @return score
     */
    public int centerControl(int[][] state, int player) {
        int[] centerColumns = {3, 4, 5}; // Give priority to the middle 3 columns
        int score = 0;

        for (int col : centerColumns) {
            for (int row = 0; row < 6; row++) {
                if (state[col][row] == player) {
                    score += (col == 4) ? 5 : 3; // More weight to exact center (col 4)
                }
            }
        }

        return score;
    }

    /**
     * Checks if the player has a fork move.
     * 
     * @param state
     * @param player
     * @return boolean
     */
    public boolean isForkMove(int[][] state, int player) {
        int forkCount = 0;
    
        for (int action : actions(state)) {
            int[][] newState = result(state, action);
            if (checkWinner(newState) == player) {
                forkCount++;
                if (forkCount >= 2) return true; // Fork found
            }
        }
        return false;
    }

    /**
     * prints the board state (for debugging).
     * 
     * @param state
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
