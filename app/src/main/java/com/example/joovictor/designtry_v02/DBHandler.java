package com.example.joovictor.designtry_v02;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 13;
    // Database Name
    private static final String DATABASE_NAME = "iotkitchen";
    // Tabela tomada
    private static final String TABLE_TOMADA = "tomadas";
    // colunas da tabela tomada
    private static final String  KEY_IDTOMADA= "idtomada";
    private static final String KEY_IDCASA= "idcasa";
    private static final String KEY_NUMTOMADA = "numtomadas";
    private static final String KEY_POTENCIA= "potencia";


    //Tabela casa
    private static final String TABLE_CASA = "casa";
    // colunas da tabela casa
    private static final String KEY_NUMDETOMADAS= "numdetomadas";
    private static final String KEY_NUMPESSOAS= "numpessoas";


    //tabela pessoa
    private static final String TABLE_PESSOA = "pessoa";
// colunas table pessoa
private static final String KEY_IDPESSOA = "idpessoa";
    private static final String KEY_NOME = "nomepessoa";
    private static final String KEY_BTADS= "btads";


    //tabela horario
    private static final String TABLE_HORARIO = "horario";
    // colunas table pessoa
    private static final String KEY_IDHORARIO = "idhorario";
    private static final String KEY_SEGUNDOS= "segundos";
    private static final String KEY_DATA= "data";



    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TOMADAS_TABLE = "CREATE TABLE " + TABLE_TOMADA + "("
                + KEY_IDTOMADA + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_IDCASA + " INTEGER,"
                + KEY_NUMTOMADA + " INTEGER," + KEY_POTENCIA+ " INTEGER, "+ " FOREIGN KEY("+KEY_IDCASA+") REFERENCES "+TABLE_CASA+"("+KEY_IDCASA+"))";
        db.execSQL(CREATE_TOMADAS_TABLE);

        String CREATE_CASA_TABLE = "CREATE TABLE " + TABLE_CASA + "("
                + KEY_IDCASA + " INTEGER PRIMARY KEY AUTOINCREMENT," +KEY_NUMDETOMADAS + " INTEGER," + KEY_NUMPESSOAS+ " INTEGER"+ ")";
        db.execSQL(CREATE_CASA_TABLE);

        String CREATE_PESSOA_TABLE = "CREATE TABLE " + TABLE_PESSOA + "("
                + KEY_IDPESSOA + " INTEGER PRIMARY KEY AUTOINCREMENT," +KEY_NOME + " TEXT," + KEY_BTADS+ " TEXT,"+ KEY_IDCASA+ " INTEGER ," + KEY_IDTOMADA+" INTEGER, "+ "FOREIGN KEY("+KEY_IDCASA+") REFERENCES "+TABLE_CASA+"("+KEY_IDCASA+")" +
                "FOREIGN KEY("+KEY_IDTOMADA+") REFERENCES "+TABLE_TOMADA+"("+KEY_IDTOMADA+"))";
        db.execSQL(CREATE_PESSOA_TABLE);

        String CREATE_HORARIO_TABLE = "CREATE TABLE " + TABLE_HORARIO + "("
                + KEY_IDHORARIO + " INTEGER PRIMARY KEY AUTOINCREMENT," +KEY_IDPESSOA + " INTEGER," + KEY_IDTOMADA+ " INTEGER,"+KEY_DATA + " TEXT," + KEY_SEGUNDOS+" DOUBLE,"+
                " FOREIGN KEY("+KEY_IDPESSOA+") REFERENCES "+TABLE_PESSOA+"("+KEY_IDPESSOA+"), "+" FOREIGN KEY("+KEY_IDTOMADA+") REFERENCES "+TABLE_TOMADA+"("+KEY_IDTOMADA+"))";
        db.execSQL(CREATE_HORARIO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOMADA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CASA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PESSOA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HORARIO);
// Creating tables again
        onCreate(db);
    }
    // adicionando a table tomada
    public void addTomada(Tomada tomada) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_IDCASA, tomada.getId_casa()); // potencia do aparelho
        values.put(KEY_POTENCIA, tomada.getpotencia()); // potencia do aparelho
        values.put(KEY_NUMTOMADA, tomada.getNum_tomada()); // Shop Phone Number

