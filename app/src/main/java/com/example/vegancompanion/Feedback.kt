package com.example.vegancompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vegancompanion.models.Feedbacks
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Feedback : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)

        val submitFeedbackButton: Button = view.findViewById(R.id.submit_feedback)
        val feedbackField: TextInputEditText = view.findViewById(R.id.feedback_text)

        submitFeedbackButton.setOnClickListener{
            submitFeedback(feedbackField)
        }

        return view

    }

    private fun submitFeedback(feedbackField: TextInputEditText) {
        val feedbackBody = feedbackField.text.toString()
        val dbFeedback = FirebaseDatabase.getInstance().getReference("Feedback")
        if(feedbackBody.length == 0){
            feedbackField.error = "Please insert feedback"
            feedbackField.requestFocus()
        }
        else if(feedbackBody.length > 1000){
            feedbackField.error = "Feedback cannot exceed 1000 characters"
            feedbackField.requestFocus()
        } else{

            val newFeedback = Feedbacks()
            newFeedback.Body = feedbackBody
            newFeedback.User = FirebaseAuth.getInstance().currentUser!!.email.toString()
            val id = dbFeedback.push().key
            dbFeedback.child(id!!).setValue(newFeedback)
            Toast.makeText(activity,"Feedback submitted", Toast.LENGTH_SHORT).show()
            feedbackField.setText("")
        }
    }
}