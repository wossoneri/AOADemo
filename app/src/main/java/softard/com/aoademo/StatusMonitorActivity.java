package softard.com.aoademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StatusMonitorActivity extends AppCompatActivity {

    TextView tvStatus;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_monitor);
        Toast.makeText(this, "主页", Toast.LENGTH_LONG).show();
        tvStatus = findViewById(R.id.tv_status);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    protected void dumpStatus(final String line) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvStatus.setText(tvStatus.getText() + "\n" + line);
            }
        });
    }
}
