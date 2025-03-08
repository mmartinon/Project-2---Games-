package c4.players;

import c4.mvc.ConnectFourModel;
import c4.mvc.ConnectFourModelInterface;
import java.util.ArrayList;

public class ConnectFourAIPlayer extends ConnectFourPlayer {
    private ConnectFourModelInterface model;
    private int me;
    private int opponent;

    public ConnectFourAIPlayer(ConnectFourModelInterface model) {
        this.model = model;
    }

    // Original getMove() comment: Called by the game controller to obtain the AI's next move.
    @Override
    public int getMove() {
        me = model.getTurn();
        opponent = (me == 1) ? 2 : 1;
        return alphaBetaSearch();
    }

    // Original terminalTest() comment: Returns true if the game is over.
    public boolean terminalTest() {
        return terminalTest(getBoardCopy());
    }

    // Original actions() comment: Returns available actions.
    public int[] actions(int[][] state) {
        ArrayList<Integer> moves = new ArrayList<Integer>();
        for (int i = 0; i < 7; i++) {
            if (state[i][0] == -1) {
                moves.add(i);
            }
        }
        int[] availActions = new int[moves.size()];
        for (int i = 0; i < availActions.length; i++) {
            availActions[i] = moves.get(i);
        }
        return availActions;
    }

    // Original results() comment: Returns a new board state after applying the action.
    public int[][] results(int[][] state, int action) {
        int[][] newState = new int[7][6];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                newState[col][row] = state[col][row];
            }
        }
        int player = 0;
        int num1s = 0;
        int num2s = 0;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (state[col][row] == 1) {
                    num1s++;
                } else if (state[col][row] == 2) {
                    num2s++;
                }
            }
        }
        if (num1s > num2s) {
            player = 2;
        } else {
            player = 1;
        }
        int row = 5;
        while (state[6][row] != -1) {
            row--;
        }
        newState[action][row] = player;
        return newState;
    }

    private int alphaBetaSearch() {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int bestValue = Integer.MIN_VALUE;
        int bestMove = -1;
        int[][] board = getBoardCopy();
        boolean[] validMoves = model.getValidMoves();
        for (int col = 0; col < 7; col++) {
            if (validMoves[col]) {
                int[][] newBoard = copyBoard(board);
                applyMove(newBoard, col, me);
                int value = minValue(newBoard, alpha, beta);
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = col;
                }
                alpha = Math.max(alpha, bestValue);
                if (alpha >= beta) {
                    break;
                }
            }
        }
        if (bestMove == -1) {
            for (int col = 0; col < 7; col++) {
                if (validMoves[col]) {
                    return col;
                }
            }
        }
        return bestMove;
    }

    private int maxValue(int[][] board, int alpha, int beta) {
        if (terminalTest(board)) {
            return utility(board);
        }
        int value = Integer.MIN_VALUE;
        for (int col = 0; col < 7; col++) {
            if (canPlay(board, col)) {
                int[][] newBoard = copyBoard(board);
                applyMove(newBoard, col, me);
                value = Math.max(value, minValue(newBoard, alpha, beta));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break;
                }
            }
        }
        return value;
    }

    private int minValue(int[][] board, int alpha, int beta) {
        if (terminalTest(board)) {
            return utility(board);
        }
        int value = Integer.MAX_VALUE;
        for (int col = 0; col < 7; col++) {
            if (canPlay(board, col)) {
                int[][] newBoard = copyBoard(board);
                applyMove(newBoard, col, opponent);
                value = Math.min(value, maxValue(newBoard, alpha, beta));
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return value;
    }

    private boolean terminalTest(int[][] board) {
        return (checkWinner(board) != 0) || isBoardFull(board);
    }

    private int utility(int[][] board) {
        int winner = checkWinner(board);
        if (winner == me) {
            return 1000;
        } else if (winner == opponent) {
            return -1000;
        }
        return 0;
    }

    private int checkWinner(int[][] board) {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 4; c++) {
                int p = board[c][r];
                if (p != 0 && p == board[c + 1][r] && p == board[c + 2][r] && p == board[c + 3][r]) {
                    return p;
                }
            }
        }
        for (int c = 0; c < 7; c++) {
            for (int r = 0; r < 3; r++) {
                int p = board[c][r];
                if (p != 0 && p == board[c][r + 1] && p == board[c][r + 2] && p == board[c][r + 3]) {
                    return p;
                }
            }
        }
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 3; r++) {
                int p = board[c][r];
                if (p != 0 && p == board[c + 1][r + 1] && p == board[c + 2][r + 2] && p == board[c + 3][r + 3]) {
                    return p;
                }
            }
        }
        for (int c = 0; c < 4; c++) {
            for (int r = 3; r < 6; r++) {
                int p = board[c][r];
                if (p != 0 && p == board[c + 1][r - 1] && p == board[c + 2][r - 2] && p == board[c + 3][r - 3]) {
                    return p;
                }
            }
        }
        return 0;
    }

    private boolean isBoardFull(int[][] board) {
        for (int c = 0; c < 7; c++) {
            if (board[c][5] == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canPlay(int[][] board, int col) {
        return board[col][5] == 0;
    }

    private void applyMove(int[][] board, int col, int player) {
        for (int r = 0; r < 6; r++) {
            if (board[col][r] == 0) {
                board[col][r] = player;
                break;
            }
        }
    }

    private int[][] getBoardCopy() {
        if (model instanceof ConnectFourModel) {
            ConnectFourModel realModel = (ConnectFourModel) model;
            int[][] original = realModel.getGrid();
            return copyBoard(original);
        }
        return new int[7][6];
    }

    private int[][] copyBoard(int[][] source) {
        int[][] target = new int[7][6];
        for (int c = 0; c < 7; c++) {
            for (int r = 0; r < 6; r++) {
                target[c][r] = source[c][r];
            }
        }
        return target;
    }
}
