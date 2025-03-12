package c4.players;

import java.util.ArrayList;

import c4.mvc.ConnectFourModel;

public class ConnectFourAIPlayer extends ConnectFourPlayer {
    private ConnectFourModel model;
    private int maxDepth;
    
    public ConnectFourAIPlayer(ConnectFourModel model) {
        this.model = model;
        this.maxDepth = 5; // default max depth
    }

    public ConnectFourAIPlayer(ConnectFourModel model, int maxDepth) {
        this.model = model;
        this.maxDepth = maxDepth;
    }

    @Override
    public int getMove() {
        return alphaBetaSearch(model.getGrid());
    }

    private int alphaBetaSearch(int[][] state) {
        int bestMove = -1;
        int bestValue = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        
        for (int action : actions(state)) {
            int[][] newState = result(state, action);
            
            int value = minValue(newState, alpha, beta, 1);
            
            if (value > bestValue) {
                bestValue = value;
                bestMove = action;
            }
            alpha = Math.max(alpha, bestValue);
        }
        return bestMove;
    }

    private int maxValue(int[][] state, int alpha, int beta, int depth) {
        if (terminalTest(state) || depth >= maxDepth) {
            return utility(state);
        }

        int value = Integer.MIN_VALUE;
        for (int action : actions(state)) {
            value = Math.max(value, minValue(result(state, action), alpha, beta, depth + 1));
            if (value >= beta) return value;
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    private int minValue(int[][] state, int alpha, int beta, int depth) {
        if (terminalTest(state) || depth >= maxDepth) {
            return utility(state);
        }

        int value = Integer.MAX_VALUE;
        for (int action : actions(state)) {
            value = Math.min(value, maxValue(result(state, action), alpha, beta, depth + 1));
            if (value <= alpha) return value;
            beta = Math.min(beta, value);
        }
        return value;
    }

    private int utility(int[][] state) {
        int winner = model.checkForWinner();
        int currentPlayer = model.getTurn();

        if (winner == currentPlayer) return 1000;
        if (winner > 0) return -1000;
        if (model.checkForDraw()) return 0;

        return heuristic(state);
    }

    private int heuristic(int[][] state) {
        int score = 0;
        int aiPlayer = model.getTurn();
        int opponent = aiPlayer == 1 ? 2 : 1;

        // Reward center column moves
        for (int row = 0; row < 6; row++) {
            if (state[3][row] == aiPlayer) score += 5; // More points for center
            if (state[3][row] == opponent) score -= 5;
        }

        return score;
    }

    private boolean terminalTest(int[][] state) {
        return model.checkForWinner() > 0 || model.checkForDraw();
    }


    public int[] actions(int[][] state) {
        ArrayList<Integer> moves = new ArrayList<Integer>();

        for(int col=0; col<7; col++) {
            if(state[col][0] == -1) {
                moves.add(col);
            }
        }

        return moves.stream().mapToInt(i -> i).toArray();  
    }
    
    public int[][] result(int[][] state, int action) {
        int[][] newState = new int[state.length][state[0].length];
        for (int col = 0; col < state.length; col++) {
            newState[col] = state[col].clone();
        }

        
        for (int row = 5; row >= 0; row--) {
            if (newState[action][row] == -1) {
                newState[action][row] = model.getTurn();
                break;
            }
        }
        return newState;
    }
    //     //copy board
    //     int[][] newState = new int[7][6];
	// 	for(int row=0; row<6; row++) {
	// 		for(int col=0; col<7; col++) {
	// 			newState[row][col] = state[row][col];
    //         }
    //     }
		
    //     //determine turn by counting how many times each player played
    //     int player = 0;
    //     int num1s = 0;
    //     int num2s = 0;

    //     for(int row=0; row<6; row++) {
    //         for(int col=0; col<7; col++) {
    //             if(state[col][row] == 1) {
    //                 num1s++;
    //             }
    //             else if(state[col][row] == 2) {
    //                 num2s++;
    //             }
    //         }
    //     }

    //     if(num1s > num2s) {
    //         player = 2;
    //     }
    //     else {
    //         player = 1;
    //     }

    //     //update board
    //     int row = 5;
    //     while(state[6][row] != -1) { //this should be guaranteed not to be an infinite loop by the actions method
    //         row--; 
    //     }
    //     newState[action][row] = player;

    //     //return
	// 	return newState;
    // }

    // public boolean terminalTest() {
    //     if (model instanceof ConnectFourModel) {
    //         ConnectFourModel gameModel = (ConnectFourModel) model;
    //         return gameModel.checkForWinner() > 0 || gameModel.checkForDraw();
    //     }
    //     return false; // Fallback, should never happen in normal game execution
    // }
    
}

