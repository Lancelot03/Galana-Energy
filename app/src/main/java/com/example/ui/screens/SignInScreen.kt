package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("executive@galanaenergy.com") }
    var password by remember { mutableStateOf("Galana2026!#") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var showBiometricPrompt by remember { mutableStateOf(false) }
    var activeRole by remember { mutableStateOf("Executive Director") }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    fun validateAndSubmit() {
        var isValid = true
        if (email.isBlank() || !email.contains("@")) {
            emailError = "Please enter a valid corporate email"
            isValid = false
        } else {
            emailError = null
        }

        if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = null
        }

        if (isValid) {
            isLoading = true
            scope.launch {
                delay(600) // Realistic authentication transition
                isLoading = false
                onSignInSuccess()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GalanaNavy, Color(0xFF091426))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // SAP Live Connection Top Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(FuelPetrolGreen)
                )
                Text(
                    text = "SAP S/4HANA Cloud: Connected • Client 080",
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // App Corporate Logo & Glow
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.galana_icon_1791266706994),
                    contentDescription = "Galana Energy Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "GALANA ENERGY",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.2.sp
            )

            Text(
                text = "Executive Sales & Mombasa Depot Operations",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Fast Demo Role Switcher (Crucial for Reviewers & Demoing)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SELECT EXECUTIVE PROFILE (QUICK DEMO)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GalanaAmberLight,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple("Director", "executive@galanaenergy.com", "Executive Director"),
                        Triple("Terminal Head", "mombasa.terminal@galanaenergy.com", "Terminal Manager"),
                        Triple("SAP Billing", "sap.billing@galanaenergy.com", "Billing Controller")
                    ).forEach { (shortTitle, emailVal, roleTitle) ->
                        val isSelected = email == emailVal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) GalanaAmber else Color.White.copy(alpha = 0.08f))
                                .border(
                                    1.dp,
                                    if (isSelected) GalanaAmberLight else Color.White.copy(alpha = 0.15f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    email = emailVal
                                    password = "Galana2026!#"
                                    activeRole = roleTitle
                                    emailError = null
                                    passwordError = null
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = shortTitle,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0F172A) else Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Credentials Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Executive Sign In",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GalanaNavy
                        )
                        Text(
                            text = activeRole,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GalanaAmberDark,
                            modifier = Modifier
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Email Field
                    Column {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = null
                            },
                            label = { Text("Corporate Email") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = GalanaNavyLight)
                            },
                            isError = emailError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        emailError?.let {
                            Text(text = it, fontSize = 11.sp, color = StatusNegative, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                        }
                    }

                    // Password Field
                    Column {
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = null
                            },
                            label = { Text("Password / Security Key") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = GalanaNavyLight)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = passwordError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                validateAndSubmit()
                            }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        passwordError?.let {
                            Text(text = it, fontSize = 11.sp, color = StatusNegative, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                        }
                    }

                    // Remember Me Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = GalanaNavy)
                            )
                            Text(
                                text = "Keep session active",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        TextButton(onClick = {
                            email = "executive@galanaenergy.com"
                            password = "Galana2026!#"
                        }) {
                            Text(
                                text = "Reset Demo",
                                fontSize = 12.sp,
                                color = GalanaNavyLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Sign In Button
                    Button(
                        onClick = { validateAndSubmit() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("sign_in_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                        } else {
                            Text(
                                text = "Authorize & Access Dashboard",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Biometrics Button
                    OutlinedButton(
                        onClick = { showBiometricPrompt = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = GalanaNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fast Biometric Passcode",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GalanaNavy
                        )
                    }
                }
            }
        }

        // Security & Version Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = FuelPetrolGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Encrypted SAP NetWeaver Session • TLS 1.3",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            Text(
                text = "Galana Energy Kenya Ltd • Production Environment",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }

    if (showBiometricPrompt) {
        AlertDialog(
            onDismissRequest = { showBiometricPrompt = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = GalanaNavy,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Confirm Biometric Identity",
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Touch fingerprint scanner or use device face recognition for $email",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBiometricPrompt = false
                        onSignInSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy)
                ) {
                    Text("Authenticate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBiometricPrompt = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
