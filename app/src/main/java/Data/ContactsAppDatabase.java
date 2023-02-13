package Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import Model.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactsAppDatabase extends RoomDatabase {

    public abstract ContactDAO getContactDAO();
}
