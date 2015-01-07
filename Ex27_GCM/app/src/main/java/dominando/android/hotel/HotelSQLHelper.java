package dominando.android.hotel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HotelSQLHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "dbHotel";
    private static final int VERSAO_BANCO = 1;

    public static final String TABELA_HOTEL = "hotel";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_NOME = "nome";
    public static final String COLUNA_ENDERECO = "endereco";
    public static final String COLUNA_ESTRELAS = "estrelas";
    public static final String COLUNA_STATUS = "status";
    public static final String COLUNA_ID_SERVIDOR = "id_servidor";

    public HotelSQLHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE "+ TABELA_HOTEL +" (" +
                        COLUNA_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        COLUNA_NOME     +" TEXT NOT NULL, "+
                        COLUNA_ENDERECO +" TEXT, " +
                        COLUNA_ESTRELAS +" REAL, " +
                        COLUNA_STATUS      +" INTEGER, " +
                        COLUNA_ID_SERVIDOR +" INTEGER UNIQUE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // para as próximas versões
    }
}

