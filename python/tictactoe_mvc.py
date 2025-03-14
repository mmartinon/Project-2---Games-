class TicTacToeModel:
    def __init__(self):
        self.__grid = None
        self.__turn = -1
        self.__grid_observers = []
        self.__result_observers = []

    def initialize(self):
        self.__grid = [[None, None, None], [None, None, None], [None, None, None]]
        self.__turn = 'X'
        self.__notify_grid_observers()

    def set_position(self, pos, player):
        if pos < 1 or pos > 9:
            raise Exception()

        pos -= 1
        row = pos // 3
        col = pos % 3
        self.__grid[row][col] = player
        self.__notify_grid_observers()

        result = self.check_for_winner()
        if result is not None:
            self.__notify_result_observers(result)
        elif self.check_for_draw():
            self.__notify_result_observers(0)

    def __notify_grid_observers(self):
        for o in self.__grid_observers:
            o.update_grid()

    def __notify_result_observers(self, result):
        for o in self.__result_observers:
            o.report_result(result)

    def check_for_winner(self):
        state = self.__grid
        for row in range(3):
            if state[row][0] is not None and state[row][0] == state[row][1] and state[row][0] == state[row][2]:
                return state[row][0]
        for col in range(3):
            if state[0][col] is not None and state[0][col] == state[1][col] and state[0][col] == state[2][col]:
                return state[0][col]
        if state[0][0] is not None and state[0][0] == state[1][1] and state[0][0] == state[2][2]:
            return state[0][0]
        if state[2][0] is not None and state[2][0] == state[1][1] and state[2][0] == state[0][2]:
            return state[2][0]

        return None  # No winner

    def check_for_draw(self):
        all_filled = True
        for row in range(3):
            for col in range(3):
                if self.__grid[row][col] is None:
                    all_filled = False
        return all_filled

    def next_player(self):
        if self.__turn == 'X':
            self.__turn = 'O'
        else:
            self.__turn = 'X'

    def get_turn(self):
        return self.__turn

    def get_grid(self):
        return self.__grid

    def get_valid_moves(self):
        moves = []
        for row in range(3):
            for col in range(3):
                if self.__grid[row][col] is None:
                    moves.append(row * 3 + col + 1)
        return moves

    def register_grid_observer(self, o):
        self.__grid_observers.append(o)

    def register_result_observer(self, o):
        self.__result_observers.append(o)

    def remove_grid_observer(self, o):
        try:
            self.__grid_observers.remove(o)
        except ValueError:
            pass

    def remove_result_observer(self, o):
        try:
            self.__result_observers.remove(o)
        except ValueError:
            pass


class TicTacToeView:
    def __init__(self, model, con):
        self.model = model
        self.controller = con
        self.game_over = False
        self.grid_output = ''

    def create_view(self):
        self.grid_output = '- - -\n'*3
        print(self.grid_output)

        self.model.register_grid_observer(self)
        self.model.register_result_observer(self)

    def play_game(self):
        self.game_over = False
        while not self.game_over:
            player_char = self.model.get_turn()
            if player_char == 'X':
                print('Current turn: X')
            else:
                print('Current turn: O')

            player = self.controller.get_player(player_char)
            move = player.get_move()
            self.controller.place_token(move)

    def update_grid(self):
        grid = self.model.get_grid()
        grid_output = ''

        for row in range(3):
            text = ''
            for col in range(3):
                if grid[row][col] is None:
                    text += '- '
                else:
                    text += grid[row][col] + ' '
            text += '\n'
            grid_output += text

        print(grid_output)

    def announce_winner(self, winner):
        print('Player '+str(winner)+' wins!')

    def announce_draw(self):
        print("It's a draw!")

    def report_result(self, result):
        self.game_over = True


class TicTacToeController:
    def __init__(self, model, p1, p2):
        self.players = [p1,p2]
        self.model = model
        self.model.initialize()
        self.model.register_result_observer(self)
        self.game_winner = None
        self.view = TicTacToeView(model, self)

    def start(self):
        self.view.create_view()
        self.view.play_game()
        return self.game_winner

    def place_token(self, move):
        self.model.set_position(move, self.model.get_turn())
        self.model.next_player()

    def reset(self):
        self.model.initialize()

    def quit(self):
        import sys
        sys.exit(0)

    def get_player(self, p):
        if p == 'X':
            return self.players[0]
        return self.players[1]

    def report_result(self, result):
        self.game_winner = result
        if result == 0:
            self.view.announce_draw()
        else:
            self.view.announce_winner(result)


