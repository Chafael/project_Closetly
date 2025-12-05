package com.activity.closetly.project_closedly.data.repository

import com.activity.closetly.project_closedly.data.local.dao.UserDao
import com.activity.closetly.project_closedly.data.local.entity.UserEntity
import com.activity.closetly.project_closedly.data.remote.AuthResult
import com.activity.closetly.project_closedly.data.remote.FirebaseAuthService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val userDao: UserDao
) {
    val currentUser: FirebaseUser?
        get() = firebaseAuthService.currentUser

    val isUserLoggedIn: Boolean
        get() = firebaseAuthService.isUserLoggedIn

    fun observeAuthState(): Flow<FirebaseUser?> = firebaseAuthService.observeAuthState()

    suspend fun login(email: String, password: String): AuthResult<FirebaseUser> {
        val result = firebaseAuthService.signInWithEmail(email, password)

        if (result is AuthResult.Success) {
            val user = result.data
            val localUser = userDao.getUserById(user.uid)
            if (localUser != null) {
                userDao.updateLastLogin(user.uid)
            } else {
                userDao.insertUser(
                    UserEntity(
                        id = user.uid,
                        email = user.email ?: "",
                        username = user.displayName ?: ""
                    )
                )
            }
        }
        return result
    }

    suspend fun register(
        email: String,
        password: String,
        username: String
    ): AuthResult<FirebaseUser> {
        val result = firebaseAuthService.registerWithEmail(email, password, username)

        if (result is AuthResult.Success) {
            val user = result.data
            userDao.insertUser(
                UserEntity(
                    id = user.uid,
                    email = email,
                    username = username
                )
            )
        }
        return result
    }

    suspend fun resetPassword(email: String): AuthResult<Unit> {
        return firebaseAuthService.sendPasswordResetEmail(email)
    }

    suspend fun logout() {
        firebaseAuthService.signOut()
        userDao.deleteAllUsers() // Limpia datos locales al cerrar sesión
    }

    fun getLocalUser(userId: String): Flow<UserEntity?> {
        return userDao.observeUser(userId)
    }

    suspend fun getLocalUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }
}