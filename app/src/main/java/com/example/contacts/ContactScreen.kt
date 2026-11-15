package com.example.contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContactScreen(viewModel: ContactViewModel) {
    val contacts by viewModel.allContacts.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Contacts", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; nameError = null },
            label = { Text("Name") },
            isError = nameError != null,
            supportingText = { nameError?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; phoneError = null },
            label = { Text("Phone (999.999.9999)") },
            isError = phoneError != null,
            supportingText = { phoneError?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                nameError = if (name.isBlank()) "Name cannot be blank" else null
                phoneError = when {
                    phone.isBlank() -> "Phone number cannot be blank"
                    !viewModel.isValidPhoneNumber(phone) -> "Phone must be in the format 999.999.9999"
                    else -> null
                }
                if (nameError == null && phoneError == null) {
                    viewModel.insert(Contact(name = name.trim(), phoneNumber = phone))
                    name = ""
                    phone = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Add") }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            label = { Text("Search") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.onSortOrderChange(SortOrder.DESC) },
                modifier = Modifier.weight(1f)
            ) { Text("Sort Descending") }
            Button(
                onClick = { viewModel.onSortOrderChange(SortOrder.ASC) },
                modifier = Modifier.weight(1f)
            ) { Text("Sort Ascending") }
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(contacts, key = { it.id }) { contact ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(contact.name, style = MaterialTheme.typography.titleMedium)
                        Text(contact.phoneNumber, style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(onClick = { contactToDelete = contact }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete contact")
                    }
                }
                HorizontalDivider()
            }
        }
    }

    contactToDelete?.let { contact ->
        AlertDialog(
            onDismissRequest = { contactToDelete = null },
            title = { Text("Delete Contact") },
            text = { Text("Are you sure you want to delete ${contact.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(contact)
                    contactToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { contactToDelete = null }) { Text("Cancel") }
            }
        )
    }
}