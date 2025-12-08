package com.activity.closetly.project_closedly.data.remote

import android.util.Log
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

private const val TAG = "FirebaseAuthService"

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
            Log.d(TAG, "Iniciando actualizacion de email")

            val user = currentUser ?: return AuthResult.Error("No hay usuario autenticado")
            val email = user.email ?: return AuthResult.Error("Email actual no disponible")

            Log.d(TAG, "Email actual: $email")
            Log.d(TAG, "Nuevo email: $newEmail")
            Log.d(TAG, "Usuario UID: ${user.uid}")
            Log.d(TAG, "Reautenticando usuario")

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            Log.d(TAG, "Reautenticacion exitosa")
            Log.d(TAG, "Actualizando email en Authentication")

            user.updateEmail(newEmail).await()

            Log.d(TAG, "Email actualizado en Authentication")
            Log.d(TAG, "Actualizando email en Firestore")

            firestore.collection("users")
                .document(user.uid)
                .update("email", newEmail)
                .await()

            Log.d(TAG, "Email actualizado en Firestore")
            Log.d(TAG, "Actualizacion completada exitosamente")

            firestore.collection("users")
                .document(user.uid)
                .update("email", newEmail)
                .await()

            Log.d(TAG, "Email de verificacion enviado")
            Log.d(TAG, "El usuario debe verificar el nuevo email antes de que se actualice")

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar email")
            Log.e(TAG, "Tipo de excepcion: ${e.javaClass.simpleName}")
            Log.e(TAG, "Mensaje: ${e.message}")
            Log.e(TAG, "Mensaje localizado: ${e.localizedMessage}")
            Log.e(TAG, "Stack trace:", e)

            val errorMessage = when {
                e.message?.contains("password is invalid", ignoreCase = true) == true ->
                    "La contraseña actual es incorrecta"
                e.message?.contains("email-already-in-use", ignoreCase = true) == true ->
                    "Este email ya está en uso"
                e.message?.contains("invalid-email", ignoreCase = true) == true ->
                    "El formato del email es inválido"
                e.message?.contains("network", ignoreCase = true) == true ->
                    "Error de conexión. Verifica tu internet"
                e.message?.contains("OPERATION_NOT_ALLOWED", ignoreCase = true) == true ||
                        e.message?.contains("sign-in provider is disabled", ignoreCase = true) == true ->
                    "ERROR DE FIREBASE: El proveedor de email/contraseña está deshabilitado"
                else -> "Error: ${e.message ?: e.localizedMessage ?: "Error desconocido"}"
            }
            AuthResult.Error(errorMessage)
        }
    }

    suspend fun updateUserPassword(
        currentPassword: String,
        newPassword: String
    ): AuthResult<Unit> {
        return try {
            Log.d(TAG, "Iniciando actualizacion de contraseña")

            val user = currentUser ?: return AuthResult.Error("No hay usuario autenticado")
            val email = user.email ?: return AuthResult.Error("Email no disponible")

            Log.d(TAG, "Usuario: $email")
            Log.d(TAG, "Reautenticando usuario")

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            Log.d(TAG, "Reautenticacion exitosa")
            Log.d(TAG, "Actualizando contraseña")

            user.updatePassword(newPassword).await()

            Log.d(TAG, "Contraseña actualizada exitosamente")

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar contraseña")
            Log.e(TAG, "Tipo de excepcion: ${e.javaClass.simpleName}")
            Log.e(TAG, "Mensaje: ${e.message}")
            Log.e(TAG, "Stack trace:", e)

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