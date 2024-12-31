package com.example.mydev

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mydev.adapter.ContactAdapter
import com.example.mydev.api.RetrofitInstance
import com.example.mydev.databinding.FragmentContactBinding
import com.example.mydev.model.User
import com.example.mydev.model.UserCreate
import com.example.mydev.model.UserUpdate
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private lateinit var adapter: ContactAdapter
    private var allUsers: List<User> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSearchView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fullTitle = "My Contacts"
        val spannableString = SpannableString(fullTitle)
        spannableString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            0,
            2,  // "My" is 2 characters
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.purple2)), // #BB86FC
            3,
            fullTitle.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.titleTextView.text = spannableString




        fetchUsers()

        binding.fabAddContact.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = ContactAdapter(
            context = requireContext(),
            onItemClick = { user -> showUserDetail(user) },
            onDeleteClick = { user -> deleteUser(user.id) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ContactFragment.adapter
        }

        // Swipe to delete 설정
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                showDeleteConfirmationDialog(position)
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recyclerView)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchUsers(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchUsers(newText)
                return true
            }
        })
    }

    private fun searchUsers(query: String?) {
        lifecycleScope.launch {
            try {
                if (query.isNullOrBlank()) {
                    adapter.updateUsers(allUsers)
                } else {
                    val response = RetrofitInstance.api.searchUsers(query)
                    val searchResults = response.users.map { user ->
                        user.copy(profileImageRes = R.drawable.ic_add)
                    }
                    adapter.updateUsers(searchResults)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Search error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ContactFragment", "Error searching users", e)
            }
        }
    }

    private fun fetchUsers() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getUsers()
                allUsers = response.users.map { user ->
                    user.copy(profileImageRes = R.drawable.ic_add)
                }
                adapter.updateUsers(allUsers)
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ContactFragment", "Error fetching users", e)
            }
        }
    }

    private fun showUserDetail(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_detail, null)

        // 텍스트뷰 설정
        dialogView.findViewById<TextView>(R.id.tvName).text = "Name: ${user.userName}"
        dialogView.findViewById<TextView>(R.id.tvEmail).text = "Email: ${user.email}"
        dialogView.findViewById<TextView>(R.id.tvBirthday).text = "Birthday: ${user.birthday}"
        dialogView.findViewById<TextView>(R.id.tvPhone).text = "Phone: ${user.phoneNumber}"
        dialogView.findViewById<TextView>(R.id.tvInstagram).text = "Instagram: ${user.instagramId}"
        dialogView.findViewById<TextView>(R.id.tvCreated).text = "Created: ${user.createdAt}"

        val dialog = AlertDialog.Builder(requireContext(), R.style.DarkDialog)
            .setView(dialogView)
            .setPositiveButton("Edit") { _, _ ->
                showEditDialog(user)
            }
            .setNegativeButton("Close", null)
            .create()

        dialog.setOnShowListener {
            // Add 버튼 색상 설정
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(requireContext().getColor(R.color.purple2))

            // Cancel 버튼 색상 설정
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(requireContext().getColor(R.color.purple2))
        }

        // 다이얼로그 창 배경 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.black)

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete this contact?")
            .setPositiveButton("Delete") { _, _ ->
                val user = adapter.getUser(position)
                deleteUser(user.id)
            }
            .setNegativeButton("Cancel") { _, _ ->
                adapter.notifyItemChanged(position)
            }
            .show()
    }

    private fun deleteUser(userId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.deleteUser(userId)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Contact deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchUsers()
                } else {
                    Toast.makeText(context, "Failed to delete contact", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ContactFragment", "Error deleting user", e)
            }
        }
    }

    private fun showEditDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_contact, null)
        val editTitleView = layoutInflater.inflate(R.layout.edit_title, null)

        dialogView.findViewById<EditText>(R.id.edtName).setText(user.userName)
        dialogView.findViewById<EditText>(R.id.edtEmail).setText(user.email)
        dialogView.findViewById<EditText>(R.id.edtBirthday).setText(user.birthday)
        dialogView.findViewById<EditText>(R.id.edtPhone).setText(user.phoneNumber)
        dialogView.findViewById<EditText>(R.id.edtInstagram).setText(user.instagramId)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Contact")
            .setView(dialogView)
            .setCustomTitle(editTitleView)
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
            .create()
        dialog.setOnShowListener {
            // Add 버튼 색상 설정
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(requireContext().getColor(R.color.purple2))

            // Cancel 버튼 색상 설정
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(requireContext().getColor(R.color.purple2))
        }
        // 다이얼로그 창 배경 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.black)

        dialog.show()
    }

    private fun updateUser(userId: Int, userUpdate: UserUpdate) {
        lifecycleScope.launch {
            try {
                RetrofitInstance.api.updateUser(userId, userUpdate)
                Toast.makeText(context, "Contact updated successfully", Toast.LENGTH_SHORT).show()
                fetchUsers()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ContactFragment", "Error updating user", e)
            }
        }
    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val customTitleView = layoutInflater.inflate(R.layout.dialog_title, null)

        val dialog = AlertDialog.Builder(requireContext(), R.style.DarkDialog)  // 스타일 적용
            .setCustomTitle(customTitleView)
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
            .create()
        dialog.setOnShowListener {
            // Add 버튼 색상 설정
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(requireContext().getColor(R.color.purple2))

            // Cancel 버튼 색상 설정
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(requireContext().getColor(R.color.purple2))
        }
        // 다이얼로그 창 배경 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.black)

        dialog.show()
    }

    private fun createUser(userCreate: UserCreate) {
        lifecycleScope.launch {
            try {
                val newUser = RetrofitInstance.api.createUser(userCreate)
                val updatedUser = newUser.copy(profileImageRes = R.drawable.ic_add)
                adapter.updateUsers(adapter.users + updatedUser)
                Toast.makeText(context, "Contact added successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}