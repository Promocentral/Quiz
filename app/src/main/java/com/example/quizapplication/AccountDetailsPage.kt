package com.example.quizapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.quizapplication.ui.theme.QuizApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AccountDetailsPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizApplicationTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccountDetails(navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetails(navController: NavController) {
    val firebase = FirebaseAuth.getInstance()
    val lightPurple = Color(0xFFB19CD9)
    var profilePicture by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            profilePicture = uri
        }
    var showDialog by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .fillMaxSize()
    ){TopAppBar(
            title = { Text(text = "Account Details") },
            navigationIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "BackArrow",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            navController.navigate("HomePage")
                        }
                )
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
//                        Icon(
//                            imageVector = Icons.Default.ExitToApp,
//                            contentDescription = "Exit",
//                            modifier = Modifier.size(50.dp)
//                        )
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        containerColor = lightPurple,
                        onDismissRequest = {
                            showDialog = false
                        },
                        title = { Text("Logout", style = MaterialTheme.typography.headlineMedium) },
                        text = { Text("Are you sure you want to log out?", style = MaterialTheme.typography.bodyLarge) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                    navController.navigate("LoginPage")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                            ) {
                                Text("Confirm", color = Color.White)
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Cancel", color = Color.White)
                            }
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = lightPurple)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 100.dp, end = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val profilePictureUrl = firebaseUser.photoUrl?.toString()
                if (profilePictureUrl != null) {
                    Image(
                        painter = rememberImagePainter(data = profilePictureUrl),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .aspectRatio(1f)
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                    profilePicture?.let { uri ->
                        updateProfilePicture(firebaseUser.uid, uri) { url ->
                            if (url != null) {
                                println("New profile picture URL: $url")
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(url))
                                    .build()
                                firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d("UpdateProfile", "User profile updated.")
                                        } else {
                                            Log.e(
                                                "UpdateProfile",
                                                "Error updating profile: ${task.exception}"
                                            )
                                        }
                                    }
                            } else {
                                println("Failed to update profile picture")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Update profile picture?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = lightPurple,
                        modifier = Modifier
                            .clickable { launcher.launch("image/*") }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "AccountCircle",
                        modifier = Modifier.size(50.dp)
                    )
                    Text("Do you want to set a profile picture?")
                }

                Spacer(modifier = Modifier.height(10.dp))

                var email by remember { mutableStateOf(firebaseUser.email ?: "") }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                var password by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun updateProfilePicture(userId: String, profilePicture: Uri?, onComplete: (String?) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val imageName = "profile_pictures/${userId}/profile_picture"
    val imageRef = storageRef.child(imageName)

    val uploadTask = imageRef.putFile(profilePicture!!)

    uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
                throw it
            }
        }

        imageRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            Log.d("UpdateProfilePicture", "Download URL: $downloadUri")
            onComplete(downloadUri.toString())
        } else {
            Log.e("UpdateProfilePicture", "Error uploading profile picture: ${task.exception}")
            onComplete(null)
        }
    }
}
