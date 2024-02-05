package com.example.todoapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentAddBinding
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import kotlin.time.Duration.Companion.minutes


class AddFragment : DialogFragment() {

    private lateinit var binding:FragmentAddBinding
    private lateinit var listner: DialogNextBtnClickListner
    private var toDoData:ToDoData?=null
    fun setListner(listner:DialogNextBtnClickListner){
        this.listner=listner
    }
    companion object{

        const val TAG="AddFragment"
        @JvmStatic
        fun newInstance(taskId: String,Task:String)=AddFragment().apply {
            arguments=Bundle().apply {
                putString("taskId",taskId)
                putString("Task",Task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments!=null){
            toDoData=ToDoData(arguments?.getString("taskId").toString(),arguments?.getString("Task").toString())
            binding.todoEt.setText(toDoData?.Task)
        }
        registerEvent()
    }

    private fun registerEvent() {
        binding.todoNextBtn.setOnClickListener {
            val task=binding.todoEt.text.toString()

            if(task.isNotEmpty()){

                    if (toDoData==null){
                        listner.onSaveTask(task,binding.todoEt)

                    }
                    else{
                        toDoData!!.Task=task
                        listner.onUpdateTask(toDoData!!,binding.todoEt)
                    }
                }



            else{
                Toast.makeText(context,"Please Type Some Tasks",Toast.LENGTH_SHORT).show()
            }
        }
        binding.todoClose.setOnClickListener {
            dismiss()
        }
    }
    interface DialogNextBtnClickListner{
        fun onSaveTask(todo:String,todoEdit:TextInputEditText)
        fun onUpdateTask(toDoData: ToDoData,todoEdit:TextInputEditText)

    }


}