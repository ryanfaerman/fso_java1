package com.nwitty.java1.twitterquicksearch;

import java.util.HashMap;

import com.nwitty.helpers.FileHelpers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class HistoryProvider extends ContentProvider {
	
	public static final String PROVIDER_NAME = "com.nwitty.java1.twitterquicksearch.HistoryProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/history");
	
	private static final int TERMS = 1;
	private static final int CACHED_RESULT = 2;
	int _id = 0;
	
	public static final String _ID = "_id";
    public static final String TERM = "term";
    public static final String CACHE = "cache";
	
	private static final UriMatcher uriMatcher;
    static{
      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
      uriMatcher.addURI(PROVIDER_NAME, "terms", TERMS);
      uriMatcher.addURI(PROVIDER_NAME, "cache/#", CACHED_RESULT);      
    }
    
    HashMap<String, String> _queryCache;
    MatrixCursor _queryCursor = new MatrixCursor(new String[] {"_id","term","cache"});
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		// noop
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)){
        //---get all terms---
        case TERMS:
           return "vnd.android.cursor.dir/vnd.twitterquicksearch.terms ";
        //---get a particular term---
        case CACHED_RESULT:                
           return "vnd.android.cursor.item/vnd.twitterquicksearch.terms ";
        default:
           throw new IllegalArgumentException("Unsupported URI: " + uri); 
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		Log.i("TRACE", "VALUES: "+values.toString());
		Uri _uri = ContentUris.withAppendedId(CONTENT_URI, _id);
		String term = values.getAsString(TERM);
		String result = values.getAsString(CACHE);
		_queryCursor.addRow(new Object[] {_id, term, result});
		_id++;
		
		_queryCache.put(term, result);
		
		FileHelpers.storeObjectFile(_context, "query_history", _queryCache, false);
        getContext().getContentResolver().notifyChange(_uri, null); 
		return _uri;
	}
	
	Context _context;
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Context context = getContext();
		_context = context;
		 Log.i("TRACE HistoryProvider", "created history provider");
		_queryCache = loadHistory(context);
		return (_queryCache == null)? false:true;
	}

	@Override
	public MatrixCursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Log.i("TRACE HistoryProvider", "queried history provider");
//		_queryCursor.setNotificationUri(getContext().getContentResolver(), uri);
		MatrixCursor c = _queryCursor;
		Log.i("TRACE HistoryProvider", "found c" + c.toString());
		return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// noop
		return 0;
	}
	
	private HashMap<String, String> loadHistory(Context _context) {
    	Object stored = FileHelpers.readObjectFile(_context, "query_history", false);
    	Log.i("TRACE HistoryProvider", "loading history");
    	HashMap<String, String> history;
    	if(stored == null) {
    		Log.i("HISTORY", "NOT HISTORY FILE FOUND");
    		history = new HashMap<String, String>();
    	} else {
    		history = ((HashMap<String, String>) stored);
    	}
    	
//    	history = new HashMap<String, String>();
    	
    	Log.i("TRACE HistoryProvider", "populator cursor");
    	for (String key: history.keySet()) {
        	_queryCursor.addRow(new Object[] {_id, key, history.get(key)});
        	_id++;
        	Log.i("TRACE HistoryProvider", "new entry id: "+_id);
        }
    	
    	Log.i("TRACE HistoryProvider", "_queryCursor count " +_queryCursor.getCount());
    	
    	return history;
    }

}
