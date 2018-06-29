package com.example.kson.cart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.kson.cart.adapter.CartAdapter;
import com.example.kson.cart.api.CartApi;
import com.example.kson.cart.bean.CartEntity;
import com.example.kson.cart.common.Constants;
import com.example.kson.cart.i.onCheckListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements onCheckListener {
    private ExpandableListView expandableListView;
    private CheckBox checkBox;
    private TextView priceTv;
    CartAdapter cartAdapter;
    CartEntity cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadData();
    }

    /**
     * 网络请求购物车数据
     */
    private void loadData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CartApi cartApi = retrofit.create(CartApi.class);

        cartApi.getData("71").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<CartEntity>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(CartEntity cartEntity) {
                System.out.println("cartentity:" + cartEntity.code);
                cart = cartEntity;
                fillData();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Throwable:" + e + "");
            }

            @Override
            public void onComplete() {

            }
        });


    }


    /**
     *
     */
    private void fillData() {

        cartAdapter = new CartAdapter(this, cart);
        expandableListView.setAdapter(cartAdapter);
        for (int i = 0; i < cart.data.size(); i++) {
            expandableListView.expandGroup(i);
        }

        cartAdapter.setOnCheckListener(this);
    }

    private void initView() {

        expandableListView = findViewById(R.id.elv);
        checkBox = findViewById(R.id.all_checkbox);
        priceTv = findViewById(R.id.total_price);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    for (int i = 0; i < cart.data.size(); i++) {
                        cart.data.get(i).setGroupChecked(false);
                        for (int i1 = 0; i1 < cart.data.get(i).list.size(); i1++) {
                            cart.data.get(i).list.get(i1).setChildChecked(false);
                        }
                    }

                } else {
                    for (int i = 0; i < cart.data.size(); i++) {
                        cart.data.get(i).setGroupChecked(true);
                        for (int i1 = 0; i1 < cart.data.get(i).list.size(); i1++) {
                            cart.data.get(i).list.get(i1).setChildChecked(true);
                        }
                    }
                }
                cartAdapter.notifyDataSetChanged();
                //总价计算
                total();
            }
        });


    }

    /**
     * 选中group复选框
     *
     * @param grouppostion
     */
    @Override
    public void groupClick(int grouppostion) {
        if (cart != null && cart.data.size() > 0) {
            CartEntity.ShopEntity shopEntity = cart.data.get(grouppostion);
            boolean isChecked = !shopEntity.isGroupChecked();
            shopEntity.setGroupChecked(isChecked);
            for (int i = 0; i < shopEntity.list.size(); i++) {
                CartEntity.ShopEntity.ProductEntity productEntity = shopEntity.list.get(i);
                productEntity.setChildChecked(isChecked);
                shopEntity.list.set(i, productEntity);
            }
            cart.data.set(grouppostion, shopEntity);
            cartAdapter.notifyDataSetChanged();
            //总价计算
            total();

        }


    }

    /**
     * 选中child复选框
     *
     * @param groupPostion
     * @param childPos
     */
    @Override
    public void childClick(int groupPostion, int childPos) {
        CartEntity.ShopEntity.ProductEntity productEntity = cart.data.get(groupPostion).list.get(childPos);
        boolean childChecked = !productEntity.isChildChecked();
        productEntity.setChildChecked(childChecked);
        boolean groupChecked = true;
        for (int i = 0; i < cart.data.get(groupPostion).list.size(); i++) {
            if (!cart.data.get(groupPostion).list.get(childPos).isChildChecked()) {
                groupChecked = false;
            }
        }
        CartEntity.ShopEntity shopEntity = cart.data.get(groupPostion);
        shopEntity.setGroupChecked(groupChecked);
        cart.data.set(groupPostion, shopEntity);
        cartAdapter.notifyDataSetChanged();
        total();
    }

    @Override
    public void totalNumPrice() {
        total();
    }


    private void total() {
        List<Double> prices = new ArrayList<>();
        for (int i = 0; i < cartAdapter.getCartEntity().data.size(); i++) {
            for (int i1 = 0; i1 < cartAdapter.getCartEntity().data.get(i).list.size(); i1++) {
                CartEntity.ShopEntity.ProductEntity productEntity = cartAdapter.getCartEntity().data.get(i).list.get(i1);
                System.out.println("price:" + productEntity.price + " num:" + productEntity.num);
                if (productEntity.isChildChecked()) {
                    prices.add(productEntity.price * productEntity.num);
                }
            }

        }
        priceTv.setText("价格：¥" + add(prices) + "");
    }

    /**
     * 提供精确加法计算的add方法
     *
     * @param values 值几何
     * @return 累加的和
     */
    public static double add(List<Double> values) {
        double sum = 0;
        for (int i = 0; i < values.size(); i++) {
            double value = values.get(i);
            BigDecimal b1 = new BigDecimal(Double.toString(sum));
            BigDecimal b2 = new BigDecimal(Double.toString(value));
            sum = b1.add(b2).doubleValue();
        }

        return sum;
    }
}
