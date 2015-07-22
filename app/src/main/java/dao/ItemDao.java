package dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import model.Item;

/**
 * Created by igiagante on 22/7/15.
 */
public class ItemDao {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ITEM_ID,
            MySQLiteHelper.COLUMN_PRICE,
            MySQLiteHelper.COLUMN_EXPIRATION_DATE};

    public ItemDao(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }


    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Item createItem(Item item) {

        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_ID, item.getId());
        values.put(MySQLiteHelper.COLUMN_ITEM_ID, item.getItemId());
        values.put(MySQLiteHelper.COLUMN_PRICE, item.getPrice());
        values.put(MySQLiteHelper.COLUMN_EXPIRATION_DATE, item.getExpirationDate());

        long insertId = database.insert(MySQLiteHelper.TABLE_ITEMS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Item newItem = cursorToItem(cursor);
        cursor.close();
        return newItem;
    }

    public void deleteItem(Item item) {
        long id = item.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ITEMS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public Item getItem(String itemId){
        String[] args = new String[] {itemId};
        Cursor cursor = database.rawQuery(" SELECT _id, item_id, price, stop_time FROM Items WHERE item_id=? ", args);
        cursor.moveToFirst();
        Item item = cursorToItem(cursor);
        cursor.close();
        return item;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<Item>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    public boolean exist(String itemId){
        String[] args = new String[] {itemId};
        Cursor cursor = database.rawQuery(" SELECT _id, item_id, price, stop_time FROM Items WHERE item_id=? ", args);
        return cursor.getCount() == 1;
    }

    private Item cursorToItem(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getLong(0));
        item.setItemId(cursor.getString(1));
        item.setPrice(cursor.getString(2));
        item.setExpirationDate(cursor.getString(3));
        return item;
    }
}