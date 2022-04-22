package com.altop.mystoryapp.ui.register

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.altop.mystoryapp.R
import com.altop.mystoryapp.data.Result
import com.altop.mystoryapp.data.remote.response.User
import com.altop.mystoryapp.databinding.FragmentRegisterBinding
import com.altop.mystoryapp.hideKeyboard
import com.altop.mystoryapp.ui.login.LoginViewModel
import com.altop.mystoryapp.ui.login.LoginViewModelFactory

@Suppress("DEPRECATION")
class RegisterFragment : Fragment() {
  private lateinit var progressDialog: ProgressDialog
  
  private lateinit var registerFactory: RegisterViewModelFactory
  private val registerViewModel: RegisterViewModel by viewModels {
    registerFactory
  }
  private lateinit var loginFactory: LoginViewModelFactory
  private val loginViewModel: LoginViewModel by viewModels {
    loginFactory
  }
  
  private var _binding: FragmentRegisterBinding? = null
  private val binding get() = _binding!!
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    progressDialog = ProgressDialog(context).apply {
      setMessage(getString(R.string.loading_message))
      setCancelable(false)
    }
    _binding = FragmentRegisterBinding.inflate(inflater, container, false)
    return binding.root
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loginFactory = LoginViewModelFactory.getInstance(requireActivity())
    registerFactory = RegisterViewModelFactory.getInstance(requireActivity())
    
    val emailEditText = binding.email
    val nameEditText = binding.name
    val passwordEditText = binding.password
    val registerButton = binding.register
    val loginDirection = binding.textButton
    
    if (loginViewModel.isLoggedIn()) {
      redirectToListStory()
    }
    
    registerViewModel.isFormValid.observe(viewLifecycleOwner) {
      registerButton.isEnabled = it
    }
    
    val afterTextChangedListener = object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
      override fun afterTextChanged(s: Editable) {
        registerViewModel.registerFormChanged(
          nameEditText.text.toString(),
          emailEditText.text.toString(),
          passwordEditText.text.toString()
        )
      }
    }
    
    nameEditText.addTextChangedListener(afterTextChangedListener)
    emailEditText.addTextChangedListener(afterTextChangedListener)
    passwordEditText.addTextChangedListener(afterTextChangedListener)
    
    passwordEditText.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_DONE && registerButton.isEnabled) {
        submitRegister(nameEditText, emailEditText, passwordEditText)
      }
      false
    }
    registerButton.setOnClickListener {
      submitRegister(nameEditText, emailEditText, passwordEditText)
    }
    loginDirection.setOnClickListener {
      findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }
  }
  
  private fun submitLogin(email: String, password: String) {
    hideKeyboard()
    loginViewModel.login(email, password).observe(viewLifecycleOwner) { loginResult ->
      when (loginResult) {
        is Result.Loading -> progressDialog.show()
        is Result.Success -> {
          progressDialog.dismiss()
          onSuccessLogin(loginResult.data)
        }
        is Result.Error -> {
          progressDialog.dismiss()
          showToast(loginResult.error)
        }
        else -> {}
      }
    }
  }
  
  private fun submitRegister(
    nameEditText: EditText, emailEditText: EditText, passwordEditText: EditText
  ) {
    val name = nameEditText.text.toString()
    val email = emailEditText.text.toString()
    val password = passwordEditText.text.toString()
    
    hideKeyboard()
    registerViewModel.register(name, email, password)
      .observe(viewLifecycleOwner) { registerResult ->
        when (registerResult) {
          is Result.Loading -> progressDialog.show()
          is Result.Success -> {
            progressDialog.dismiss()
            submitLogin(email, password)
          }
          is Result.Error -> {
            progressDialog.dismiss()
            showToast(registerResult.error)
          }
          else -> {}
        }
      }
  }
  
  private fun onSuccessLogin(user: User) {
    showToast(getString(R.string.welcome, user.name))
    redirectToListStory()
  }
  
  private fun showToast(message: String) {
    val appContext = context?.applicationContext ?: return
    Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
  }
  
  private fun redirectToListStory() {
    findNavController().navigate(R.id.action_registerFragment_to_listStoryFragment)
  }
  
  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
  
}