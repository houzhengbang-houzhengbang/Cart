package com.example.kson.cart.i;

import com.example.kson.cart.bean.CartEntity;

/**
 * Author:kson
 * E-mail:19655910@qq.com
 * Time:2018/06/28
 * Description:
 */
public interface onCheckListener {

    void groupClick(int grouppostion);
    void childClick(int groupPostion,int childPos);
    void totalNumPrice();
}
