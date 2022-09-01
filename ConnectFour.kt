package connectfour

const val MIN_DIMENSION   = 5
const val MAX_DIMENSION   = 9
const val DEFAULT_ROWS    = 6
const val DEFAULT_COLUMNS = 7
const val EMPTY_SIGN      = ' '
const val PLAYER1_SIGN    = 'o'
const val PLAYER2_SIGN    = '*'

class ConnectFour {
    private lateinit var board: MutableList<MutableList<Char>>
    private var player1            = ""
    private var player2            = ""
    private var player1Score       = 0
    private var player2Score       = 0
    private var rows               = 0
    private var columns            = 0
    private var numberOfTotalGames = 0
    private var isPlayer1Turn      = true
    private var isSingleGame       = true
    private var gameIsRunning      = false

    init {
        println("Connect Four")
        setPlayerNames()
        setBoardDimensions()

        while (true) {
            println("Do you want to play single or multiple games?\n" +
                    "For a single game, input 1 or press Enter\n" +
                    "Input a number of games:")
            val input = readln()
            try {
                if (!input.matches("^[1-9]\\d*$".toRegex()) && input.isNotEmpty()) throw IllegalInputException()
                numberOfTotalGames = if (input == "1" || input.isEmpty()) 1 else input.toInt()
                if (numberOfTotalGames > 1) isSingleGame = false
                break
            } catch (e: Exception) {
                println(e.message)
            }
        }

        println("$player1 VS $player2\n$rows X $columns board")
        println(if (numberOfTotalGames == 1) "Single game" else "Total $numberOfTotalGames games")
    }

    /** Asks the first and second player names */
    private fun setPlayerNames() {
        println("First player's name:")
        player1 = readln()
        println("Second player's name:")
        player2 = readln()
    }

    /** Asks the board dimensions and initializes the board */
    private fun setBoardDimensions() {
        while (true) {
            println("Set the board dimensions (Rows x Columns)\nPress Enter for default (6 x 7)")
            val input = readln().replace("\\s".toRegex(), "").replace("[xX]".toRegex(), " x ")

            try {
                if (input.isNotEmpty() && !input.matches("\\d+ x \\d+".toRegex())) throw IllegalInputException()
                rows    = if (input.isEmpty()) DEFAULT_ROWS    else input.split(" ")[0].toInt()
                columns = if (input.isEmpty()) DEFAULT_COLUMNS else input.split(" ")[2].toInt()
                if (   rows !in MIN_DIMENSION..MAX_DIMENSION) throw IllegalDimensionException("rows"   )
                if (columns !in MIN_DIMENSION..MAX_DIMENSION) throw IllegalDimensionException("columns")
                break
            } catch (e: Exception) {
                println(e.message)
            }
        }
        board = MutableList(rows) { MutableList(columns) { EMPTY_SIGN } }
    }

    /** Draws the board */
    private fun printBoard() {
        println((1..columns).joinToString(prefix = " ", separator = " "))
        for (row in board) {
            println(row.joinToString(prefix = "|", separator = "|", postfix = "|"))
        }
        println("=".repeat(columns * 2 + 1))
    }

    /** Performs the next move, returns false if user input is end */
    private fun nextMove(): Boolean {
        while (true) {
            try {
                println("${if (isPlayer1Turn) player1 else player2}'s turn:")
                val column = readln()
                if (column == "end") {
                    return false
                } else if (!column.matches("\\d+".toRegex())) {
                    throw NumberFormatException("Incorrect column number")
                }
                if (column.toInt() - 1 !in 0 until columns) throw ColumnOutOfRangeException(columns)
                updateBoard(column.toInt() - 1)
                return true
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    /** Updates the board, throws FullColumnException if the column is full */
    private fun updateBoard(column: Int) {
        for (row in board.asReversed()) {
            if (row[column] == EMPTY_SIGN) {
                row[column] = if (isPlayer1Turn) PLAYER1_SIGN else PLAYER2_SIGN
                return
            }
        }
        throw FullColumnException(column + 1)
    }

    /** Returns true if someone has won the game */
    private fun someoneHasWon(): Boolean {
        val sign = if (isPlayer1Turn) PLAYER1_SIGN else PLAYER2_SIGN

        // Check horizontal lines and vertical lines
        for (i in board.indices) {
            if (board[i].joinToString("").contains(sign.toString().repeat(4))) {
                return true
            }
            if (i < board.size - 3) {
                for (j in board[i].indices) {
                    if (board[i][j] == sign &&
                        board[i][j] == board[i + 1][j] && board[i][j] == board[i + 2][j] && board[i][j] == board[i + 3][j]) {
                        return true
                    }
                }
            }
        }

        // Check diagonal lines
        for (i in 0 until board.size - 3) {
            for (j in 0 until board[i].size) {
                if (j < board[i].size - 3 && board[i][j] == sign &&
                    board[i][j] == board[i + 1][j + 1] && board[i][j] == board[i + 2][j + 2] && board[i][j] == board[i + 3][j + 3]) {
                    return true
                }
                if (j > 2 && board[i][j] == sign &&
                    board[i][j] == board[i + 1][j - 1] && board[i][j] == board[i + 2][j - 2] && board[i][j] == board[i + 3][j - 3]) {
                    return true
                }
            }
        }
        return false
    }

    /** Returns true if the board is completely full */
    private fun boardIsFull(): Boolean {
        for (row in board) if (row.joinToString("").contains(" ")) return false
        return true
    }

    /** Runs a single game */
    private fun run() {
        printBoard()
        while (nextMove()) {
            printBoard()
            if (someoneHasWon()) {
                println("Player ${if (isPlayer1Turn) player1 else player2} won")
                if (isPlayer1Turn) player1Score+=2 else player2Score+=2
                break
            }
            if (boardIsFull()) {
                println("It is a draw")
                player1Score++
                player2Score++
                break
            }
            isPlayer1Turn = !isPlayer1Turn
        }
    }

    /** Resets the board */
    private fun resetBoard() {
        for (i in board.indices) {
            for (j in board[i].indices) {
                board[i][j] = EMPTY_SIGN
            }
        }
    }

    /** Starts the game */
    fun start() {
        if (gameIsRunning) {
            println("Game is already started")
            return
        }
        gameIsRunning = true

        var currentGame = 1
        while (currentGame <= numberOfTotalGames) {
            if (!isSingleGame) println("Game #${currentGame}")
            run()
            if (!isSingleGame) {
                println("Score\n$player1: $player1Score $player2: $player2Score")
                isPlayer1Turn = !isPlayer1Turn
                resetBoard()
            }
            currentGame++
        }
        println("Game over!")
    }
}
