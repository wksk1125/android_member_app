package siestageek.memberapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import siestageek.memberapplication.helper.Databasehelper;

public class UserlistActivity extends AppCompatActivity {

    // 변수 선언
    private ListView listView;
    private Databasehelper databasehelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_userlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 뷰 초기화
        listView = findViewById(R.id.listView);

        // 데이터베이스 헬퍼 초기화
        databasehelper = new Databasehelper(this);

        // 회원 목록 가져오기
        List<String> userlist = getUserFromDatabase();

        // listView에 회원 목록 표시
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userlist);
        listView.setAdapter(adapter);

    } // onCreate

    // 데이터베이스에서 회원 목록 가져오기
    private List<String> getUserFromDatabase(){
        return databasehelper.getAllUsers();
    }
} // Activity