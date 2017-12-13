package com.ezreal.multiselecttreeview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ezreal.treevieewlib.AndroidTreeView;
import com.ezreal.treevieewlib.NodeIDFormat;
import com.ezreal.treevieewlib.TreeNode;

import java.util.ArrayList;
import java.util.List;


public class SecondActivity extends AppCompatActivity {

    private AndroidTreeView mTreeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button btnConfirm = findViewById(R.id.btn_confirm);
        final TextView result = findViewById(R.id.tv_result);
        FrameLayout layout = findViewById(R.id.layout_tree_view);

        // 通过构造方法实例化
        mTreeView = new AndroidTreeView(this);
        // 设置开启多选，默认为关闭,可选
        mTreeView.setMultiSelEnable(true);
        // 设置当前bean 中 id pid 的类型为 String，必选，默认为 long
        mTreeView.setNodeIdFormat(NodeIDFormat.STRING);

        mTreeView.setTitleTextColor("#8a8a8a");             // 设置显示文本字体颜色，可选
        mTreeView.setTitleTextSize(16);                     // 设置显示文本字体大小，可选
        mTreeView.setOpenIconRes(R.mipmap.icon_node_open);  // 设置节点打开时显示的图标，可选，
        mTreeView.setCloseIconRes(R.mipmap.icon_node_close);// 设置节点关闭时显示的图标，可选
        mTreeView.setLevelPadding(10);                       // 设置层级缩进大小，可选
        mTreeView.setIntervalColor("#aabbaa");              // 设置节点间隔线颜色，可选
        mTreeView.setIntervalHeight(0.8f);                   // 设置节点间隔线高度，可选

        // 绑定数据，必须在最后一步调用，否则上述自定义数据将无效
        mTreeView.bindData(testData());

        // 将 TreeView 添加到 FrameLayout 中显示
        layout.addView(mTreeView);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTreeView != null){
                    // 在多选状态时，通过 getSelected 可以获取当前所选的叶子节点的集合
                    List<TreeNode> selected = mTreeView.getSelected();

                    StringBuilder builder = new StringBuilder();
                    for (TreeNode node : selected){
                        builder.append(node.getTitle());
                        builder.append(",");
                    }
                    result.setText(builder.toString());
                }
            }
        });
    }

    private List<TypeBeanString> testData(){

        // 根据 层级关系，设置好 PID ID 构造测试数据
        // 在工作项目中，一般会通过解析 json/xml 来得到个节点数据以及节点间关系

        List<TypeBeanString> list = new ArrayList<>();
        TypeBeanString bean;

        bean = new TypeBeanString("1","0","广东省");
        list.add(bean);

        bean = new TypeBeanString("2","0","广西区");
        list.add(bean);

        bean = new TypeBeanString("11","1","广州市");
        list.add(bean);

        bean = new TypeBeanString("12","1","佛山市");
        list.add(bean);

        bean = new TypeBeanString("21","2","南宁市");
        list.add(bean);

        bean = new TypeBeanString("22","2","百色市");
        list.add(bean);

        bean = new TypeBeanString("111","11","天河区");
        list.add(bean);

        bean = new TypeBeanString("112","11","花都区");
        list.add(bean);

        bean = new TypeBeanString("113","11","白云区");
        list.add(bean);

        return list;
    }
}
