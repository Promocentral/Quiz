package com.example.quizapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Visibility
import androidx.core.graphics.drawable.IconCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.quizapplication.ui.theme.QuizApplicationTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class SignUpPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizApplicationTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignUp(navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var receiveEmails by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandIn(),
        exit = shrinkOut()
    ) {
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
                    modifier = Modifier.align(CenterHorizontally)
                )

                Text(
                    text = "Create An Account!",
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(CenterHorizontally)
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

//            ClickableText(
//                text = AnnotatedString("Select Profile Picture"),
//                onClick = {
//                    openImagePicker()
//                },
//                style = TextStyle(color = Color.Blue),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 130.dp, end = 16.dp)
//                    .align(CenterHorizontally)
////                    .padding(bottom = 16.dp)
//            )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = receiveEmails,
                        onCheckedChange = { checked ->
                            receiveEmails = checked
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .padding(start = 16.dp, end = 16.dp)
                    )

                    Text(
                        text = "I want to receive emails",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }

                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (validSignUp(email, password)) {
                            signUpWithFirebase(email, password, navController)
                        } else {
                            errorMessage = "Invalid Details, Try Again"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 16.dp, end = 16.dp)
                        .align(CenterHorizontally)
                ) {
                    Text(text = "Sign Up")
                }
            }

            var selectedIcon by remember { mutableStateOf(Icons.Default.AccountBox) }

            BottomAppBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(color = Color.LightGray)
                    .height(70.dp),
                containerColor = Color.LightGray,
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
}


fun validEmail(email: String): Boolean{
    return email.isNotEmpty() && email.length > 8
}

fun validPassword(password: String): Boolean{
    return password.isNotEmpty() && password.length > 5
}

fun validSignUp(email: String, password: String): Boolean{
    return validEmail(email) && validPassword(password)
}

fun signUpWithFirebase(email: String, password: String, navController: NavController){
    val firebase = FirebaseAuth.getInstance()
    firebase.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if(task.isSuccessful){
                navController.navigate("LoginPage")
            } else {
                navController.navigate("SignUpPage")
            }
        }
}