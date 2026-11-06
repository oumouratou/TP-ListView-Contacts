
package com.example.contacts

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {


    private lateinit var etContact: EditText
    private lateinit var btnAjouter: Button
    private lateinit var btnModifier: Button
    private lateinit var lvContacts: ListView


    private lateinit var adapter: ArrayAdapter<String>


    private lateinit var viewModel: ContactsViewModel


    private var positionSelectionnee = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        etContact = findViewById(R.id.etContact)
        btnAjouter = findViewById(R.id.btnAjouter)
        btnModifier = findViewById(R.id.btnModifier)
        lvContacts = findViewById(R.id.lvContacts)


        viewModel = ViewModelProvider(this)
            .get(ContactsViewModel::class.java)


        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_activated_1,
            viewModel.contacts
        )

        lvContacts.adapter = adapter


        lvContacts.setOnItemClickListener { _, _, position, _ ->

            positionSelectionnee = position

            lvContacts.setItemChecked(position, true)

            etContact.setText(viewModel.contacts[position])

            etContact.setSelection(etContact.text.length)
        }


        btnAjouter.setOnClickListener {
            ajouterContact()
        }


        btnModifier.setOnClickListener {
            modifierContact()
        }


        lvContacts.setOnItemLongClickListener {
                _, _, position, _ ->

            supprimerContact(position)

            true
        }
    }


    private fun ajouterContact() {

        val nouveauNom = etContact.text.toString().trim()

        if (nouveauNom.isEmpty()) {
            etContact.error = "Entrez un nom"
            return
        }


        if (viewModel.contacts.any {
                it.equals(nouveauNom, ignoreCase = true)
            }) {

            etContact.error = "Ce contact existe déjà"
            return
        }


        viewModel.contacts.add(nouveauNom)


        viewModel.contacts.sortBy { it.lowercase() }


        adapter.notifyDataSetChanged()


        etContact.text.clear()
        positionSelectionnee = -1
        lvContacts.clearChoices()

        Toast.makeText(
            this,
            "Contact ajouté avec succès",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun modifierContact() {

        if (positionSelectionnee == -1) {

            Toast.makeText(
                this,
                "Sélectionnez d'abord un contact",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val nouveauNom = etContact.text.toString().trim()

        if (nouveauNom.isEmpty()) {
            etContact.error = "Le nom ne peut pas être vide"
            return
        }


        val existe = viewModel.contacts.withIndex().any {
            it.index != positionSelectionnee &&
                    it.value.equals(nouveauNom, ignoreCase = true)
        }

        if (existe) {
            etContact.error = "Ce contact existe déjà"
            return
        }


        viewModel.contacts[positionSelectionnee] = nouveauNom


        viewModel.contacts.sortBy { it.lowercase() }

        adapter.notifyDataSetChanged()


        positionSelectionnee = -1
        lvContacts.clearChoices()
        etContact.text.clear()

        Toast.makeText(
            this,
            "Contact modifié avec succès",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun supprimerContact(position: Int) {

        val nom = viewModel.contacts[position]


        AlertDialog.Builder(this)
            .setTitle("Supprimer le contact")
            .setMessage("Voulez-vous vraiment supprimer $nom ?")

            .setPositiveButton("Oui") { _, _ ->

                viewModel.contacts.removeAt(position)

                adapter.notifyDataSetChanged()

                positionSelectionnee = -1
                lvContacts.clearChoices()
                etContact.text.clear()

                Toast.makeText(
                    this,
                    "Contact supprimé",
                    Toast.LENGTH_SHORT
                ).show()
            }

            .setNegativeButton("Non", null)

            .show()
    }
}
