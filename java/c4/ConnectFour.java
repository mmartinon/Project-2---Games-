package c4;

import c4.mvc.ConnectFourController;
import c4.mvc.ConnectFourModel;
import c4.players.ConnectFourPlayer;
import c4.players.ConnectFourPlayer.ConnectFourAlphaBetaPlayer;
import c4.players.ConnectFourRandomPlayer;

public class ConnectFour {

    public static void main(String[] args) {
        playBatchGames(50);
    }
    
    public static void playSingleGame() {
        ConnectFourModel m = new ConnectFourModel();
        ConnectFourPlayer player1 = new ConnectFourAlphaBetaPlayer(m);
        ConnectFourPlayer player2 = new ConnectFourAlphaBetaPlayer(m);
        
        ConnectFourController c = new ConnectFourController(m, player1, player2, true);
        c.start();
    }
    
    public static void playBatchGames(int plays) {
        int[] results = {0, 0, 0};  
        int[] forfeits = {0, 0, 0}; 
        
        for (int i = 0; i < plays; i++) {
            try {
                ConnectFourModel m = new ConnectFourModel();
                ConnectFourPlayer player1 = new ConnectFourAlphaBetaPlayer(m);
                ConnectFourPlayer player2 = new ConnectFourRandomPlayer(m);
                
                ConnectFourController c = new ConnectFourController(m, player1, player2, false);
                int winner = c.start();
                results[winner] += 1;
            }
            catch (IllegalMoveException e) {
                int player = e.getPlayer();
                forfeits[player] += 1;
                results[3 - player] += 1;  
            }
        }
        System.out.print("Player 1 record (W-L-D): " + results[1] + "-" + results[2] + "-" + results[0]);
        System.out.println(" (including " + forfeits[1] + " forfeits)");
        System.out.print("Player 2 record (W-L-D): " + results[2] + "-" + results[1] + "-" + results[0]);
        System.out.println(" (including " + forfeits[2] + " forfeits)");
    }
}
