package com.example.kson.cart.bean;

import java.util.List;

/**
 * Author:kson
 * E-mail:19655910@qq.com
 * Time:2018/06/28
 * Description:
 */
public class CartEntity {

    public String msg;
    public String code;
    public List<ShopEntity> data;

    public class ShopEntity{
        public String sellerName;
        public String sellerid;
        public List<ProductEntity> list;
        private boolean groupChecked;

        public boolean isGroupChecked() {
            return groupChecked;
        }

        public void setGroupChecked(boolean groupChecked) {
            this.groupChecked = groupChecked;
        }

        public class ProductEntity{
            public String title;
            public double price;
            public int num = 1;
            public String images;
            private boolean childChecked;

            public boolean isChildChecked() {
                return childChecked;
            }

            public void setChildChecked(boolean childChecked) {
                this.childChecked = childChecked;
            }
        }
    }

}
