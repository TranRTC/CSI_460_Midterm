package com.example.rolldice

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BankAccountDetails : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bank_account_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // get update account
        val account = intent.getParcelableExtra<Account>("updatedAccount")

        // declare variables hook with UI elements
        val nameText = findViewById<TextView>(R.id.textAccountBankName)
        val numberText = findViewById<TextView>(R.id.textAccountNumber)
        val balanceText = findViewById<TextView>(R.id.textAccountBalance)

        // assign value from the object account to the respective field for display

        if (account != null) {
            nameText.text = "Bank Name: ${account.bankName}"
            numberText.text = "Account Number: ${account.accountNumber}"
            balanceText.text = "Balance: $${String.format("%.2f", account.balance)}"


        }
    }


}