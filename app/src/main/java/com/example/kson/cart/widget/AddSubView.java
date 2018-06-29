package com.example.kson.cart.widget;

import android.content.Context;
import android.media.Image;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.kson.cart.R;

/**
 * Author:kson
 * E-mail:19655910@qq.com
 * Time:2018/06/28
 * Description:
 */
public class AddSubView extends LinearLayout {
    private ImageView add, sub;
    private EditText num;

    public AddSubView(Context context) {
        this(context, null);
    }

    public AddSubView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddSubView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
//加载布局
        View view = LayoutInflater.from(context).inflate(R.layout.add_sub_layout, this, true);
//        addView(view);
        add = view.findViewById(R.id.add_btn);
        sub = view.findViewById(R.id.sub_btn);
        num = view.findViewById(R.id.num_et);

        sub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                onAddSubClicklistener.sub(v, Integer.parseInt(num.getText().toString()));


            }
        });

        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddSubClicklistener.add(v, Integer.parseInt(num.getText().toString()));

            }
        });


    }

    public void setText(int i){
        num.setText(i+"");
    }

    public onAddSubClicklistener onAddSubClicklistener;

    public void setOnAddSubClicklistener(AddSubView.onAddSubClicklistener onAddSubClicklistener) {
        this.onAddSubClicklistener = onAddSubClicklistener;
    }

    public interface onAddSubClicklistener {
        void add(View v, int num);

        void sub(View v, int num);
    }


}
