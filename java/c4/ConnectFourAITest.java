package c4;

import c4.mvc.ConnectFourModel;
import c4.players.ConnectFourAIPlayer;

/**
 * Test suite for ConnectFourAIPlayer to validate its decision-making.
 */
public class ConnectFourAITest {
    public static void main(String[] args) {
        testAIPlayerMove();
        testTerminalTest();
    }

    /**
     * Tests whether the AI selects a valid move.
     */
    public static void testAIPlayerMove() {
        System.out.println("=== Testing AI Player Move ===");
        ConnectFourModel model = new ConnectFourModel();
        model.initialize();

        ConnectFourAIPlayer aiPlayer = new ConnectFourAIPlayer(model);
        int move = aiPlayer.getMove();
        
        boolean validMove = move >= 0 && move <= 6 && model.getGrid()[move][0] == -1;
        
        if (validMove) {
            System.out.println("AI selected a valid move: " + move);
        } else {
            System.out.println("AI selected an invalid move: " + move);
        }
    }

    /**
     * Tests whether terminalTest correctly detects game-ending conditions.
     */
    public static void testTerminalTest() {
        System.out.println("\n=== Testing Terminal Test ===");
        ConnectFourModel model = new ConnectFourModel();
        model.initialize();
        
        ConnectFourAIPlayer aiPlayer = new ConnectFourAIPlayer(model);
        
        // Initially, the game should not be terminal
        if (!aiPlayer.terminalTest(model.getGrid())) {
            System.out.println("Terminal test correctly identified that the game is ongoing.");
        } else {
            System.out.println("Terminal test incorrectly detected a game end.");
        }

        // Simulate a winning condition
        for (int i = 0; i < 4; i++) {
            model.setGridPosition(0, model.getTurn()); // Player places four in a column
        }

        if (aiPlayer.terminalTest(model.getGrid())) {
            System.out.println("Terminal test correctly detected a win condition.");
        } else {
            System.out.println("Terminal test failed to detect a win condition.");
        }
    }
}
