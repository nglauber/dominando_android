package dominando.android.hotel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class HotelRepositorio {

    private Context ctx;

    public HotelRepositorio(Context ctx) {
        this.ctx = ctx;
    }

    private long inserir(Hotel hotel) {
        hotel.status = Hotel.Status.INSERIR;
        long id = inserirLocal(hotel, ctx.getContentResolver());
        return id;
    }

    private int atualizar(Hotel hotel) {
        hotel.status = Hotel.Status.ATUALIZAR;
        int linhasAfetadas = atualizarLocal(hotel, ctx.getContentResolver());
        return linhasAfetadas;
    }

    public void salvar(Hotel hotel) {
        if (hotel.id == 0) {
            inserir(hotel);
        } else {
            atualizar(hotel);
        }
    }

    public int excluir(Hotel hotel) {
        hotel.status = Hotel.Status.EXCLUIR;
        int linhasAfetadas = atualizarLocal(hotel, ctx.getContentResolver());
        return linhasAfetadas;
    }

    public CursorLoader buscar(Context ctx, String s) {
        String where = null;
        String[] whereArgs = null;
        if (s != null) {
            where = HotelSQLHelper.COLUNA_NOME +" LIKE ?";
            whereArgs = new String[]{ "%"+ s +"%" };
        }
        return new CursorLoader(
                ctx,
                HotelProvider.CONTENT_URI,
                null,
                where,
                whereArgs,
                HotelSQLHelper.COLUNA_NOME);
    }

    private ContentValues getValues(Hotel hotel) {
        ContentValues cv = new ContentValues();
        cv.put(HotelSQLHelper.COLUNA_NOME, hotel.nome);
        cv.put(HotelSQLHelper.COLUNA_ENDERECO, hotel.endereco);
        cv.put(HotelSQLHelper.COLUNA_ESTRELAS, hotel.estrelas);
        cv.put(HotelSQLHelper.COLUNA_STATUS, hotel.status.ordinal());
        if (hotel.idServidor != 0) {
            cv.put(HotelSQLHelper.COLUNA_ID_SERVIDOR, hotel.idServidor);
        }
        return cv;
    }

    public static Hotel hotelFromCursor(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndex(
                        HotelSQLHelper.COLUNA_ID)
        );
        String nome = cursor.getString(
                cursor.getColumnIndex(
                        HotelSQLHelper.COLUNA_NOME)
        );
        String endereco = cursor.getString(
                cursor.getColumnIndex(
                        HotelSQLHelper.COLUNA_ENDERECO)
        );
        float estrelas = cursor.getFloat(
                cursor.getColumnIndex(
                        HotelSQLHelper.COLUNA_ESTRELAS)
        );

        int status = cursor.getInt(cursor.getColumnIndex(
                HotelSQLHelper.COLUNA_STATUS));
        long idServidor = cursor.getLong(cursor.getColumnIndex(
                HotelSQLHelper.COLUNA_ID_SERVIDOR));
        Hotel hotel = new Hotel(id, nome, endereco, estrelas,
                idServidor, Hotel.Status.values()[status]);

        return hotel;
    }

    public long inserirLocal(Hotel hotel, ContentResolver cr) {
        Uri uri = cr.insert(
                HotelProvider.CONTENT_URI,
                getValues(hotel));
        long id = Long.parseLong(uri.getLastPathSegment());
        if (id != -1) {
            hotel.id = id;
        }
        return id;
    }

    public int atualizarLocal(Hotel hotel, ContentResolver cr) {
        Uri uri = Uri.withAppendedPath(
                HotelProvider.CONTENT_URI, String.valueOf(hotel.id));

        int linhasAfetadas = cr.update(
                uri, getValues(hotel), null, null);
        return linhasAfetadas;
    }

    public int excluirLocal(Hotel hotel, ContentResolver cr) {
        Uri uri = Uri.withAppendedPath(
                HotelProvider.CONTENT_URI, String.valueOf(hotel.id));

        int linhasAfetadas = cr.delete(uri, null, null);
        return linhasAfetadas;
    }

}