// Inserting Row
        db.insert(TABLE_TOMADA, null, values);
        db.close(); // Closing database connection
    }



    // Getting one shop
    public int getPotencia(int idtomada) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TOMADA, new String[]{KEY_IDTOMADA,
                        KEY_IDCASA, KEY_NUMTOMADA, KEY_POTENCIA}, KEY_IDTOMADA + "=?",
                new String[]{String.valueOf(idtomada)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            Tomada tomada = new Tomada((cursor.getInt(0)),
                    cursor.getInt(1), cursor.getInt(2), cursor.getInt(3));
            // return contact

            return cursor.getInt(3);

        } else {
            return 0;
        }


    }


    // Updating a shop
    public int updateTomada(Tomada tomada) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_POTENCIA, tomada.getpotencia ());
        values.put(KEY_NUMTOMADA,tomada.getNum_tomada());

// updating row
        return db.update(TABLE_TOMADA, values, KEY_IDTOMADA + " = ?",
        new String[]{String.valueOf(tomada.getId_tomada())});
    }

    // Getting All Contacts
    public List<Tomada> getAllTomadas() {
        List<Tomada> tomadaList = new ArrayList<Tomada>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TOMADA;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
               Tomada tomada = new Tomada();
               tomada.setId_tomada(Integer.parseInt(cursor.getString(0)));
                tomada.setId_casa(Integer.parseInt(cursor.getString(1)));
                tomada.setNum_tomada(Integer.parseInt(cursor.getString(2)));
                tomada.setpotencia(cursor.getInt(3));

                // Adding contact to list
                tomadaList.add(tomada);
            } while (cursor.moveToNext());
        }

        // return contact list
        return tomadaList;
    }
    public void addCasa(Casa casa) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NUMDETOMADAS, casa.getNum_tomadas()); //
        values.put(KEY_NUMPESSOAS, casa.getNum_pessoas()); //


// Inserting Row
        db.insert(TABLE_CASA, null, values);
        db.close(); // Closing database connection
    }
    // Getting All Contacts
    public List<Casa> getAllCasas() {
        List<Casa> casaList = new ArrayList<Casa>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CASA;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Casa casa = new Casa();
                casa.setId_casa(Integer.parseInt(cursor.getString(0)));
                casa.setNum_tomadas(Integer.parseInt(cursor.getString(1)));
                casa.setNum_pessoas(cursor.getInt(2));

                // Adding contact to list
                casaList.add(casa);
            } while (cursor.moveToNext());
        }

        // return contact list
        return casaList;
    }

    public void addPessoa(Pessoa pessoa) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_IDCASA, pessoa.getId_casa()); //
        values.put(KEY_NOME, pessoa.getNome()); //
        values.put(KEY_BTADS, pessoa.getBluetoothAddress()); //


// Inserting Row
        db.insert(TABLE_PESSOA, null, values);
        db.close(); // Closing database connection

    }
    public String getBTADS(int idpessoa) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PESSOA, new String[]{KEY_IDPESSOA,
                        KEY_IDCASA, KEY_NOME, KEY_BTADS}, KEY_IDPESSOA + "=?",
                new String[]{String.valueOf(idpessoa)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            Pessoa pessoa = new Pessoa((cursor.getInt(0)),
                    cursor.getInt(1), cursor.getString(2), cursor.getString(3));
            // return contact

            return cursor.getString(3);

        } else {
            return null;
        }
    };


    public void addHorario(Horario horario) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_IDPESSOA, horario.getId_pessoa()); //
        values.put(KEY_IDTOMADA, horario.getId_tomada()); //
        values.put(KEY_DATA, horario.getHorario()); //
        values.put(KEY_SEGUNDOS, horario.getSeg()); //


// Inserting Row
        db.insert(TABLE_HORARIO, null, values);
        db.close(); // Closing database connection
    }
    public int updateHorario(Horario horario) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_SEGUNDOS,horario.getSeg());

// updating row
        return db.update(TABLE_HORARIO, values, KEY_IDHORARIO + " = ?",
                new String[]{String.valueOf(horario.getId_horario())});
    }
    public double getSegundos(int idhorario) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HORARIO, new String[]{KEY_IDHORARIO,KEY_IDPESSOA,KEY_IDTOMADA,KEY_DATA , KEY_SEGUNDOS
                        }, KEY_IDHORARIO + "=?",
                new String[]{String.valueOf(idhorario)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            Horario horario = new Horario((cursor.getInt(0)),
                    cursor.getInt(1),cursor.getInt(2), cursor.getString(3), cursor.getDouble(4));
                    ;


            // return contact

            return cursor.getDouble(4);

        } else {
            return 0;
        }


    }


}
