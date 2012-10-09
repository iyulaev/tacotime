/** Implements the top-level DAO for the saved character database. Used for saving and loading
 * saved characters from the game DB. The use model is to call load initially, which populates 
 * databaseCache, and load characters from that cache. When a character needs to be saved, 
 * saveCharacterToDatabase() should be called, which will not only update the database but
 * also the cache (it's a write-through cache).
 * 
 * Inspired / educated by http://www.vogella.com/articles/AndroidSQLite/article.html#overview
 * 
 */

package org.coffeecats.coffeetime.gamelogic;

import java.util.Set;
import java.util.TreeMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GameDatabase {
	private static final String activitynametag="GameDatabase";
	
	private SQLiteDatabase db;
	private GameDatabaseHelper dbHelper;
	
	//Write through cache of loaded SavedCharacters, indexed by name
	public TreeMap<String, SavedCharacter> databaseCache;
	public boolean ready;
	
	public GameDatabase(Context context) {
		dbHelper = new GameDatabaseHelper(context);
		ready = false;
	}
	
	public void open() throws SQLException { db = dbHelper.getWritableDatabase(); }
	public void close() { dbHelper.close(); }
	
	/** Load databaseCache from the SQLite database file */
	public synchronized void loadDatabase() {
		databaseCache = new TreeMap<String, SavedCharacter>();
		
		Cursor cursor = db.query(SavedCharacter.TABLE_NAME, SavedCharacter.COLUMN_ALL, 
				null, null, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			SavedCharacter newSC = SavedCharacter.loadSavedCharacter(cursor);
			if(newSC != null) databaseCache.put(newSC.name, newSC);
			cursor.moveToNext();
		}
		
		cursor.close();
		ready = true;
	}
	
	public synchronized void flushCache() {
		databaseCache = null;
	}
	
	/** Save a provided SavedCharacter to the database, updating the cache along the way 
	 * 
	 * @param sc The SavedCharacter to save into our database. Its _id member variable will be updated after
	 * this operation, and will be validated if it is presently invalid (i.e. we've never saved this member to
	 * the database before).
	 * */
	public synchronized void saveCharacterToDatabase(SavedCharacter sc) {
		ContentValues values = sc.generateContentValuesforDBInsert();
		
		//If sc already has a valid ID, then we need to delete the existing entry first
		if(sc.id_set) {
			db.delete(SavedCharacter.TABLE_NAME, SavedCharacter.COLUMN_ID + " = " + sc._id, null);
		}
		
		int insertId = (int) db.insert(SavedCharacter.TABLE_NAME, null, values);
		sc.setId(insertId);
		
		if(databaseCache == null) {
			databaseCache = new TreeMap<String, SavedCharacter>();
			ready = true;
		}
		
		databaseCache.put(sc.name, sc);
	}
	
	/** Deletes a given character from the database 
	 * 
	 * @param sc The SavedCharacter to delete from our database.
	 * */
	public synchronized void deleteCharacterFromDatabase(SavedCharacter sc) {		
		//If sc already has a valid ID, then we need to delete the existing entry first
		if(sc.id_set) 
			db.delete(SavedCharacter.TABLE_NAME, SavedCharacter.COLUMN_ID + " = " + sc._id, null);
		
		if(databaseCache != null) databaseCache.remove(sc.name);
	}
	
	/** This method is used to print the contents of the database cache to Log.v. Usually used for 
	 * debugging and manually checking the database contents.
	 */
	public void printCache() {
		if(databaseCache != null) {
			Set<String>keys = databaseCache.keySet();
			
			Log.v(activitynametag, "Database contains " + keys.size() + " keys:");
			
			for( String k : keys ) {
				Log.v(activitynametag, databaseCache.get(k).toString());
			}
		}
	}
	
	/** This method is used to run a simple, user-interpreted test of GameDatabase. It
	 * creates a GameDatabase and fills it with some crap, and then dumps the result to
	 * Log.v.
	 * @param context The calling Context for this method (usually the Activity that called it)
	 */
	public static void test (Context context) {
		GameDatabase gameDB = new GameDatabase(context);
		gameDB.open();
		
		gameDB.saveCharacterToDatabase(new SavedCharacter("megan", "girl"));
		gameDB.saveCharacterToDatabase(new SavedCharacter("ivan", "boy"));
		gameDB.saveCharacterToDatabase(new SavedCharacter("jude", "boy"));
		
		gameDB.loadDatabase();
		gameDB.printCache();
		
		SavedCharacter ivan = gameDB.databaseCache.get("ivan");
		Log.v(activitynametag, "ivan = " + ivan.toString());
		
		ivan.points = 10;
		
		gameDB.saveCharacterToDatabase(ivan);
		
		gameDB.printCache();
		
		context.deleteDatabase(GameDatabaseHelper.DATABASE_NAME);
	}
}
