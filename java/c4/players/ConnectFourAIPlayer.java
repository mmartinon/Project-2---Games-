package c4.players;

import java.util.ArrayList;

import c4.mvc.ConnectFourModel;
import c4.mvc.ConnectFourModelInterface;

public class ConnectFourAIPlayer extends ConnectFourPlayer {
    private ConnectFourModelInterface model;

    public ConnectFourAIPlayer(ConnectFourModelInterface model) {
        this.model = model;
    }

    @Override
    public int getMove() {
        boolean[] validMoves = model.getValidMoves();
        int[] preferredOrder = {3, 2, 4, 1, 5, 0, 6};

        for (int column : preferredOrder) {
            if (validMoves[column]) {
                return column;
            }
        }
        return -1;
    }

    public int[] actions(int[][] state) {
        ArrayList<Integer> moves = new ArrayList<Integer>();

        for(int i=0; i<7; i++) {
            if(state[i][0] == -1) {
                moves.add(i);
            }
        }

        int[] availActions = new int[moves.size()];
        for(int i=0; i<availActions.length; i++) {
            availActions[i] = moves.get(i);
        }

        return availActions;  
    }
    
    public int[][] results(int[][] state, int action) {
        //copy board
        int[][] newState = new int[7][6];
		for(int row=0; row<6; row++) {
			for(int col=0; col<7; col++) {
				newState[row][col] = state[row][col];
            }
        }
		
        //determine turn by counting how many times each player played
        int player = 0;
        int num1s = 0;
        int num2s = 0;

        for(int row=0; row<6; row++) {
            for(int col=0; col<7; col++) {
                if(state[col][row] == 1) {
                    num1s++;
                }
                else if(state[col][row] == 2) {
                    num2s++;
                }
            }
        }

        if(num1s > num2s) {
            player = 2;
        }
        else {
            player = 1;
        }

        //update board
        int row = 5;
        while(state[6][row] != -1) { //this should be guaranteed not to be an infinite loop by the actions method
            row--; 
        }
        newState[action][row] = player;

        //return
		return newState;
    }

    public boolean terminalTest() {
        if (model instanceof ConnectFourModel) {
            ConnectFourModel gameModel = (ConnectFourModel) model;
            return gameModel.checkForWinner() > 0 || gameModel.checkForDraw();
        }
        return false; // Fallback, should never happen in normal game execution
    }
}
