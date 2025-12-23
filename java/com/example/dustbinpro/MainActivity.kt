package com.example.dustbinpro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dustbinpro.ui.theme.DustbinProTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure Firebase is initialized before using it
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            DustbinProTheme {
                MainScreen(
                    onBookingClick = { navigateTo(BookingActivity::class.java) },
                    onPaymentClick = { navigateTo(PaymentActivity::class.java) },
                    onAdminDashboardClick = { navigateTo(AdminDashboardActivity::class.java) },
                    onAssignWorkersClick = { navigateTo(AssignWorkersActivity::class.java) },
                    onCustomerListClick = { navigateTo(CustomerListActivity::class.java) },
                    onDashboardClick = { navigateTo(DashboardActivity::class.java) },
                    onLoginClick = { navigateTo(LoginActivity::class.java) },
                    onRegisterClick = { navigateTo(RegisterActivity::class.java) },
                    onPaymentHistoryClick = { navigateTo(PaymentHistoryActivity::class.java) }
                )
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }
}


