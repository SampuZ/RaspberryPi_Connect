package shompu.rasberrypi_connect;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    public TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.testText);
        textView=findViewById(R.id.textView);
    }


    public void Connect2(View v) {
        String command = editText.getText().toString().trim();
        SSHConnectionManager.getInstance(this).executeSSHcommand(command);
    }

    public void Mount(View v){
        SSHConnectionManager.getInstance(this)
                .executeSSHcommand("cd Documents/Scripts;./Mount_BigDrive_Samba.sh");
    }
    public void Unmount(View v){
        SSHConnectionManager.getInstance(this)
                .executeSSHcommand("cd Documents/Scripts;./Unmount_BigDrive.sh");
    }
}
