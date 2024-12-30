package siestageek.memberapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraExtensionSession;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import siestageek.memberapplication.helper.Databasehelper;

public class LoginActivity extends AppCompatActivity {

    // 변수 선언
    TextView textView2;
    EditText editTextUserid, editTextPasswd;
    Button buttonLogin;
    Databasehelper databasehelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 변수 초기화
        editTextUserid = findViewById(R.id.editTextUserid);
        editTextPasswd = findViewById(R.id.editTextPasswd);
        buttonLogin = findViewById(R.id.buttonLogin);
        databasehelper = new Databasehelper(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // 로그인 이벤트 처리
        buttonLogin.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // 입력한 아이디/비밀번호를 가져옴
                        String userid = editTextUserid.getText().toString();
                        String passwd = editTextPasswd.getText().toString();

                        // 로그인 성공 여부에 따른 처리
                        if (databasehelper.loginUser(userid, passwd)) {
                            // 로그인 성공시 SharePreferences에 세션 데이터 저장
                            // SharePreferences 수정을 위한 초기화
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply(); // 추가한 내용 저장

                            Intent intent = new Intent(LoginActivity.this, UserlistActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}