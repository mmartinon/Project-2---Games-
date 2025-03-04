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

    public int[] actions(int[][] state)
    {
        ArrayList<Integer> moves = new ArrayList<Integer>();

        for(int i=0; i<7; i++)
        {
            if(state[i][0] == -1)
            {
                moves.add(i);
            }
        }

        int[] availActions = new int[moves.size()];
        for(int i=0; i<availActions.length; i++)
        {
            availActions[i] = moves.get(i);
        }

        return availActions;  
    }

    public boolean terminalTest() {
        if (model instanceof ConnectFourModel) {
            ConnectFourModel gameModel = (ConnectFourModel) model;
            return gameModel.checkForWinner() > 0 || gameModel.checkForDraw();
        }
        return false; // Fallback, should never happen in normal game execution
    }
}
