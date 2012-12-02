/** Implements the top-level DAO for the saved character database. Used for saving and loading
 * saved characters from the game DB. The use model is to call load initially, which populates 
 * databaseCache, and load characters from that cache. When a character needs to be saved, 
 * saveCharacterToDatabase() should be called, which will not only update the database but
 * also the cache (it's a write-through cache).
 * 
 * Inspired / educated by http://www.vogella.com/articles/AndroidSQLite/article.html#overview
 * 
 */

package org.coffeecats.coffeetime.utility;

import java.util.Set;
import java.util.TreeMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AnnouncementDatabase {
	private static final String activitynametag="AnnoucementDatabase";
	
	private SQLiteDatabase db;
	private AnnouncementDatabaseHelper dbHelper;
	
	//Write through cache of loaded ServerAnnouncements, indexed by name
	public TreeMap<Integer, ServerAnnouncement> databaseCache;
	public boolean ready;
	
	public AnnouncementDatabase(Context context) {
		dbHelper = new AnnouncementDatabaseHelper(context);
		ready = false;
	}
	
	public void open() throws SQLException { db = dbHelper.getWritableDatabase(); }
	public void close() { dbHelper.close(); }
	
	/** Load databaseCache from the SQLite database file */
	public synchronized void loadDatabase() {
		databaseCache = new TreeMap<Integer, ServerAnnouncement>();
		
		Cursor cursor = db.query(ServerAnnouncement.TABLE_NAME, ServerAnnouncement.COLUMN_ALL, 
				null, null, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			ServerAnnouncement newSC = ServerAnnouncement.loadServerAnnouncement(cursor);
			if(newSC != null) databaseCache.put(newSC.getServerAnnouncementId(), newSC);
			cursor.moveToNext();
		}
		
		cursor.close();
		ready = true;
	}
	
	public synchronized void flushCache() {
		databaseCache = null;
		ready = false;
	}
	
	public synchronized boolean databaseContainsId(int query_annoucement_id) {
		if(!ready) loadDatabase();
		
		if(ready) return databaseCache.containsKey(query_annoucement_id);
		else {
			Log.e(activitynametag, "Database never went ready.");
			return false;
		}
	}
	
	/** Save a provided ServerAnnouncement to the database, updating the cache along the way 
	 * 
	 * @param sc The ServerAnnouncement to save into our database. Its _id member variable will be updated after
	 * this operation, and will be validated if it is presently invalid (i.e. we've never saved this member to
	 * the database before).
	 * */
	public synchronized void saveAnnouncementToDatabase(ServerAnnouncement sc) {
		if(databaseCache == null || !ready) {
			loadDatabase();
		}
		
		//Delete any existing entries in the database with the same SA ID
		if(databaseCache.containsKey(sc.getServerAnnouncementId())) {
			ServerAnnouncement existing_sa = databaseCache.get(sc.getServerAnnouncementId());
			db.delete(ServerAnnouncement.TABLE_NAME, ServerAnnouncement.COLUMN_ID + " = " + existing_sa._id, null);
			databaseCache.remove(sc.getServerAnnouncementId());
		}
		
		ContentValues values = sc.generateContentValuesforDBInsert();
		databaseCache.put(sc.getServerAnnouncementId(), sc);
		int insertId = (int) db.insert(ServerAnnouncement.TABLE_NAME, null, values);
		sc.setId(insertId);
	}
	
	/** This method is used to print the contents of the database cache to Log.v. Usually used for 
	 * debugging and manually checking the database contents.
	 */
	public void printCache() {
		if(databaseCache != null) {
			Set<Integer>keys = databaseCache.keySet();
			
			Log.v(activitynametag, "Annoucement Database contains " + keys.size() + " keys:");
			
			for( Integer k : keys ) {
				Log.v(activitynametag, databaseCache.get(k).toString());
			}
		}
	}
	
	/** This method is used to run a simple, user-interpreted test of AnnoucementDatabase. It
	 * creates a AnnoucementDatabase and fills it with some crap, and then dumps the result to
	 * Log.v.
	 * @param context The calling Context for this method (usually the Activity that called it)
	 */
	public static void test (Context context) {
		AnnouncementDatabase gameDB = new AnnouncementDatabase(context);
		gameDB.open();
		
		gameDB.saveAnnouncementToDatabase(new ServerAnnouncement(1, "hello"));
		gameDB.saveAnnouncementToDatabase(new ServerAnnouncement(2, "hi there"));
		gameDB.saveAnnouncementToDatabase(new ServerAnnouncement(3, "bye bye"));
		
		gameDB.loadDatabase();
		gameDB.printCache();
		
		ServerAnnouncement ivan = gameDB.databaseCache.get(2);
		Log.v(activitynametag, "ID #2 = " + ivan.toString());
		
		gameDB.saveAnnouncementToDatabase(new ServerAnnouncement(2, "updated string??"));
		
		gameDB.flushCache();
		gameDB.loadDatabase();
		gameDB.printCache();
		
		context.deleteDatabase(AnnouncementDatabaseHelper.DATABASE_NAME);
	}
}
