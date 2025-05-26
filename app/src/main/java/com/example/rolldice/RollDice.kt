package com.example.rolldice

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge

class RollDice : AppCompatActivity() {


    // The variable named "currentAction" will be set to one of these values
    // to keep track of the state before each action is executed in the flow

    enum class GameAction {
        RESET, CONFIRM_BET, ROLL_DICE, APPLY_SCORE, GAME_OVER
    }

    private var currentAction = GameAction.RESET

    private var player1Points = 0
    private var player2Points = 0
    private var player1Balance = 50
    private var player2Balance = 50
    private var sharedPot = 0

    private var isPlayer1Turn = true
    private var rollCount = 0
    private var currentRollTotal = 0

    private lateinit var dice1: ImageView
    private lateinit var dice2: ImageView
    private lateinit var dice3: ImageView
    private lateinit var textStatus: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_roll_dice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI hookup
        dice1 = findViewById(R.id.dice1)
        dice2 = findViewById(R.id.dice2)
        dice3 = findViewById(R.id.dice3)
        textStatus = findViewById(R.id.textGameStatus)


        // Event listeners for buttons' press

        findViewById<Button>(R.id.buttonConfirmBetPlayer1).setOnClickListener {
            if (isPlayer1Turn) confirmBet(1)
        }
        findViewById<Button>(R.id.buttonConfirmBetPlayer2).setOnClickListener {
            if (!isPlayer1Turn) confirmBet(2)
        }

        findViewById<Button>(R.id.buttonRollPlayer1).setOnClickListener {
            if (isPlayer1Turn && currentAction == GameAction.ROLL_DICE) handleRoll(1)
        }
        findViewById<Button>(R.id.buttonRollPlayer2).setOnClickListener {
            if (!isPlayer1Turn && currentAction == GameAction.ROLL_DICE) handleRoll(2)
        }

        findViewById<Button>(R.id.buttonKeepScorePlayer1).setOnClickListener {
            if (isPlayer1Turn) applyScore(1)
        }
        findViewById<Button>(R.id.buttonKeepScorePlayer2).setOnClickListener {
            if (!isPlayer1Turn) applyScore(2)
        }


        findViewById<Button>(R.id.buttonReset).setOnClickListener {
            resetGame()
        }

        resetGame()
    }


    // ResetGame
    private fun resetGame() {
        player1Points = 0
        player2Points = 0
        player1Balance = 50
        player2Balance = 50
        sharedPot = 0
        isPlayer1Turn = true
        rollCount = 0
        currentRollTotal = 0
        currentAction = GameAction.CONFIRM_BET


        findViewById<TextView>(R.id.textPlayer1Money).text = "Balance: $50"
        findViewById<TextView>(R.id.textPlayer2Money).text = "Balance: $50"
        findViewById<TextView>(R.id.textPlayer1Score).text = "Score: 0"
        findViewById<TextView>(R.id.textPlayer2Score).text = "Score: 0"
        findViewById<TextView>(R.id.textPotTotal).text = "Winning Pot: $0"
        findViewById<EditText>(R.id.editBetPlayer1).text.clear()
        findViewById<EditText>(R.id.editBetPlayer2).text.clear()
        // after clear the input field need enable bet input for player1 here



        listOf(dice1, dice2, dice3).forEach {
            it.setImageResource(R.drawable.dice_1)
        }

        updateStatus("Game reset. Player 1's turn. Waiting for bet.")
    }


    // confirmBet

    private fun confirmBet(player: Int): Boolean {
        if (currentAction != GameAction.CONFIRM_BET) return false
        val bet = getBetAmount(player)
        if (!validateBet(player, bet)) return false
        currentAction = GameAction.ROLL_DICE
        updateStatus("Player $player placed a bet. Waiting for roll.")
        // disable the edit text after confirming bet

        return true
    }


    // handleRoll

    private fun handleRoll(player: Int) {
        if (currentAction != GameAction.ROLL_DICE) return
        val values = List(3) { (1..6).random() }
        currentRollTotal = values.sum()
        rollCount++
        updateDiceImages(values)
        updateStatus("Player $player rolled: ${values.joinToString(", ")} (Total: $currentRollTotal).")
        if (rollCount == 3) applyScore(player)
    }


    // applyScore

    private fun applyScore(player: Int) {
        if (currentAction != GameAction.ROLL_DICE) return
        val bet = getBetAmount(player)
        if (!validateBet(player, bet)) return

        if (player == 1) {
            player1Balance -= bet
            player1Points += currentRollTotal
            findViewById<TextView>(R.id.textPlayer1Money).text = "Balance: \$$player1Balance"
            findViewById<TextView>(R.id.textPlayer1Score).text = "Score: $player1Points"
        } else {
            player2Balance -= bet
            player2Points += currentRollTotal
            findViewById<TextView>(R.id.textPlayer2Money).text = "Balance: \$$player2Balance"
            findViewById<TextView>(R.id.textPlayer2Score).text = "Score: $player2Points"
        }

        sharedPot += bet
        findViewById<TextView>(R.id.textPotTotal).text = "Winning Pot: \$$sharedPot"
        currentAction = GameAction.APPLY_SCORE
        checkWinCondition(player)
    }

    // checkWinCondition
    private fun checkWinCondition(player: Int) {
        if (player1Points >= 100 || player2Points >= 100) {
            updateStatus("Player $player wins the pot! Game over.")
            disableAllButtons()
            currentAction = GameAction.GAME_OVER
        } else {
            switchTurn()
        }
    }

    //getBetAmount
    private fun getBetAmount(player: Int): Int {
        val input = if (player == 1)
            findViewById<EditText>(R.id.editBetPlayer1).text.toString()
        else
            findViewById<EditText>(R.id.editBetPlayer2).text.toString()
        return input.toIntOrNull() ?: 0
    }


    // validateBet
    private fun validateBet(player: Int, bet: Int): Boolean {
        val balance = if (player == 1) player1Balance else player2Balance
        return if (bet <= 0 || bet > balance) {
            updateStatus("Invalid bet for Player $player")
            false
        } else true
    }


    // switchTurn
    private fun switchTurn() {
        isPlayer1Turn = !isPlayer1Turn
        rollCount = 0
        currentRollTotal = 0
        currentAction = GameAction.CONFIRM_BET
        updateStatus("Turn switched. Player ${if (isPlayer1Turn) 1 else 2}'s turn. Waiting for bet.")
    }

    // updateDiceImages
    private fun updateDiceImages(values: List<Int>) {
        val imageIds = listOf(dice1, dice2, dice3)
        values.forEachIndexed { i, v ->
            val resId = resources.getIdentifier("dice_$v", "drawable", packageName)
            imageIds[i].setImageResource(resId)
        }
    }

    private fun updateStatus(message: String) {
        textStatus.text = message
    }

    // disableAllButtons
    private fun disableAllButtons() {
        findViewById<Button>(R.id.buttonRollPlayer1).isEnabled = false
        findViewById<Button>(R.id.buttonRollPlayer2).isEnabled = false
        findViewById<Button>(R.id.buttonKeepScorePlayer1).isEnabled = false
        findViewById<Button>(R.id.buttonKeepScorePlayer2).isEnabled = false
        findViewById<Button>(R.id.buttonConfirmBetPlayer1).isEnabled = false
        findViewById<Button>(R.id.buttonConfirmBetPlayer2).isEnabled = false
    }
}
