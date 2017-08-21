package tufer.com.menutest.UIActivity.update;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;



public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "down.db";

    private static final int VERSION = 1;
	
	private static DBOpenHelper helper = null;
	
	public static DBOpenHelper getInstance(Context context){  
        if (helper == null){  
            synchronized (DBOpenHelper.class) {  
                if (helper == null){  
                    DBOpenHelper temp = new DBOpenHelper(context);  
                    helper = temp;  
                }  
            }  
        }  
          
        return helper;  
    }

    

    public DBOpenHelper(Context context) {

        super(context, DBNAME, null, VERSION);

    }

    

    @Override

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (id integer primary key autoincrement, downpath varchar(100), threadid INTEGER, downlength INTEGER)");

    }



    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS filedownlog");

        onCreate(db);

    }

}