/** This class represents a server-based announcement entry */

package org.coffeecats.coffeetime.utility;

import org.coffeecats.coffeetime.gamelogic.SavedCharacter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class ServerAnnouncement {
	public int _id;
	public boolean id_set;
	private Integer serverAnnouncementId;
	private String serverAnnouncement;
	
	//For database storage/retrieval
	public static final String TABLE_NAME = "game_announcements";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ANNOUNCEMENT_ID = "announcement_id";
	public static final String COLUMN_ANNOUNCEMENT = "announcement";
	
	public static final String [] COLUMN_ALL = {COLUMN_ID, COLUMN_ANNOUNCEMENT_ID, COLUMN_ANNOUNCEMENT};
	
	public ServerAnnouncement(int new__id, int new_id, String newAnnouncement) {
		_id = new__id;
		this.id_set = true;
		serverAnnouncementId = new Integer(new_id);
		serverAnnouncement = newAnnouncement;
	}
	
	public ServerAnnouncement(int new_id, String newAnnouncement) {
		this(-1, new_id, newAnnouncement);
		this.id_set = false; 
	}
	
	public Integer getServerAnnouncementId() {
		return serverAnnouncementId;
	}
	
	public String getServerAnnouncement() {
		return serverAnnouncement;
	}
	
	public void setId(int new__id) { _id = new__id; }
	
	public boolean equals(Object other) {
		if(!(other instanceof ServerAnnouncement)) return false;
		
		ServerAnnouncement otherServerAnnouncement = (ServerAnnouncement) other;
		
		return(this.serverAnnouncementId.equals(otherServerAnnouncement.getServerAnnouncementId()));
	}
	
	public String toString() {
		return serverAnnouncement;
	}
	
	
	
	
	
/* STUFF FOR DATABASE ACCESS */
	
	/** Create the String used to initialize the SavedCharacter database table. Used by GameDatabaseHelper
	 * to initialize the table that we'll use.
	 * @return A SQL query that initializes the saved character table. 
	 */
	public static String createServerAnnouncementDBString() {
		String returned = new String();
		
		returned += "create table ";
		returned += ServerAnnouncement.TABLE_NAME + "(";
		returned += ServerAnnouncement.COLUMN_ID + " integer primary key autoincrement, ";
		returned += ServerAnnouncement.COLUMN_ANNOUNCEMENT_ID + " integer, ";
		returned += ServerAnnouncement.COLUMN_ANNOUNCEMENT + " text not null";
		returned += ");";
		
		return(returned);
	}
	
	/** Load a saved character, given a Cursor that points to a saved character table entry (row) 
	 * in a database. Returns the ServerAnnouncement that has been loaded from the DB
	 * @param cursor
	 * @return
	 */
	public static ServerAnnouncement loadServerAnnouncement(Cursor cursor) {
		try {
			int id = cursor.getInt(0);
			int announcement_id = cursor.getInt(1);
			String annoucement = cursor.getString(2);
			
			return(new ServerAnnouncement(id, announcement_id, annoucement));
		} catch (SQLException e) {
			e.printStackTrace();
			return(null);
		}
	}
	
	/** Generates the set of ContentValues that can be used to save this ServerAnnoucement to an
	 * Android SQLite database.
	 * @return
	 */
	public ContentValues generateContentValuesforDBInsert() {
		ContentValues returned = new ContentValues();
		
		returned.put(COLUMN_ANNOUNCEMENT_ID, serverAnnouncementId);
		returned.put(COLUMN_ANNOUNCEMENT, serverAnnouncement);
		
		return(returned);
	}

}
