package com.example.dustbinpro  // Adjust package name if needed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onBookingClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onAdminDashboardClick: () -> Unit,
    onAssignWorkersClick: () -> Unit,
    onCustomerListClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onPaymentHistoryClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to DustbinPro!",
                    style = MaterialTheme.typography.headlineMedium
                )


                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onBookingClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Book a Cleaning Service")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onPaymentClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Make a Payment")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onAdminDashboardClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Admin Dashboard")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onAssignWorkersClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Assign Workers")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onCustomerListClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Customer List")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDashboardClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Dashboard")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onLoginClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Login")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onRegisterClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Register")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onPaymentHistoryClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Payment History")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen(
        onBookingClick = {},
        onPaymentClick = {},
        onAdminDashboardClick = {},
        onAssignWorkersClick = {},
        onCustomerListClick = {},
        onDashboardClick = {},
        onLoginClick = {},
        onRegisterClick = {},
        onPaymentHistoryClick = {}
    )
}
