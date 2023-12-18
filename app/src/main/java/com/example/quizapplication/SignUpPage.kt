package com.example.quizapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.quizapplication.ui.theme.QuizApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

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
    var profilePicture by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        profilePicture = uri
    }
    val lightPurple = Color(0xFFB19CD9)

    AnimatedVisibility(
        visible = isVisible,
        enter = expandIn(),
        exit = shrinkOut()
    ) {
        Box(
            modifier = Modifier
//                .background(color = Color.White)
                .background(color = Color(0xF5F5F5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp)
            ) {
                Spacer(modifier = Modifier.height(150.dp))

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

                if (profilePicture != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profilePicture),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(CircleShape)
                            .clickable { launcher.launch("image/*") }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(CircleShape)
                            .clickable { launcher.launch("image/*") }
                    )
                }

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
                            signUpWithFirebase(email, password, navController, profilePicture)
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
                    .align(BottomCenter)
                    .background(color = Color.LightGray)
                    .height(50.dp),
                containerColor = lightPurple,
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
//                        .background(if (selectedIcon == Icons.Default.Person) Color.LightGray else Color.Transparent)
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
//                        .background(if (selectedIcon == Icons.Default.AccountBox) Color.LightGray else Color.Transparent)
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

fun signUpWithFirebase(email: String, password: String, navController: NavController, profilePicture: Uri?) {
    val firebaseAuth = FirebaseAuth.getInstance()

    firebaseAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(email)
                        .setPhotoUri(profilePicture)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                uploadProfilePicture(user.uid, profilePicture) { profilePictureUrl ->
                                    savingUserData(email, password, profilePictureUrl)
                                    navController.navigate("LoginPage")
                                }
                            } else {
                                Log.e("UpdateProfile", "Error updating profile: ${updateTask.exception}")
                                navController.navigate("SignUpPage")
                            }
                        }
                } else {
                    Log.e("SignUpWithFirebase", "User is null after account creation.")
                }
            } else {
                Log.e("SignUpWithFirebase", "Error creating account: ${task.exception}")
                navController.navigate("SignUpPage")
            }
        }
}


fun uploadProfilePicture(userId: String, profilePicture: Uri?, onComplete: (String?) -> Unit) {
    if (profilePicture == null) {
        onComplete(null)
        return
    }

    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val imageName = "profile_pictures/${userId}/${UUID.randomUUID()}"
    val imageRef = storageRef.child(imageName)

    val uploadTask = imageRef.putFile(profilePicture)

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
            Log.d("UploadProfilePicture", "Download URL: $downloadUri")
            onComplete(downloadUri.toString())
        } else {
            Log.e("UploadProfilePicture", "Error uploading profile picture: ${task.exception}")
            onComplete(null)
        }
    }
}

fun savingUserData(email: String, password: String, profilePictureUrl: String?) {
    val firestore = FirebaseFirestore.getInstance()
    data class InfoForUser(
        val email: String,
        val password: String,
        val profilePictureUrl: String? = null
    )

    val userInfo = InfoForUser(
        email = email,
        password = password,
        profilePictureUrl = profilePictureUrl
    )

    firestore.collection("users").add(userInfo)
}

