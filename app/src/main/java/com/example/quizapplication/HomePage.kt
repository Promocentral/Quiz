package com.example.quizapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.quizapplication.ui.theme.QuizApplicationTheme

class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizApplicationTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home(navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    var isDrawerOpen by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
//                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(200.dp))
        }

        TopAppBar(
            title = { Text(text = "") },
            navigationIcon = {
                Row {
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "AccountCircle",
                            modifier = Modifier.size(70.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            navController.navigate("LoginPage")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Settings",
                            modifier = Modifier.size(70.dp)
//                                .padding(end = 10.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.LightGray)
        )

        var selectedIcon by remember { mutableStateOf(Icons.Default.Home) }

        BottomAppBar(
//            colors = BottomAppBarDefaults.(containerColor = Color.LightGray),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(color = Color.LightGray)
                .height(70.dp),
            containerColor = Color.LightGray,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.navigate("HomePage")
                        selectedIcon = Icons.Default.Home
                    },
                    modifier = Modifier
                        .weight(1f)
//                        .height(150.dp)
//                        .height(70.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .background(if (selectedIcon == Icons.Default.Home) Color.Gray else Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        navController.navigate("SearchPage")
                        selectedIcon = Icons.Default.Search
                    },
                    modifier = Modifier
                        .weight(1f)
//                        .height(150.dp)
                        .height(70.dp)
                        .fillMaxWidth()
//                        .background(if (selectedIcon == Icons.Default.Search) Color.LightGray else Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Searching Quizzes",
                        modifier = Modifier.size(70.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        navController.navigate("AccountStatsPage")
                        selectedIcon = Icons.Default.Settings
                    },
                    modifier = Modifier
                        .weight(1f)
//                        .height(150.dp)
                        .height(70.dp)
                        .fillMaxWidth()
//                        .background(if (selectedIcon == Icons.Default.AccountCircle) Color.LightGray else Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Account Information",
                        modifier = Modifier.size(70.dp)
                    )
                }
            }
        }
    }
}