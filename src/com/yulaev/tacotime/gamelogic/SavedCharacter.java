/** This class implements the "data storage object" representing the saved game
 * for a particular character. It has some knowledge of the associated database
 * table name and columns, and can load SavedCharacters (given a DB cursor) from
 * a database and save characters (well, output a set of ContentValues describing
 * a SavedCharacter instance anyway)
 * 
 */

package com.yulaev.tacotime.gamelogic;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class SavedCharacter implements Comparable{
	public int _id;
	public boolean id_set;
	public String name;
	public String type;
	public int money;
	public int points;
	public int level; //last completed level
	public String upgrades;
	
	public static final String TABLE_NAME = "saved_characters";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_MONEY = "money";
	public static final String COLUMN_POINTS = "points";
	public static final String COLUMN_LEVEL = "level";
	public static final String COLUMN_UPGRADES = "upgrades";
	
	public static final String [] COLUMN_ALL = {COLUMN_ID, COLUMN_NAME, COLUMN_TYPE, 
		COLUMN_MONEY, COLUMN_POINTS, COLUMN_LEVEL, COLUMN_UPGRADES};
	
	/** Builds a new SavedCharacter with an already-set ID. Useful typically when loading a SavedCharacter 
	 * definition from a database
	 * @param id
	 * @param name
	 * @param type
	 * @param money
	 * @param points
	 * @param level
	 * @param upgrades
	 */
	public SavedCharacter(int id, String name, String type, int money, int points, int level, String upgrades) {
		this._id = id; //make ID valid
		this.id_set = true;
		this.name = name;
		this.type = type;
		this.money = money;
		this.points = points;
		this.level = level;
		this.upgrades = upgrades;
	}
	
	private SavedCharacter(String name, String type, int money, int points, int level, String upgrades) {
		this._id = -1; //invalidate ID
		this.id_set = false;
		this.name = name;
		this.type = type;
		this.money = money;
		this.points = points;
		this.level = level;
		this.upgrades = upgrades;
	}
	
	/** Build a new SavedCharacter without a set ID; this is used when creating a new character. Level, points, and
	 * money are all set to zero.
	 * @param name
	 * @param type
	 */
	public SavedCharacter(String name, String type) {
		this(name, type, 0, 0, 0, "");
	}
	
	/** Set the ID for this character, and mark it as being valid */
	public void setId(int new_id) {
		_id = new_id;
		id_set = true;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof SavedCharacter)) return false;
		
		SavedCharacter otherSavedCharacter = (SavedCharacter) other;
		
		return(otherSavedCharacter.name.equals(this.name));
	}
	
	public int compareTo(Object other) {
		if(!(other instanceof SavedCharacter)) return -1;
		
		SavedCharacter otherSavedCharacter = (SavedCharacter) other;
		
		return(this.name.compareTo(otherSavedCharacter.name));
	}
	
	
	/* STUFF FOR DATABASE ACCESS */
	
	/** Create the String used to initialize the SavedCharacter database table. Used by GameDatabaseHelper
	 * to initialize the table that we'll use.
	 * @return A SQL query that initializes the saved character table. 
	 */
	public static String createSavedCharacterDBString() {
		String returned = new String();
		
		returned += "create table ";
		returned += SavedCharacter.TABLE_NAME + "(";
		returned += SavedCharacter.COLUMN_ID + " integer primary key autoincrement, ";
		returned += SavedCharacter.COLUMN_NAME + " text not null, ";
		returned += SavedCharacter.COLUMN_TYPE + " text not null, ";
		returned += SavedCharacter.COLUMN_MONEY + " integer, ";
		returned += SavedCharacter.COLUMN_POINTS + " integer, ";
		returned += SavedCharacter.COLUMN_LEVEL + " integer, ";
		returned += SavedCharacter.COLUMN_UPGRADES + " text";
		returned += ");";
		
		return(returned);
	}
	
	/** Load a saved character, given a Cursor that points to a saved character table entry (row) 
	 * in a database. Returns the SavedCharacter that has been loaded from the DB
	 * @param cursor
	 * @return
	 */
	public static SavedCharacter loadSavedCharacter(Cursor cursor) {
		try {
			int id = cursor.getInt(0);
			String name = cursor.getString(1);
			String type = cursor.getString(2);
			int money = cursor.getInt(3);
			int points = cursor.getInt(4); 
			int level = cursor.getInt(5);
			String upgrades = cursor.getString(6);
			
			return(new SavedCharacter(id, name, type, money, points, level, upgrades));
		} catch (SQLException e) {
			e.printStackTrace();
			return(null);
		}
	}
	
	/** Generates the set of ContentValues that can be used to save this SavedCharacter to an
	 * Android SQLite database.
	 * @return
	 */
	public ContentValues generateContentValuesforDBInsert() {
		ContentValues returned = new ContentValues();
		
		//returned.put(COLUMN_ID, _id);
		returned.put(COLUMN_NAME, name);
		returned.put(COLUMN_TYPE, type);
		returned.put(COLUMN_MONEY, money);
		returned.put(COLUMN_POINTS, points);
		returned.put(COLUMN_LEVEL, level);
		returned.put(COLUMN_UPGRADES, upgrades);
		
		return(returned);
	}
	
	@Override
	public int hashCode() {
		int retval = _id;
		retval *= name.hashCode();
		return retval;
	}
	
	/** Return a String representation of this SavedCharacter. Typically used for debugging */
	public String toString() {
		String returned = new String();
		returned += "Character_id=" + _id;
		returned += " => {";
		returned += name + ", ";
		returned += type + ", ";
		returned += money + ", ";
		returned += points + ", ";
		returned += level + ", ";
		returned += "(" + upgrades + ")" + "}";
		
		return returned;
	}
}
