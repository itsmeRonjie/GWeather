package com.ronjie.gweather.presentation.screen.auth

import com.ronjie.gweather.domain.model.AuthResult
import com.ronjie.gweather.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockAuthRepository = mockk<AuthRepository>(relaxed = true)
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(mockAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Initial`() {
        assertTrue(viewModel.authState.value is AuthState.Initial)
    }

    @Test
    fun `signIn with valid credentials updates state to Success`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        coEvery { mockAuthRepository.signIn(email, password) } returns AuthResult(isSuccess = true)

        viewModel.onEvent(AuthEvent.SignIn(email, password))

        assertTrue(viewModel.authState.value is AuthState.Success)
    }

    @Test
    fun `signIn with invalid credentials updates state to Error`() = runTest {
        val email = "test@example.com"
        val password = "wrong"
        val errorMessage = "Invalid credentials"
        coEvery {
            mockAuthRepository.signIn(
                email,
                password
            )
        } returns AuthResult(errorMessage = errorMessage)

        viewModel.onEvent(AuthEvent.SignIn(email, password))

        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals(errorMessage, (state as AuthState.Error).message)
    }

    @Test
    fun `signUp with valid credentials updates state to Success`() = runTest {
        val email = "newuser@example.com"
        val password = "password123"
        coEvery { mockAuthRepository.signUp(email, password) } returns AuthResult(isSuccess = true)

        viewModel.onEvent(AuthEvent.SignUp(email, password))

        assertTrue(viewModel.authState.value is AuthState.Success)
    }

    @Test
    fun `signUp with existing email updates state to Error`() = runTest {
        val email = "existing@example.com"
        val password = "password123"
        val errorMessage = "Email already in use"
        coEvery {
            mockAuthRepository.signUp(
                email,
                password
            )
        } returns AuthResult(errorMessage = errorMessage)

        viewModel.onEvent(AuthEvent.SignUp(email, password))

        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals(errorMessage, (state as AuthState.Error).message)
    }

    @Test
    fun `signOut updates state to SignedOut`() = runTest {
        viewModel.onEvent(AuthEvent.SignOut)

        assertTrue(viewModel.authState.value is AuthState.SignedOut)
        verify { mockAuthRepository.signOut() }
    }

    @Test
    fun `resetState updates state to Initial`() = runTest {
        viewModel.onEvent(AuthEvent.SignOut)
        assertTrue(viewModel.authState.value is AuthState.SignedOut)

        viewModel.onEvent(AuthEvent.ResetState)

        assertTrue(viewModel.authState.value is AuthState.Initial)
    }

    @Test
    fun `loading state is set during authentication`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        coEvery { mockAuthRepository.signIn(email, password) } coAnswers {
            assertTrue(viewModel.authState.value is AuthState.Loading)
            AuthResult(isSuccess = true)
        }

        viewModel.onEvent(AuthEvent.SignIn(email, password))

        assertTrue(viewModel.authState.value is AuthState.Success)
    }
}
