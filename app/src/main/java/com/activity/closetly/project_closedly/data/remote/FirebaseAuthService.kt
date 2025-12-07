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
    init {
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "FirebaseAuthService inicializado")
        Log.d(TAG, "App Firebase: ${firebaseAuth.app.name}")
        Log.d(TAG, "Auth inicializado: ${firebaseAuth.currentUser != null}")
        Log.d(TAG, "Firestore inicializado: ${firestore.app.name}")

        currentUser?.let { user ->
            Log.d(TAG, "Usuario actual: ${user.email}")
            Log.d(TAG, "UID: ${user.uid}")
        } ?: Log.d(TAG, "No hay usuario autenticado")

        Log.d(TAG, "════════════════════════════════════════")
    }

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val isUserLoggedIn: Boolean
        get() = currentUser != null

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        Log.d(TAG, "Iniciando observación de estado de auth")

        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            Log.d(TAG, "Estado de auth cambió: ${user?.email ?: "Sin usuario"}")
            trySend(user)
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            Log.d(TAG, "Deteniendo observación de auth")
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            Log.d(TAG, "Intentando login con: $email")

            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            result.user?.let { user ->
                Log.d(TAG, "Login exitoso")
                Log.d(TAG, "Usuario: ${user.email}")
                Log.d(TAG, "UID: ${user.uid}")
                AuthResult.Success(user)
            } ?: run {
                Log.e(TAG, "Error: Usuario nulo después de login")
                AuthResult.Error("Error al iniciar sesión")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en login: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Error desconocido")
        }
    }

    suspend fun registerWithEmail(
        email: String,
        password: String,
        username: String
    ): AuthResult<FirebaseUser> {
        return try {
            Log.d(TAG, "Intentando registro con: $email")

            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            result.user?.let { user ->
                Log.d(TAG, "Usuario creado en Auth")
                Log.d(TAG, "Email: ${user.email}")
                Log.d(TAG, "UID: ${user.uid}")

                val userData = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "username" to username,
                    "createdAt" to System.currentTimeMillis()
                )

                Log.d(TAG, "Guardando datos en Firestore...")

                firestore.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .await()

                Log.d(TAG, "Datos guardados en Firestore")
                Log.d(TAG, "Ruta: users/${user.uid}")

                AuthResult.Success(user)
            } ?: run {
                Log.e(TAG, "Error: Usuario nulo después de registro")
                AuthResult.Error("Error al registrar usuario")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en registro: ${e.message}", e)
            firebaseAuth.currentUser?.delete()
            AuthResult.Error(e.localizedMessage ?: "Error desconocido")
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            Log.d(TAG, "Enviando correo de recuperación a: $email")

            firebaseAuth.sendPasswordResetEmail(email).await()

            Log.d(TAG, "Correo de recuperación enviado")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al enviar correo: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Error al enviar correo")
        }
    }

    suspend fun updateUserEmail(
        currentPassword: String,
        newEmail: String
    ): AuthResult<Unit> {
        return try {
            val user = currentUser ?: return AuthResult.Error("No hay usuario autenticado")
            val email = user.email ?: return AuthResult.Error("Email actual no disponible")

            Log.d(TAG, "Actualizando email de $email a $newEmail")

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            Log.d(TAG, "Re-autenticación exitosa")

            user.updateEmail(newEmail).await()
            Log.d(TAG, "Email actualizado en Authentication")

            firestore.collection("users")
                .document(user.uid)
                .update("email", newEmail)
                .await()
            Log.d(TAG, "Email actualizado en Firestore")

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar email: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Error al actualizar email")
        }
    }

    suspend fun updateUserPassword(
        currentPassword: String,
        newPassword: String
    ): AuthResult<Unit> {
        return try {
            val user = currentUser ?: return AuthResult.Error("No hay usuario autenticado")
            val email = user.email ?: return AuthResult.Error("Email no disponible")

            Log.d(TAG, "Actualizando contraseña para: $email")

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            Log.d(TAG, "Re-autenticación exitosa")

            user.updatePassword(newPassword).await()
            Log.d(TAG, "Contraseña actualizada exitosamente")

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar contraseña: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Error al actualizar contraseña")
        }
    }

    suspend fun getUserData(userId: String): AuthResult<Map<String, Any>> {
        return try {
            Log.d(TAG, "Obteniendo datos de usuario: $userId")

            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val data = document.data ?: emptyMap()
                Log.d(TAG, "Datos obtenidos: $data")
                AuthResult.Success(data)
            } else {
                Log.e(TAG, "Documento no existe")
                AuthResult.Error("Usuario no encontrado")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener datos: ${e.message}", e)
            AuthResult.Error(e.localizedMessage ?: "Error al obtener datos")
        }
    }

    fun signOut() {
        val userEmail = currentUser?.email
        Log.d(TAG, "Cerrando sesión de: $userEmail")

        firebaseAuth.signOut()

        Log.d(TAG, "Sesión cerrada")
        Log.d(TAG, "Usuario actual: ${currentUser?.email ?: "Ninguno"}")
    }
}