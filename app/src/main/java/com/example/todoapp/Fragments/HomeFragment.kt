package com.example.todoapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.fragment.app.FragmentHostCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.utils.ToDoAdapter
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment(),
    AddFragment.DialogNextBtnClickListner, ToDoAdapter.ToDoAdapterClicksInterface {
    private val TAG = "HomeFragment"
    private lateinit var auth:FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding:FragmentHomeBinding
    private  var addFragment: AddFragment?=null
    private lateinit var adapter: ToDoAdapter
    private lateinit var mlist:MutableList<ToDoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun getDataFromFirebase() {
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               mlist.clear()
                for(tasksnap in snapshot.children){
                    val todoTask=tasksnap.key?.let{
                        ToDoData(it,tasksnap.value.toString())
                    }
                    if(todoTask!=null){
                        mlist.add(todoTask)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun registerEvents() {
        binding.addbtn.setOnClickListener {
            if (addFragment!=null)
            {
                childFragmentManager.beginTransaction().remove(addFragment!!).commit()
            }

            addFragment= AddFragment()
            addFragment!!.setListner(this)
            addFragment!!.show(
                childFragmentManager,AddFragment.TAG
            )

        }
    }

    private fun init(view:View){
        navController= Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        databaseReference=FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())
        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager=LinearLayoutManager(context)
        mlist= mutableListOf()
        adapter=ToDoAdapter(mlist)
        adapter.setListner(this)
        binding.recycler.adapter=adapter

    }

    override fun onSaveTask(todo: String, todoEdit: TextInputEditText) {
        databaseReference.push().setValue(todo).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"Todo Saved Successfully",Toast.LENGTH_SHORT).show()
                todoEdit.text=null
            }
            else{
                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
            }

            addFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEdit: TextInputEditText) {
        val map=HashMap<String,Any>()
        map[toDoData.taskId]=toDoData.Task
        databaseReference.updateChildren(map).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"Updated Successfully",Toast.LENGTH_SHORT).show()

                todoEdit.text=null
            }else{
                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
            }

            addFragment!!.dismiss()
        }
    }

    override fun onDeleteTaskBtnClicked(toDoData: ToDoData) {
        databaseReference.child((toDoData.taskId)).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {
        if(addFragment!=null)
            childFragmentManager.beginTransaction().remove(addFragment!!).commit()
        addFragment=AddFragment.newInstance(toDoData.taskId,toDoData.Task)
        addFragment!!.setListner(this)
        addFragment!!.show(childFragmentManager,AddFragment.TAG)

    }


}