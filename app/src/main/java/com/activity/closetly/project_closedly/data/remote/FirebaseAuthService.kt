package com.activity.closetly.project_closedly.data.remote

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    data object Loading : AuthResult<Nothing>()
}

@Singleton
class FirebaseAuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val isUserLoggedIn: Boolean
        get() = currentUser != null

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                AuthResult.Success(user)
            } ?: AuthResult.Error("Error al iniciar sesión")
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error desconocido")
        }
    }

    suspend fun registerWithEmail(
        email: String,
        password: String,
        username: String
    ): AuthResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "username" to username,
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .await()
                AuthResult.Success(user)
            } ?: AuthResult.Error("Error al registrar usuario")
        } catch (e: Exception) {
            firebaseAuth.currentUser?.delete()
            AuthResult.Error(e.localizedMessage ?: "Error desconocido")
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error al enviar correo")
        }
    }

    suspend fun updateUserEmail(
        currentPassword: String,
        newEmail: String
    ): AuthResult<Unit> {
        return try {
            val user = currentUser ?: return AuthResult.Error("No hay usuario autenticado")
            val currentEmail = user.email ?: return AuthResult.Error("Email actual no disponible")

            val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)
            user.reauthenticate(credential).await()

            // Actualización inmediata en Firebase Auth
            user.updateEmail(newEmail).await()

            // Actualización inmediata en Firestore
            firestore.collection("users")
                .document(user.uid)
                .update("email", newEmail)
                .await()

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("password is invalid", ignoreCase = true) == true ->
                    "La contraseña actual es incorrecta"
                e.message?.contains("email-already-in-use", ignoreCase = true) == true ->
                    "Este email ya está en uso por otra cuenta"
                e.message?.contains("invalid-email", ignoreCase = true) == true ->
                    "El formato del email es inválido"
                e.message?.contains("network", ignoreCase = true) == true ->
                    "Error de conexión. Verifica tu internet"
                else -> e.localizedMessage ?: "Error al actualizar email"
            }
            AuthResult.Error(errorMessage)
        }
    }

    suspend fun updateUserPassword(
        currentPassword: String,
        newPassword: String
    ): AuthResult<Unit> {
        return try {
            val user = currentUser ?: return AuthResult.Error("No hay usuario autenticado")
            val email = user.email ?: return AuthResult.Error("Email no disponible")

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            user.updatePassword(newPassword).await()

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("password is invalid", ignoreCase = true) == true ->
                    "La contraseña actual es incorrecta"
                e.message?.contains("weak-password", ignoreCase = true) == true ->
                    "La nueva contraseña es demasiado débil"
                e.message?.contains("network", ignoreCase = true) == true ->
                    "Error de conexión. Verifica tu internet"
                else -> e.localizedMessage ?: "Error al actualizar contraseña"
            }
            AuthResult.Error(errorMessage)
        }
    }

    suspend fun getUserData(userId: String): AuthResult<Map<String, Any>> {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val data = document.data ?: emptyMap()
                AuthResult.Success(data)
            } else {
                AuthResult.Error("Usuario no encontrado")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error al obtener datos")
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
