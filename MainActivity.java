package com.itydl.app02;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageView ivmainscan;
    TextView tvmainscan;
    ProgressBar pbmainscan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        //实现动画扫描
        initScanAnimation();

        //实现进度条加载与扫描同步
        initScanProgressBar();
    }

    private void initScanProgressBar() {
        //@1:对应的参数是：1、doInBackground回调中的传入的参数类型；2、执行任务execute(...)中的参数类型
        //@2:进度参数，与进度有关。onProgressUpdate的参数类型
        //@3：1、doInBackground的返回值类型；2、执行结果onPostExecute传入的参数类型
        new AsyncTask<Integer, Integer, Boolean>() {

            //第一段，准备耗时操作
            @Override
            protected void onPreExecute() {
                // 主线程执行。准备执行前调用，用于界面初始化操作
                tvmainscan.setText("引擎正在扫描中，请稍后...");
            }

            //第二段
            @Override
            protected Boolean doInBackground(Integer... params) {
                // 执行中。子线程执行，用于耗时操作
                // 在这里可以拿到执行任务execute(...)传入的参数，可以以数组形式分别取到
                int start = params[0];
                int end = params[1];

                //真正的耗时
                for (int i = start; i < end; i++) {
                    SystemClock.sleep(50);//每加载一个进度，睡20微秒
                    publishProgress(i);
                }
                return true;//把值返回给onPostExecute
            }

            //用于更新进度，进度改变时候的回调，一般用于进度结果的UI更新
            @Override
            protected void onProgressUpdate(Integer... values) {
                // 主线程执行的回调，可更新进度。values参数接收doInBackground调用publishProgress时候推过来的参数。
                // 每次推一个值。因此每次数组长度就是0
                int progress = values[0];
                pbmainscan.setProgress(progress);
            }

            //第三段
            @Override
            protected void onPostExecute(Boolean result) {
                // 主线程中执行。执行完成的回调,即获得数据后的回调，一般在这里进行结果UI展示
                // 这里可以接收doInBackground的返回值，获取结果
                if(result){
                    //说明进度加载成功。
                    Toast.makeText(getApplicationContext(),"扫描成功！",Toast.LENGTH_SHORT).show();
                    tvmainscan.setText("恭喜扫描完毕");
                    tvmainscan.setTextColor(Color.GREEN);
                }else{
                    Toast.makeText(getApplicationContext(),"扫描失败！",Toast.LENGTH_SHORT).show();
                    tvmainscan.setText("很遗憾扫描失败");
                    tvmainscan.setTextColor(Color.RED);
                }
                //执行关闭的逻辑
                //1、进度条设置为不可见
                pbmainscan.setVisibility(View.GONE);
                //2、动画停止扫描
                ivmainscan.clearAnimation();
            }
        }.execute(0, 100);//最小进度为0，最大进度为100
    }

    private void initScanAnimation() {
        //直接使用补间动画代码设置
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        //设置线性，表示x和y坐标同比例变化。效果让动画匀速
        rotateAnimation.setInterpolator(new LinearInterpolator());
        //设置动画无限循环
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        //开启动画
        ivmainscan.startAnimation(rotateAnimation);//ivmainscan.clearAnimation();清除动画
    }

    private void initView() {
        ivmainscan = (ImageView) findViewById(R.id.iv_main_scan);
        tvmainscan = (TextView) findViewById(R.id.tv_main_scan);
        pbmainscan = (ProgressBar) findViewById(R.id.pb_main_scan);
    }
}
