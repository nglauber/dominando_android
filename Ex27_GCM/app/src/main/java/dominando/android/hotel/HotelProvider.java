package dominando.android.hotel;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class HotelProvider extends ContentProvider {

    private static final String AUTHORITY = "dominando.android.hotel";
    private static final String PATH = "hoteis";
    private static final int TIPO_GERAL = 1;
    private static final int TIPO_HOTEL_ESPECIFICO = 2;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY +"/"+ PATH);

    private HotelSQLHelper mHelper;

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, PATH, TIPO_GERAL);
        sUriMatcher.addURI(AUTHORITY, PATH + "/#", TIPO_HOTEL_ESPECIFICO);
    }

    @Override
    public boolean onCreate() {
        mHelper = new HotelSQLHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null; // Não implementamos busca por MimeType
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mHelper.getWritableDatabase();
        long id = 0;

        switch (uriType) {
            case TIPO_GERAL:
                id = sqlDB.insertWithOnConflict(HotelSQLHelper.TABELA_HOTEL,
                        null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;

            default:
                throw new IllegalArgumentException("URI não suportada: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.withAppendedPath(CONTENT_URI, String.valueOf(id));
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mHelper.getWritableDatabase();

        int linhasAfetadas = 0;

        switch (uriType) {
            case TIPO_GERAL:
                linhasAfetadas = sqlDB.update(HotelSQLHelper.TABELA_HOTEL,
                        values, selection, selectionArgs);
                break;

            case TIPO_HOTEL_ESPECIFICO:
                String id = uri.getLastPathSegment();
                linhasAfetadas = sqlDB.update(HotelSQLHelper.TABELA_HOTEL,
                        values, HotelSQLHelper.COLUNA_ID +"= ?",
                        new String[]{ id });
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return linhasAfetadas;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB =  mHelper.getWritableDatabase();

        int rowsDeleted = 0;
        switch (uriType) {
            case TIPO_GERAL:
                rowsDeleted = sqlDB.delete(HotelSQLHelper.TABELA_HOTEL,
                        selection, selectionArgs);
                break;

            case TIPO_HOTEL_ESPECIFICO:
                String id = uri.getLastPathSegment();
                rowsDeleted = sqlDB.delete(HotelSQLHelper.TABELA_HOTEL,
                        HotelSQLHelper.COLUNA_ID +"= ?",
                        new String[]{ id });
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        SQLiteQueryBuilder queryBuilder =  new SQLiteQueryBuilder();
        queryBuilder.setTables(HotelSQLHelper.TABELA_HOTEL);

        Cursor cursor = null;

        switch (uriType) {
            case TIPO_GERAL:
                cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case TIPO_HOTEL_ESPECIFICO:
                queryBuilder.appendWhere(HotelSQLHelper.COLUNA_ID + "= ?");

                cursor = queryBuilder.query(db, projection, selection,
                        new String[]{ uri.getLastPathSegment() },
                        null, null, null);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }
}

