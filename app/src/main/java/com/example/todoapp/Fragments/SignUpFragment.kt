package com.example.todoapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class  SignUpFragment : Fragment() {


    private lateinit var auth: FirebaseAuth
    private lateinit var navControl:NavController
    private lateinit var binding:FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvent()
    }
    private fun init(view: View){
        navControl=Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
    }
    private fun registerEvent()
    {
        binding.text3.setOnClickListener {
            navControl.navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.nextbtn.setOnClickListener{


            val email=binding.mailEt.text.toString().trim()
            val pass=binding.passEt.text.toString().trim()
            val retypePass=binding.repassEt.text.toString().trim()

            if(email.isNotEmpty()&& pass.isNotEmpty()&&retypePass.isNotEmpty()){
                if(pass==retypePass){
                    binding.progressBar2.visibility=View.VISIBLE
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(
                        OnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(context,"Registerd Successfully",Toast.LENGTH_SHORT).show()
                            navControl.navigate(R.id.action_signUpFragment_to_homeFragment)

                        }else{
                            Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                            binding.progressBar2.visibility=View.GONE
                    })
                }
                else{
                    Toast.makeText(context,"Password Doesn't Match",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context,"Please Enter All Fields",Toast.LENGTH_SHORT).show()
            }
        }

    }


}