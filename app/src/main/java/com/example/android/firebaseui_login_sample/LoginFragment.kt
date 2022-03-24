/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.firebaseui_login_sample

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.example.android.firebaseui_login_sample.databinding.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    // Get a reference to the ViewModel scoped to this Fragment.
    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Firebase 인스턴스 초기화
        auth = FirebaseAuth.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment.
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater, R.layout.fragment_login, container, false
        )

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    Toast.makeText(requireContext(), "로그인 성공.", Toast.LENGTH_SHORT).show()
                }
                LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> {
                    checkVerifyAuthenticationByEmail(auth.currentUser!!)
                }

                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> {
                    Toast.makeText(requireContext(), "로그인 실패.", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }

        })

        binding.signupButton.setOnClickListener {
            val email = binding.inputId.text.toString()
            val password = binding.inputPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) return@setOnClickListener
            createUserWithEmailAndPassword(email, password)
        }

        binding.signinButton.setOnClickListener {
            val email = binding.inputId.text.toString()
            val password = binding.inputPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) return@setOnClickListener
            signInWithEmailAndPassword(email, password)
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }


        return binding.root
    }


    /**
     *
     * */
    private fun checkVerifyAuthenticationByEmail(user: FirebaseUser) {
        val url = "https://solaroid.page.link/Eit5?mode=verifyEmail&uid=" + user.uid
        val actionCodeSetting = ActionCodeSettings.newBuilder()
            .setUrl(url)
            .setAndroidPackageName(
                "com.example.android.firebaseui_login_sample",
                true,
                null
            )
            .setHandleCodeInApp(true)
            .build()


        Log.d(TAG, "${user.email}")

        user.sendEmailVerification(actionCodeSetting)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
            }


    }

    /**
     * 신규 사용자 가입
     * */
    private fun createUserWithEmailAndPassword(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(requireContext(), "기재하신 이메일을 통해 인증 메일을 전송했습니다.", Toast.LENGTH_SHORT)
                        .show()
                    logout()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

            }
    }

    /**
     * 기존 사용자 로그인
     * */
    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "SignInUserWithEmail:success")
                    Toast.makeText(requireContext(), "로그인 성공하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.d(TAG, "SignInUserWithEmail:failure")
                    Toast.makeText(requireContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {

        }
    }

    private fun logout() {
        AuthUI.getInstance()
            .signOut(requireContext())
        Log.d(TAG, "LogOut")
    }

    override fun onDetach() {
        super.onDetach()

    }
}
