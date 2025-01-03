package siestageek.memberapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import siestageek.memberapplication.helper.Databasehelper;
import siestageek.memberapplication.helper.MariaDBHelper;

public class MainActivity extends AppCompatActivity {

    // 변수선언
    private EditText editTextUserid, editTextPasswd, editTextName, editTextEmail;
    private Button buttonJoin, buttonUserlist;
    //private Databasehelper databasehelper;
    private MariaDBHelper mariaDBHelper;

    // SharedPreferences : 경량 데이터 저장하기 위한 내부 객체
    //
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 뷰 초기화
        editTextUserid = findViewById(R.id.editTextUserid);
        editTextPasswd = findViewById(R.id.editTextPasswd);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextTextEmail);
        buttonJoin = findViewById(R.id.buttonJoin);
        buttonUserlist = findViewById(R.id.buttonUserlist);

        // 데이터베이스 헬퍼 초기화
        //databasehelper = new Databasehelper(this);
        mariaDBHelper = new MariaDBHelper();

        // sharePreferences 초기화
        // MODE_PRIVATE : 특정 앱만 접근 가능하도록 설정
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // 회원가입 이벤트 처리
        // Main Activity에서 네트워크 작업 시도 시
        // NetworkOnmainThreadException 발생
        // 즉, 메인쓰레드에서 네트워크 작업은 기본적으로 금지되어 있음
        // 특정 activity가 네트워크를 독점적으로 점유하는 것을 방지
        // 비동기방식
        buttonJoin.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        registarUser();
                    }
                }
        );

        // 회원조회 이벤트 처리
        buttonUserlist.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        // 로그인 관련 변수 가져오기
                        // getBoolean (키, 기본값)
                        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                        if(isLoggedIn){ // 로그인 했다면 UserListActivity를 뷰에 표시
                            Intent intent = new Intent(MainActivity.this, UserlistActivity.class);
                            startActivity(intent);
                            //Toast.makeText(MainActivity.this, "UserlistActivity 표시", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            //Toast.makeText(MainActivity.this, "LoginActivity 표시", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void registarUser() {
        String userid = editTextUserid.getText().toString().trim();
        String passwd = editTextPasswd.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        // 입력값 검증
        if (userid.isEmpty() || passwd.isEmpty() || email.isEmpty() || name.isEmpty()){
            Toast.makeText(this, "모든 필드를 입력하세요!", Toast.LENGTH_SHORT).show();
            return; // 여기서 중지
        }

        /*// 중복 아이디 체크
        //if (databasehelper.useridCheck(userid)){
        if (mariaDBHelper.useridCheck(userid)){
            Toast.makeText(this, "이미 사용중인 아이디입니다.", Toast.LENGTH_SHORT).show();
            return; // 여기서 중지
        }
        // 회원 저장
        //boolean success = databasehelper.inserMember(userid, passwd, name, email);
        boolean success = mariaDBHelper.insertMember(userid, passwd, name, email);
        if(success){
            Toast.makeText(this, "✨회원 가입 성공!✨", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "💥회원 가입 실패, 다시 시도해주세요.💥", Toast.LENGTH_SHORT).show();
        }*/

        // 기존 코드 삭제 - RegisterUserTask 에서 재작성했기 때문
        // 기존코드는 비동기처리를 위해 다음과 같이 호출
        new RegistarUserTesk().execute(userid, passwd, name, email);
    }

    // AsyncTask 처리를 위한 비동기 처리 클래스
    private  class RegistarUserTesk extends AsyncTask<String, Void, Boolean>{

        // doInBackground
        // 시간이 오래 걸리는 작업 수행에 사용
        // 네트워크, 데이터베이스, 파일처리등의 작업 - UI 스레드가 차단
        @Override
        protected  Boolean doInBackground(String... params){
            String userid = params[0];
            String passwd = params[1];
            String name = params[2];
            String email = params[3];

            try{
                // 중복 아이디 체크
                if(mariaDBHelper.useridCheck(userid)){
                    return null;
                }
                // 회원 정보 저장
                return mariaDBHelper.insertMember(userid,passwd,name,email);
            } catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        // onPostExcute
        // onInBackground의 결과를 수신해서 UI에 반영할때 사용
        @Override
        protected void onPostExecute(Boolean success){
            if(success == null){ // 아이디가 중복이면
                Toast.makeText(MainActivity.this, "이미 사용중인 아이디입니다.", Toast.LENGTH_SHORT).show();
            } else if(success) { // 회원 정보가 정상적으로 저장되었다면
                Toast.makeText(MainActivity.this, "✨회원 가입 성공!✨", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "💥회원 가입 실패, 다시 시도해주세요.💥", Toast.LENGTH_SHORT).show();
            }
        }
    }
}