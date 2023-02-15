package com.supersonic.contactsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import Data.ContactsAppDatabase;
import Model.Contact;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    private ContactsAdapter contactsAdapter;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsAppDatabase contactsAppDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.mainRecyclerView);
        contactsAppDatabase = Room.databaseBuilder(getApplicationContext(), ContactsAppDatabase.class, "ContactsDB")
                .build();

        new GetAllContactsAsyncTask().execute();

        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton addContactButton = findViewById(R.id.floatingActionButton);

        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndEditContacts(false, null, -1);
            }
        });


    }

    ItemTouchHelper.SimpleCallback callBackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            deleteContact(contactArrayList.get(position),position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftLabel("Delete")
                            .setSwipeLeftLabelColor(getResources().getColor(R.color.white))
                                    .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                                            .setSwipeLeftActionIconTint(getResources().getColor(R.color.white))
                                                    .addSwipeLeftBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_error))
                                                            .addSwipeLeftCornerRadius(1,6)
                                                                .addSwipeLeftPadding(1,8,8,8)
                                                                    .create()
                                                                        .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void addAndEditContacts(final boolean isUpdate, final Contact contact, final int position){

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView newContactTitle = view.findViewById(R.id.newContactTitle);
        final EditText firstNameEditText = view.findViewById(R.id.firstNameEditText);
        final EditText secondNameEditText = view.findViewById(R.id.secondNameEditText);
        final EditText emailEditText = view.findViewById(R.id.emailAddressEditText);
        final EditText phoneNumberEditText = view.findViewById(R.id.phoneEditText);

        newContactTitle.setText(!isUpdate ? "New Contact" : "Edit Contact");

        if (isUpdate && contact != null){
            firstNameEditText.setText(contact.getFirstName());
            secondNameEditText.setText(contact.getSecondName());
            emailEditText.setText(contact.getEmail());
            phoneNumberEditText.setText(contact.getPhoneNumber());
        }

        alertDialogBuilderUserInput.setCancelable(true)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(firstNameEditText.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter First name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(secondNameEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Second Name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(emailEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(phoneNumberEditText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    alertDialog.dismiss();
                }

                if (isUpdate && contact !=null){
                    updateContact(firstNameEditText.getText().toString(), secondNameEditText.getText().toString(), emailEditText.getText().toString(), phoneNumberEditText.getText().toString()
                    , position);
                }else {
                    createContact(firstNameEditText.getText().toString(), secondNameEditText.getText().toString(), emailEditText.getText().toString(), phoneNumberEditText.getText().toString());
                }
            }
        });

    }

    private void createContact(String firstName, String secondName, String email, String phoneNumber) {
        new CreateContactAsyncTast().execute(new Contact(0, firstName, secondName, email, phoneNumber));
    }

    private void updateContact(String firstName, String secondName, String email, String phoneNumber, int position) {
        Contact contact = contactArrayList.get(position);

        contact.setFirstName(firstName);
        contact.setSecondName(secondName);
        contact.setEmail(email);
        contact.setPhoneNumber(phoneNumber);

        new UpdateContactAsyncTask().execute(contact);

        contactArrayList.set(position, contact);
    }

    private void deleteContact(Contact contact, int position) {
        contactArrayList.remove(position);
        new DeleteContactAsyncTask().execute(contact);
    }

    private class GetAllContactsAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            contactArrayList.addAll(contactsAppDatabase.getContactDAO().getAllContacts());
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            contactsAdapter.notifyDataSetChanged();
        }
    }

    private class CreateContactAsyncTast extends AsyncTask<Contact, Void, Void>{

        @Override
        protected Void doInBackground(Contact... contacts) {

            long id = contactsAppDatabase.getContactDAO().addContact(contacts[0]);

            Contact contact = contactsAppDatabase.getContactDAO().getContact(id);

            if (contact !=null){
                contactArrayList.add(0, contact);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    private class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void>{

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsAppDatabase.getContactDAO().updateContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    private class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void>{

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsAppDatabase.getContactDAO().deleteContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            contactsAdapter.notifyDataSetChanged();
        }
    }
}