// package c4;

// import c4.mvc.ConnectFourModel;
// import c4.players.ConnectFourAIPlayer;

// public class ConnectFourAITest {
//     public static void main(String[] args) {
//         testAIPlayerMove();
//         testTerminalTest();
//     }

//     public static void testAIPlayerMove() {
//         System.out.println("=== Testing AI Player Move ===");
//         ConnectFourModel model = new ConnectFourModel();

//         model.initialize();

//         ConnectFourAIPlayer aiPlayer = new ConnectFourAIPlayer(model);

//         int move = aiPlayer.getMove();
//         boolean[] validMoves = model.getValidMoves();

//         if (validMoves[move]) {
//             System.out.println("AI selected a valid move: " + move);
//         } else {
//             System.out.println("AI selected an invalid move: " + move);
//         }
//     }

//     public static void testTerminalTest() {
//         System.out.println("\n Testing Terminal Test");
//         ConnectFourModel model = new ConnectFourModel();
        
//         model.initialize();

//         ConnectFourAIPlayer aiPlayer = new ConnectFourAIPlayer(model);

//         // Initially, the game should not be terminal
//         if (!aiPlayer.terminalTest()) {
//             System.out.println("Terminal test correctly identified that the game is ongoing.");
//         } else {
//             System.out.println("Terminal test incorrectly detected a game end.");
//         }

//         // Simulate a winning condition
//         model.setGridPosition(0, ConnectFourModel.PLAYER1);
//         model.setGridPosition(0, ConnectFourModel.PLAYER1);
//         model.setGridPosition(0, ConnectFourModel.PLAYER1);
//         model.setGridPosition(0, ConnectFourModel.PLAYER1); // Player 1 wins

//         if (aiPlayer.terminalTest()) {
//             System.out.println("Terminal test correctly detected a win condition.");
//         } else {
//             System.out.println("Terminal test failed to detect a win condition.");
//         }

//         // Reset and simulate a full board (draw)
//         model.initialize();
//         for (int col = 0; col < 7; col++) {
//             for (int row = 0; row < 6; row++) {
//                 model.setGridPosition(col, (row % 2 == 0) ? ConnectFourModel.PLAYER1 : ConnectFourModel.PLAYER2);
//             }
//         }

//         if (aiPlayer.terminalTest()) {
//             System.out.println("Terminal test correctly detected a draw.");
//         } else {
//             System.out.println("Terminal test failed to detect a draw.");
//         }
//     }
// }
