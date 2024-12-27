package com.example.mydev

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mydev.adapter.ContactAdapter
import com.example.mydev.api.RetrofitInstance
import com.example.mydev.databinding.FragmentContactBinding
import com.example.mydev.model.User
import com.example.mydev.model.UserCreate
import com.example.mydev.model.UserUpdate
import kotlinx.coroutines.launch

class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        setupListView()
        fetchUsers()
        return binding.root
    }

    private fun setupListView() {
        adapter = ContactAdapter(requireContext())
        binding.listView.adapter = adapter

        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val user = adapter.getItem(position)
            showUserDetail(user)
        }
    }

    private fun fetchUsers() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getUsers()
                adapter.updateUsers(response.users)
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ContactFragment", "Error fetching users", e)
            }
        }
    }

    private fun showUserDetail(user: User) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("User Detail")
            .setMessage("""
            Name: ${user.userName}
            Email: ${user.email}
            Birthday: ${user.birthday}
            Phone: ${user.phoneNumber}
            Instagram: ${user.instagramId}
            Created: ${user.createdAt}
        """.trimIndent())
            .setPositiveButton("Edit") { dialog, _ ->
                showEditDialog(user)
            }
            .setNegativeButton("Close", null)
            .create()
        dialog.show()
    }

    private fun showEditDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_contact, null)

        // 현재 데이터 채우기
        dialogView.findViewById<EditText>(R.id.edtName).setText(user.userName)
        dialogView.findViewById<EditText>(R.id.edtEmail).setText(user.email)
        dialogView.findViewById<EditText>(R.id.edtBirthday).setText(user.birthday)
        dialogView.findViewById<EditText>(R.id.edtPhone).setText(user.phoneNumber)
        dialogView.findViewById<EditText>(R.id.edtInstagram).setText(user.instagramId)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Contact")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val updatedUser = UserUpdate(
                    userName = dialogView.findViewById<EditText>(R.id.edtName).text.toString(),
                    email = dialogView.findViewById<EditText>(R.id.edtEmail).text.toString(),
                    birthday = dialogView.findViewById<EditText>(R.id.edtBirthday).text.toString(),
                    phoneNumber = dialogView.findViewById<EditText>(R.id.edtPhone).text.toString(),
                    instagramId = dialogView.findViewById<EditText>(R.id.edtInstagram).text.toString()
                )
                updateUser(user.id, updatedUser)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUser(userId: Int, userUpdate: UserUpdate) {
        lifecycleScope.launch {
            try {
                val updatedUser = RetrofitInstance.api.updateUser(userId, userUpdate)
                Toast.makeText(context, "Contact updated successfully", Toast.LENGTH_SHORT).show()
                fetchUsers() // 목록 새로고침
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ContactFragment", "Error updating user", e)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListView()
        fetchUsers()

        binding.fabAddContact.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = dialogView.findViewById<EditText>(R.id.edtName).text.toString()
                val email = dialogView.findViewById<EditText>(R.id.edtEmail).text.toString()
                val birthday = dialogView.findViewById<EditText>(R.id.edtBirthday).text.toString()
                val phone = dialogView.findViewById<EditText>(R.id.edtPhone).text.toString()
                val instagram = dialogView.findViewById<EditText>(R.id.edtInstagram).text.toString()

                if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()) {
                    createUser(UserCreate(name, email, birthday, phone, instagram))
                } else {
                    Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createUser(userCreate: UserCreate) {
        lifecycleScope.launch {
            try {
                val newUser = RetrofitInstance.api.createUser(userCreate)
                Toast.makeText(context, "Contact added successfully", Toast.LENGTH_SHORT).show()
                fetchUsers() // 목록 새로고침
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ContactFragment", "Error creating user", e)
            }
        }
    }

}