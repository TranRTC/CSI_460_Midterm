package com.example.rolldice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BankAccount : AppCompatActivity() {



    private lateinit var account: Account
    private lateinit var balanceText: TextView
    private lateinit var amountInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bank_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Get account object (non-null) passed from "MainActivity"
        account = intent.getParcelableExtra("account") ?: return


        // Bind UI elements from Layout
        val infoText = findViewById<TextView>(R.id.textAccountInfo)
        val nameText = findViewById<TextView>(R.id.textAccountBankName)
        val numberText = findViewById<TextView>(R.id.textAccountNumber)
        balanceText = findViewById(R.id.textAccountBalance)
        amountInput = findViewById(R.id.editAmount)

        val depositBtn = findViewById<Button>(R.id.buttonDeposit)
        val withdrawBtn = findViewById<Button>(R.id.buttonWithdraw)


        // Show initial account information
        infoText.text = "Account Information"
        nameText.text = "Bank Name: ${account.bankName}"
        numberText.text = "Account Number: ${account.accountNumber}"
        updateBalanceText()

        // Handle deposit button click
        depositBtn.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull()
            if (amount != null && amount > 0) {
                account.balance += amount
                updateBalanceText()
                returnResult()
            } else {
                amountInput.error = "Enter valid amount"
            }
        }

        // Handle withdraw button click
        withdrawBtn.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull()
            if (amount != null && amount > 0) {
                if (amount > account.balance) {
                    amountInput.error = "Insufficient funds"
                } else {
                    account.balance -= amount
                    updateBalanceText()
                    returnResult()
                }
            } else {
                amountInput.error = "Enter valid amount"
            }
        }

    }

    // Update balance TextView with current balance
    private fun updateBalanceText() {
        balanceText.text = "Balance: $${String.format("%.2f", account.balance)}"
    }

    // prepare and return result to MainActivity
    private fun returnResult() {
        val intent = Intent()
        intent.putExtra("updatedAccount", account)
        setResult(RESULT_OK, intent)
        finish()
    }


}