package com.altop.mystoryapp.ui.login

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
import com.altop.mystoryapp.databinding.FragmentLoginBinding
import com.altop.mystoryapp.hideKeyboard

@Suppress("DEPRECATION")
class LoginFragment : Fragment() {
  private lateinit var progressDialog: ProgressDialog
  
  private lateinit var factory: LoginViewModelFactory
  private val loginViewModel: LoginViewModel by viewModels {
    factory
  }
  
  private var _binding: FragmentLoginBinding? = null
  private val binding get() = _binding!!
  
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    
    progressDialog = ProgressDialog(context).apply {
      setMessage(getString(R.string.loading_message))
      setCancelable(false)
    }
    
    _binding = FragmentLoginBinding.inflate(inflater, container, false)
    return binding.root
    
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    factory = LoginViewModelFactory.getInstance(requireActivity())
    
    val emailEditText = binding.email
    val passwordEditText = binding.password
    val loginButton = binding.login
    val registerDirection = binding.textButton
    
    if (loginViewModel.isLoggedIn()) {
      redirectToListStory()
    }
    
    loginViewModel.isFormValid.observe(viewLifecycleOwner) {
      loginButton.isEnabled = it
    }
    
    registerDirection.setOnClickListener {
      findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }
    
    val afterTextChangedListener = object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
      override fun afterTextChanged(s: Editable) {
        loginViewModel.loginDataChanged(
          emailEditText.text.toString(), passwordEditText.text.toString()
        )
      }
    }
    emailEditText.addTextChangedListener(afterTextChangedListener)
    passwordEditText.addTextChangedListener(afterTextChangedListener)
    passwordEditText.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_DONE && loginButton.isEnabled) {
        submitLogin(emailEditText, passwordEditText)
      }
      false
    }
    
    loginButton.setOnClickListener {
      submitLogin(emailEditText, passwordEditText)
    }
  }

  
  private fun submitLogin(emailEditText: EditText, passwordEditText: EditText) {
    hideKeyboard()
    val email = emailEditText.text.toString()
    val password = passwordEditText.text.toString()
    
    loginViewModel.login(email, password).observe(viewLifecycleOwner) { loginResult ->
      when (loginResult) {
        is Result.Loading -> progressDialog.show()
        is Result.Success -> {
          progressDialog.dismiss()
          onSuccessLogin(loginResult.data)
        }
        is Result.Error -> {
          progressDialog.dismiss()
          showLoginFailed(loginResult.error)
        }
        else -> {}
      }
    }
    
  }
  
  private fun showLoginFailed(errorString: String) {
    val appContext = context?.applicationContext ?: return
    Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
  }
  
  private fun onSuccessLogin(user: User) {
    Toast.makeText(requireContext(), getString(R.string.welcome, user.name), Toast.LENGTH_LONG)
      .show()
    redirectToListStory()
  }
  
  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
  
  private fun redirectToListStory() {
    findNavController().navigate(R.id.action_loginFragment_to_listStoryFragment)
  }
  
}
