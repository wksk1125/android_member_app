package siestageek.memberapplication.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Databasehelper extends SQLiteOpenHelper {

    // 데이터베이스 초기화 변수
    private static final String DBNAME = "android.db";
    private static final int DBVERSION = 1;


    // 생성자 - 클래스 호출시 자동으로 sqlite DB 파일 생성 (한번만!)
    public Databasehelper(@Nullable Context context) {
        super(context, DBNAME, null,DBVERSION);
    }
    // DB 팡리 생성 시 자동으로 실행하는 메서드
    // 주로 테이블 생성시 사용
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "create table member ("+
                "mno int primary key autoincrement, "+
                "userid varchar(18) unique," +
                "passwd varchar(18) not null, "+
                "name varchar(18) not null, "+
                "email text not null,"+
                "regdate date default current_timestamp)";
        db.execSQL(SQL);
    }

    // 테이블 재생성시 사용
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists member");
        onCreate(db);
    }
}
