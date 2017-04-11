package compsci290.edu.duke.agora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button teacher = (Button) findViewById(R.id.teacherLogin);
        Button student = (Button) findViewById(R.id.studentLogin);

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("Clicked");
                Intent teacherIntent = new Intent(getApplicationContext(), TeacherLoginActivity.class);
                startActivity(teacherIntent);
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("Clicked");
                Intent studentIntent = new Intent(getApplicationContext(), StudentLoginActivity.class);
                startActivity(studentIntent);
            }
        });



    }
}
