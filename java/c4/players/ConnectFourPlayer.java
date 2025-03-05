package c4.players;

import c4.mvc.ConnectFourModel;
import c4.mvc.ConnectFourModelInterface;

public abstract class ConnectFourPlayer {
    public abstract int getMove();

    public boolean isAutomated() {
        return true;
    }
    public static class ConnectFourAlphaBetaPlayer extends ConnectFourPlayer {
        private final ConnectFourModelInterface model;
        private int me;        
        private int opponent;  

        public ConnectFourAlphaBetaPlayer(ConnectFourModelInterface model) {
            this.model = model;
        }

        @Override
        public int getMove() {
            me = model.getTurn();
            opponent = (me == 1) ? 2 : 1;

            int alpha    = Integer.MIN_VALUE;
            int beta     = Integer.MAX_VALUE;
            int bestVal  = Integer.MIN_VALUE;
            int bestMove = -1;

            boolean[] validMoves = model.getValidMoves();
            for (int col = 0; col < 7; col++) {
                if (validMoves[col]) {
                    int[][] boardCopy = getBoardCopy();
                    applyMove(boardCopy, col, me);
                    int value = minValue(boardCopy, alpha, beta);
                    if (value > bestVal) {
                        bestVal  = value;
                        bestMove = col;
                    }
                    alpha = Math.max(alpha, bestVal);
                    if (alpha >= beta) {
                        break; 
                    }
                }
            }


            return (bestMove == -1) ? 3 : bestMove;
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

        private int utility(int[][] board) {
            int winner = checkWinner(board);
            if (winner == me)       return  1000;
            if (winner == opponent) return -1000;
            return 0;  
        }

        private boolean terminalTest(int[][] board) {
            return (checkWinner(board) != 0) || isBoardFull(board);
        }

        private int checkWinner(int[][] board) {
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 4; c++) {
                    int p = board[c][r];
                    if (p != 0 && p == board[c+1][r]
                               && p == board[c+2][r]
                               && p == board[c+3][r]) {
                        return p;
                    }
                }
            }
            for (int c = 0; c < 7; c++) {
                for (int r = 0; r < 3; r++) {
                    int p = board[c][r];
                    if (p != 0 && p == board[c][r+1]
                               && p == board[c][r+2]
                               && p == board[c][r+3]) {
                        return p;
                    }
                }
            }
            for (int c = 0; c < 4; c++) {
                for (int r = 0; r < 3; r++) {
                    int p = board[c][r];
                    if (p != 0 && p == board[c+1][r+1]
                               && p == board[c+2][r+2]
                               && p == board[c+3][r+3]) {
                        return p;
                    }
                }
            }
            for (int c = 0; c < 4; c++) {
                for (int r = 3; r < 6; r++) {
                    int p = board[c][r];
                    if (p != 0 && p == board[c+1][r-1]
                               && p == board[c+2][r-2]
                               && p == board[c+3][r-3]) {
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
}
