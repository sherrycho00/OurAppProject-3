package com.example.elsie.framelayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;

import cn.bmob.v3.Bmob;

public class LoginActivity extends FragmentActivity {

    public static String muserid;//获取用户名
    private MySqliteHelper helper;
    Button sign;
    Button reg;

    String  name;
    String  mypwd;
    private EditText user;
    private EditText pwd;
    int userflag ;//定义一个标示判断 用户名是否存在
    int loginflag ;//登录时判断用户密码是否输入正确

    //云数据库应用ID
    public static String APPID = "0f93e5a6104d9b326d354d3b2479ff2e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_login);

        //启动云数据库
        Bmob.initialize(this, APPID);

        //用户已存在，直接登录

        user = (EditText)findViewById(R.id.username);
        pwd=(EditText)findViewById(R.id.userpwd);
        SharedPreferences pref =getSharedPreferences("data",MODE_PRIVATE);
        String name=pref.getString("username","");
        String password=pref.getString("password","");
        muserid=name;
        if(!TextUtils.isEmpty(name)) {
            user.setText(name);
            user.setSelection(name.length());
            pwd.setText(password);
            pwd.setSelection(pwd.length());
            select();
        }

        //播放动图
        ImageView  iv = (ImageView)findViewById(R.id.gif);
        ImageView  iv1 = (ImageView)findViewById(R.id.gif1);
        ImageView  iv2 = (ImageView)findViewById(R.id.gif2);
        String url="http://img1.2345.com/duoteimg/qqbiaoqing/160812512480/23.gif";
        //加载图片
        Glide.with(LoginActivity.this).load(url).into(iv);
        Glide.with(LoginActivity.this).load(url).into(iv1);
        Glide.with(LoginActivity.this).load(url).into(iv2);

        sign=(Button)findViewById(R.id.login);
        reg=(Button)findViewById(R.id.register);
        //用户登录
        sign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                select();
                muserid=user.getText().toString();
            }

        });
        reg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                insert();
                SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("username",user.getText().toString());
                editor.putString("password",pwd.getText().toString());
                editor.apply();
            }
        });


    }


    public void  insert()
    {


        helper = new MySqliteHelper(getApplicationContext());
        SQLiteDatabase db=helper.getWritableDatabase();    //建立打开可读可写的数据库实例



        //查询一下，是否用户名重复
        String sql1 = "select * from users";
        Cursor cursor = db.rawQuery(sql1, null);
        while (cursor.moveToNext()) {
            //第一列为id
            name =  cursor.getString(1); //获取第2列的值,第一列的索引从0开始
            mypwd = cursor.getString(2);//获取第3列的值

            if((user.getText().toString().isEmpty())||(pwd.getText().toString().isEmpty())){

                Toast.makeText(this, "不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                break;
            }


            userflag = 1;  //不存在此用户


            if((user.getText().toString().equals(name)))
            {
                Toast.makeText(this, "已存在此用户，请重新注册", Toast.LENGTH_SHORT).show();


                userflag =0;
                break;
            }

        }

        if(userflag==1)
        {
            String sql2 = "insert into users(name,pwd) values ('"+user.getText().toString()+"','"+pwd.getText().toString()+"')";
            db.execSQL(sql2);
            Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();

        }



    }


    public void select()
    {

        helper = new MySqliteHelper(getApplicationContext());
        SQLiteDatabase db=helper.getWritableDatabase();

        String sql = "select * from users";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            //第一列为id
            name =  cursor.getString(1); //获取第2列的值,第一列的索引从0开始
            mypwd = cursor.getString(2);//获取第3列的值



            if((user.getText().toString().equals(name))&&(pwd.getText().toString().equals(mypwd)))
            {
                Toast.makeText(this, "用户验证成功", Toast.LENGTH_SHORT).show();
                loginflag=1;

                //intent bundle传值
                Intent MainActivity = new Intent();
                MainActivity.setClass(LoginActivity.this,MainActivity.class);
                Bundle bundle = new Bundle(); //该类用作携带数据
                bundle.putString("user", user.getText().toString());
                MainActivity.putExtras(bundle);   //向MainActivity传值
                this.startActivity(MainActivity);
                finish();//退出


            }


        }

        if((user.getText().toString().isEmpty())||(pwd.getText().toString().isEmpty())){

            Toast.makeText(this, "不能为空，请重新输入", Toast.LENGTH_SHORT).show();
        }


        if(loginflag!=1)
        {
            Toast.makeText(this, "账号或者密码错误,请重新输入", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
        //Toast.makeText(this, "已经关闭数据库", Toast.LENGTH_SHORT).show();
    }

}