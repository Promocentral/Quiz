package com.example.quizapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                    startDestination = "SignUpPage"
                ) {
                    composable("LoginPage") {
                        LoginPage(navController)
                    }
                    composable("SignUpPage"){
                        SignUp(navController)
                    }
                }
            }
        }
    }
}


@Composable
fun LoginPage(navController: NavController){

}

fun loginWithFirebase(email: String, password: String, navController: NavController){
    val firebase = FirebaseAuth.getInstance()
    firebase.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener() { task ->
            if(task.isSuccessful){
                navController.navigate("HomePage")
            } else {
                navController.navigate("LoginPage")
            }
        }
}