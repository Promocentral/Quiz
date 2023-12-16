package com.example.quizapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapplication.ui.theme.QuizApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import java.security.AccessController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizApplicationTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "LoginPage"
                ) {
                    composable("LoginPage") {
                        Login(navController)
                    }
                    composable("SignUpPage"){
                        SignUp(navController)
                    }
                    composable("HomePage"){
                        Home(navController)
                    }
                    composable("SearchPage"){
//                        Home(navController)
                    }
                    composable("AccountStatsPage"){
//                        Home(navController)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
//            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(200.dp))

            Text(
                text = "Hello there,",
                color = Color.Black,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Login To Get Started!",
                color = Color.Black,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = { Text("Email", color = Color.Black) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .padding(start = 16.dp, end = 16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = { Text("Password", color = Color.Black) },
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .padding(start = 16.dp, end = 16.dp)
            )

            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Button(
                onClick = {
                    if (validLogin(email, password)) {
                        loginWithFirebase(email, password, navController)
                    } else {
                        errorMessage = "Invalid Details, Try Again"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 16.dp, end = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Login")
            }
        }

        var selectedIcon by remember { mutableStateOf(Icons.Default.Person) }

        BottomAppBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(color = Color.LightGray)
//                .padding(top = 10.dp)
        ) {
            IconButton(
                onClick = {
                    navController.navigate("LoginPage")
                    selectedIcon = Icons.Default.Person
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(150.dp)
                    .align(Alignment.Top)
                    .background(if (selectedIcon == Icons.Default.Person) Color.LightGray else Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Login",
                    modifier = Modifier.size(150.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    navController.navigate("SignUpPage")
                    selectedIcon = Icons.Default.AccountBox
                },
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
                    .fillMaxWidth()
//                    .align(Alignment.Top)
                    .background(if (selectedIcon == Icons.Default.AccountBox) Color.LightGray else Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Create An Account",
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    }
}

        fun validLoginEmail(email: String): Boolean {
            return email.isNotEmpty() && email.length > 8
        }

        fun validLoginPassword(password: String): Boolean {
            return password.isNotEmpty() && password.length > 5
        }

        fun validLogin(email: String, password: String): Boolean {
            return validEmail(email) && validPassword(password)
        }

        fun loginWithFirebase(email: String, password: String, navController: NavController) {
            val firebase = FirebaseAuth.getInstance()
            firebase.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        navController.navigate("HomePage")
                    } else {
                        navController.navigate("LoginPage")
                    }
                }
        }
