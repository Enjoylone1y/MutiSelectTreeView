package com.ezreal.multiselecttreeview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ezreal.treevieewlib.AndroidTreeView;
import com.ezreal.treevieewlib.NodeIDFormat;
import com.ezreal.treevieewlib.OnTreeNodeClickListener;
import com.ezreal.treevieewlib.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class SingleSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_sel);
        final TextView result = findViewById(R.id.tv_result);

        // 通过 findViewById获得控件实例
        AndroidTreeView treeView = findViewById(R.id.tree_view);
        treeView.setNodeIdFormat(NodeIDFormat.LONG); // 声明 bean 中id pid 字段类型，必选
        treeView.setMultiSelEnable(false);   // 设置关闭多选，默认关闭，可选

        // 在单选状态下，通过监听叶子节点单击事件，得到选中的节点
        treeView.setTreeNodeClickListener(new OnTreeNodeClickListener() {
            @Override
            public void OnLeafNodeClick(TreeNode node, int position) {
                result.setText(node.getTitle());
            }
        });

        // 绑定数据，注意：本行需要写在为 treeView 设置属性之后
        // 在本行之后任何 setXXX 都不起作用
        treeView.bindData(testData());
    }


    private List<TypeBeanLong> testData(){

        // 根据 层级关系，设置好 PID ID 构造测试数据
        // 在工作项目中，一般会通过解析 json/xml 来得到个节点数据以及节点间关系

        List<TypeBeanLong> list = new ArrayList<>();
        list.add(new TypeBeanLong(1,0,"图书"));
        list.add(new TypeBeanLong(2,0,"服装"));

        list.add(new TypeBeanLong(11,1,"小说"));
        list.add(new TypeBeanLong(12,1,"杂志"));

        list.add(new TypeBeanLong(21,2,"衣服"));
        list.add(new TypeBeanLong(22,2,"裤子"));

        list.add(new TypeBeanLong(111,11,"言情小说"));
        list.add(new TypeBeanLong(112,11,"科幻小说"));

        list.add(new TypeBeanLong(121,12,"军事杂志"));
        list.add(new TypeBeanLong(122,12,"娱乐杂志"));

        list.add(new TypeBeanLong(211,21,"阿迪"));
        list.add(new TypeBeanLong(212,21,"耐克"));

        list.add(new TypeBeanLong(221,22,"百斯盾"));
        list.add(new TypeBeanLong(222,22,"海澜之家"));
        return list;
    }
}
