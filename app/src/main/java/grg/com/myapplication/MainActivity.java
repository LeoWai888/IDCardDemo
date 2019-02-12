package grg.com.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.grg.idcard.IDCardMsg;
import com.grg.idcard.IDCardRecognition;


public class MainActivity extends AppCompatActivity {

    private IDCardRecognition mIDCardRecognition; // 身份证识别类


    private ImageView mImage;

    private IDCardRecognition.IDCardRecListener mIDCardRecListener = new IDCardRecognition.IDCardRecListener() {
        @Override
        public void onResp(final IDCardMsg info) {
           // print info


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getApplicationContext(),info.getName(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),info.getIdCardNum(),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),info.getSex(),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),info.getUsefulEndDate(),Toast.LENGTH_LONG).show();
                    mImage.setImageBitmap(info.getPortrait());

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIDCardRecognition = new IDCardRecognition(this,mIDCardRecListener);
        mIDCardRecognition.start();
        mImage=(ImageView)findViewById(R.id.img_card);



    }


}
