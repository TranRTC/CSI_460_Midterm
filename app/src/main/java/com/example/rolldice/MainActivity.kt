package com.example.rolldice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Store the account at class level
    private lateinit var account: Account


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Initialize the account only once
        account = Account("123456", 1000.0, "US Bank")



        val buttonApp1 = findViewById<Button>(R.id.buttonApp1)
        val buttonApp2 = findViewById<Button>(R.id.buttonApp2)


        // Event Listener for buttonApp1
        buttonApp1.setOnClickListener {
            val intent = Intent(this, RollDice::class.java)
            startActivity(intent)
        }

        // Event Listener for buttonApp2
        buttonApp2.setOnClickListener {
            val intent = Intent(this, BankAccount::class.java)
            intent.putExtra("account", account)
            startActivityForResult(intent, 1)
        }
    }

    // Receive updated account back from BankAccount
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val updatedAccount = data?.getParcelableExtra<Account>("updatedAccount")

            updatedAccount?.let {
                // Update stored account
                val oldBalance = account.balance
                account = it

                // Show result (optional)
                val change = account.balance - oldBalance
                val status = when {
                    change > 0 -> "increased by $${"%.2f".format(change)}"
                    change < 0 -> "decreased by $${"%.2f".format(-change)}"
                    else -> "not changed"
                }

                findViewById<TextView>(R.id.textCurrentAccountBalance)?.text =
                    "Account Balance ${status}\nCurrent: $${"%.2f".format(account.balance)}"
            }
        }
    }


}