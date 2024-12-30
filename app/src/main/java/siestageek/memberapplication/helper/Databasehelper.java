package siestageek.memberapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Databasehelper extends SQLiteOpenHelper {

    // 데이터베이스 초기화 변수
    // Device Explorer >> data >> data >> 앱 >> databases
    private static final String DBNAME = "android.db";
    private static final int DBVERSION = 1;
    public List<String> getAllusers;


    // 생성자 - 클래스 호출시 자동으로 sqlite DB 파일 생성 (한번만!)
    public Databasehelper(@Nullable Context context) {
        super(context, DBNAME, null,DBVERSION);
    }
    // DB 파일 생성 시 자동으로 실행하는 메서드
    // 주로 테이블 생성시 사용
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "create table member ("+
                "mno integer primary key autoincrement, "+
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

    // 회원 가입 처리
    public boolean inserMember(String userid, String passwd, String name, String email){
        // 데이블에 레코드를 저장하기위해 초기화
        SQLiteDatabase db = this.getWritableDatabase();
        // 저장할 데이터를 컨테이너로 생성
        ContentValues values = new ContentValues();
        values.put("userid", userid);
        values.put("passwd", passwd);
        values.put("name", name);
        values.put("email", email);

        // 테이블에 데이터 저장
        long result = db.insert("member", null, values);

        // DB 연결 해제
        db.close();

        // 저장 성공 여부 리턴
        return result != -1;
    }

    // 아이디 중복 확인
    public boolean useridCheck(String userid) {
        // 아이디 중복체크를 위해 sqlite 초기화
        SQLiteDatabase db = this.getReadableDatabase();
        // 커서 초기화
        // select mno from member where userid = ?
        // query(table, colums, select, groupby, having, orderby)
        Cursor cur = db.query("member", new String[]{"mno"},
                "userid=?", new String[]{userid},
                null, null, null);
        // 조회결과 확인
        boolean exists = cur.getCount() > 0;

        // DB 연결 해제
        cur.close();
        db.close();

        return exists;
    }

    // 회원 목록 조회 메서드
    public List<String> getAllUsers(){
        // 변수 초기화
        String sql = "select userid, name, email from member";
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> userList = new ArrayList<>();

        // 쿼리 실행
        // rawQuery (질의문, 매개변수)
        Cursor cur = db.rawQuery(sql, null);

        // 결과
        if(cur.moveToFirst()){ // 읽어올 데이터가 존재한다면
            do{
                // 커서로부터 데이터 가져오기
                String userid = cur.getString(0);
                String name = cur.getString(1);
                String email = cur.getString(2);

                // 가져온 데이터들을 문자열로 조합
                String userinfo = "아이디 : " + userid + "\n"
                        + "이름 : " + name + "\n"
                        + "이메일 : " + email;
                // 조합한 문자열을 동적배열에 저장
                userList.add(userinfo);
            }while (cur.moveToNext()); // 다음 데이터를 읽어옴

        }
        // DB 연결 해제
        cur.close();
        db.close();

        return userList;
    }

    // 로그인 확인
    public boolean loginUser(String userid, String passwd){
        String sql = "SELECT name FROM member WHERE userid = ? AND passwd = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        String[] params = new String[]{userid, passwd};
        Cursor cur = db.rawQuery(sql, params);

        // 결과 확인
        boolean isLoggedIn = cur.getCount() > 0;

        // DB 연결 해제
        cur.close();
        db.close();

        return isLoggedIn;
    }
}
