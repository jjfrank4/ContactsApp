package com.example.contacts

import kotlinx.coroutines.flow.Flow

class ContactRepository(private val contactDao: ContactDao) {

    fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()

    fun findContacts(searchQuery: String): Flow<List<Contact>> =
        contactDao.findContacts(searchQuery)

    fun getContactsSortedByNameAsc(): Flow<List<Contact>> =
        contactDao.getContactsSortedByNameAsc()

    fun getContactsSortedByNameDesc(): Flow<List<Contact>> =
        contactDao.getContactsSortedByNameDesc()

    suspend fun insert(contact: Contact) = contactDao.insert(contact)

    suspend fun delete(contact: Contact) = contactDao.delete(contact)
}