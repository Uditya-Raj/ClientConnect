package com.udit.ClientConnect

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.udit.ClientConnect.databinding.FragmentAssignWorkBinding


import com.udit.ClientConnect.utils.Utils
import com.udit.ClientConnect.utils.Work
import java.text.SimpleDateFormat
import java.util.Locale


class AssignWorkFragment : Fragment() {
    val employeeData by navArgs<AssignWorkFragmentArgs>()
   private lateinit var binding: FragmentAssignWorkBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssignWorkBinding.inflate(layoutInflater)

        binding.assignWorkFrag.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        setDate()
        binding.btnDone.setOnClickListener {
            assignWork()
        }
        return binding.root
    }

    private fun assignWork() {
        Utils.showDialog(requireContext())
        val workTitle = binding.etTitle.text.toString()
        val WorkDescription = binding.workDesc.text.toString()
        val lastdate = binding.tvDate.text.toString()
        if(workTitle.isNotEmpty() && WorkDescription.isNotEmpty() && lastdate.isNotEmpty()){
            val empId = employeeData.EmployeeDetails.userId
            val bossId = FirebaseAuth.getInstance().currentUser?.uid
            val workId = bossId+empId
            val randomId =(1..20).map{(('A'..'Z')+('a'..'z')+('0'..'9')).random()}.joinToString("")
            val work = Work(workId=randomId,workTitle=workTitle, workDescription = WorkDescription, workLastDate = lastdate, workStatus = "1")
            FirebaseDatabase.getInstance().getReference("Works").child(workId).child(randomId).setValue(work).addOnSuccessListener {
                Utils.hideDialog()
                Toast.makeText(requireContext(),"Work has been Assigned to ${employeeData.EmployeeDetails.userName}",Toast.LENGTH_SHORT).show()
                val action = AssignWorkFragmentDirections.actionAssignWorkFragmentToWorksFragment(employeeData.EmployeeDetails)
                findNavController().navigate(action)
            }
        }
    }

    private fun setDate(){
        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view,year,month,dayOfMonth ->
            myCalender.apply {
                set(Calendar.YEAR , year)
                set(Calendar.MONTH , month)
                set(Calendar.DAY_OF_MONTH , dayOfMonth)
                updateLabel(myCalender)
            }

        }
        binding.DatePicker.setOnClickListener {
            DatePickerDialog(requireContext(),datePicker,myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH),myCalender.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateLabel(myCalender: Calendar) {
        val Myformat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(Myformat, Locale.UK)
        binding.tvDate.text = sdf.format(myCalender.time)
    }


}