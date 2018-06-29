# Cart
  购物车
![Image text](https://raw.githubusercontent.com/houzhengbang-houzhengbang/Cart/master/images/c3.PNG)


```
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
```
```

public class CartAdapter extends BaseExpandableListAdapter implements View.OnClickListener {

    private Context context;
    private CartEntity cartEntity;
    private onCheckListener onCheckListener;

    public void setOnCheckListener(com.example.kson.cart.i.onCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public CartEntity getCartEntity() {
        return cartEntity;
    }

    public CartAdapter(Context context, CartEntity cartEntity) {
        this.context = context;
        this.cartEntity = cartEntity;
    }

    @Override
    public int getGroupCount() {
        return cartEntity.data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return cartEntity.data.get(groupPosition).list.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return cartEntity.data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return cartEntity.data.get(groupPosition).list.get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView
                == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item_layout, parent, false);
            groupHolder = new GroupHolder();
            convertView.setTag(groupHolder);
            groupHolder.goupchexbox = convertView.findViewById(R.id.group_checkbox);
            groupHolder.nameTV = convertView.findViewById(R.id.group_name);

        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        groupHolder.goupchexbox.setChecked(cartEntity.data.get(groupPosition).isGroupChecked());

        groupHolder.nameTV.setText(cartEntity.data.get(groupPosition).sellerName);

        //设置tag
        groupHolder.goupchexbox.setTag(R.id.group_id_tag, groupPosition);
        groupHolder.goupchexbox.setOnClickListener(this);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder = null;
        if (convertView
                == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.child_item_layout, parent, false);
            childHolder = new ChildHolder();
            convertView.setTag(childHolder);
            childHolder.childchexbox = convertView.findViewById(R.id.child_checkbox);
            childHolder.iconIv = convertView.findViewById(R.id.child_icon);
            childHolder.nameTV = convertView.findViewById(R.id.child_name);
            childHolder.priceTv = convertView.findViewById(R.id.child_price);
            childHolder.addSubView = convertView.findViewById(R.id.addSubview);

        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        final ChildHolder finalChildHolder = childHolder;
        childHolder.addSubView.setOnAddSubClicklistener(new AddSubView.onAddSubClicklistener() {
            int i = 0;

            @Override
            public void add(View v, int num) {
                i = num;
                i++;
                finalChildHolder.addSubView.setText(i);

                cartEntity.data.get(groupPosition).list.get(childPosition).num = i;

                onCheckListener.totalNumPrice();

            }

            @Override
            public void sub(View v, int num) {

                i = num;
                i--;
                if (i < 1) {
                    i = 1;
                    Toast.makeText(v.getContext(), "不能再减了", Toast.LENGTH_SHORT).show();
                }

                finalChildHolder.addSubView.setText(i);
                CartEntity.ShopEntity.ProductEntity productEntity = cartEntity.data.get(groupPosition).list.get(childPosition);
                productEntity.num = i;
                cartEntity.data.get(groupPosition).list.set(childPosition,productEntity);

                onCheckListener.totalNumPrice();


            }
        });
        childHolder.nameTV.setText(cartEntity.data.get(groupPosition).list.get(childPosition).title);

        childHolder.priceTv.setText(cartEntity.data.get(groupPosition).list.get(childPosition).price + "");

        childHolder.childchexbox.setChecked(cartEntity.data.get(groupPosition).list.get(childPosition).isChildChecked());

        childHolder.childchexbox.setTag(R.id.child_id_tag, childPosition);
        childHolder.childchexbox.setTag(R.id.group_id_tag, groupPosition);
        childHolder.childchexbox.setOnClickListener(this);
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_checkbox:
                onCheckListener.groupClick((Integer) v.getTag(R.id.group_id_tag));
                break;
            case R.id.child_checkbox:
                onCheckListener.childClick((Integer) v.getTag(R.id.group_id_tag), (Integer) v.getTag(R.id.child_id_tag));

                break;
        }
    }

    /**
     * 父viewholder
     */
    private static class GroupHolder {
        CheckBox goupchexbox;
        TextView nameTV;


    }

    /**
     * 子viewholder
     */
    private static class ChildHolder {
        CheckBox childchexbox;
        TextView nameTV, priceTv;
        ImageView iconIv;
        AddSubView addSubView;

    }
}

```
