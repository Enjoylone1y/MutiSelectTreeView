package com.ezreal.treevieewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * AndroidTreeView
 *
 * 基于 RecyclerView 封装的 无限级树形控件
 * 支持单选和多选，默认单选，即只能选择某一叶子节点
 * 可通过 {@link AndroidTreeView#setMultiSelEnable(boolean)} 设置是否开启多选
 * 在多选状态下，节点显示文字左侧将出现 {@link android.widget.CheckBox} 控件
 *
 * Created by wudeng on 2017/3/30.
 */

public class AndroidTreeView extends RecyclerView {

    // 默认字体大小 px
    private static final float DEFAULT_TEXT_SIZE = 54;
    // 默认字体颜色
    private static final String DEFAULT_TEXT_COLOR = "#686766";
    // 默认层级缩进 px
    private static final float DEFAULT_LEVEL_PADDING = 30;
    // 默认 Node ID 数据类型
    private static final int DEFAULT_NODE_ID_FORMAT = 0;
    // 默认 是否开启多选
    private static final boolean DEFAULT_MULTI_SEL_ENABLE = false;
    // 默认展开层级
    private static final int DEFAULT_EXPAND_LEVEL = 0;
    // 默认分割线高度 px
    private static final float DEFAULT_INTERVAL_HEIGHT = 2.4f;
    // 默认分割线颜色
    private static final String DEFAULT_INTERVAL_COLOR = "#bfbfbf";

    /**
     * 节点文字大小
     */
    private float mTitleTextSize;
    /**
     * 节点文字颜色
     */
    private int mTitleTextColor;
    /**
     * 层级间缩进间距
     */
    private float mLevelPadding;
    /**
     * 节点 ID 格式，支持 long(0) 和 sting(1) 类型
     */
    private NodeIDFormat mNodeIdFormat;
    /**
     * 是否开启多选
     */
    private boolean mMultiSelEnable;
    /**
     * 默认展开层级
     */
    private int mDefaultExpandLevel;
    /**
     * 分割线高度
     */
    private int mIntervalHeight;
    /**
     * 分割线颜色
     */
    private int mIntervalColor;
    /**
     * 节点开启时的显示图标
     */
    private int mOpenIconRes;
    /**
     * 节点关闭时的显示图标
     */
    private int mCloseIconRes;

    private List<TreeNode> mAllNodes;
    private OnTreeNodeClickListener mNodeClickListener;

    public AndroidTreeView(Context context) {
        this(context, null);
    }

    public AndroidTreeView(Context context,AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AndroidTreeView(Context context,AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(getContext()));
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AndroidTreeView);

        // getDimension 得到的是 sp 或 dp 对应的像素值,需要转成 sp 在 setTextSize
        mTitleTextSize= px2dip(typedArray.getDimension(R.styleable.AndroidTreeView_titleTextSize,
                DEFAULT_TEXT_SIZE));
        mLevelPadding = typedArray.getDimension(R.styleable.AndroidTreeView_levelPadding,
                DEFAULT_LEVEL_PADDING);
        mTitleTextColor = typedArray.getColor(R.styleable.AndroidTreeView_titleTextColor,
                Color.parseColor(DEFAULT_TEXT_COLOR));
        int value = typedArray.getInteger(R.styleable.AndroidTreeView_nodeIdFormat,
                DEFAULT_NODE_ID_FORMAT);
        if (value == 0){
            mNodeIdFormat = NodeIDFormat.LONG;
        }else {
            mNodeIdFormat = NodeIDFormat.STRING;
        }
        mMultiSelEnable = typedArray.getBoolean(R.styleable.AndroidTreeView_multiSelEnable,
                DEFAULT_MULTI_SEL_ENABLE);
        mDefaultExpandLevel = typedArray.getInteger(R.styleable.AndroidTreeView_defaultExpandLevel,
                DEFAULT_EXPAND_LEVEL);
        mIntervalHeight = px2dip(typedArray.getDimension(R.styleable.AndroidTreeView_intervalHeight,
                DEFAULT_INTERVAL_HEIGHT));
        mIntervalColor = typedArray.getColor(R.styleable.AndroidTreeView_intervalColor,
                Color.parseColor(DEFAULT_INTERVAL_COLOR));
        mOpenIconRes = typedArray.getResourceId(R.styleable.AndroidTreeView_openIconRes,
                R.mipmap.arrow_up);
        mCloseIconRes = typedArray.getResourceId(R.styleable.AndroidTreeView_closeIcoRes,
                R.mipmap.arrow_down);
        typedArray.recycle();
    }

    /**
     * 绑定 tree view 显示数据
     * 并通过 setAdapter 为 treeView  绑定数据
     * 可以通过 {@link AndroidTreeView#getAdapter()} 方法获得在此实例化的 TreeViewAdapter
     *
     * @param data 需要展示的数据集合
     * @param <T> treeview 数据 bean
     */
    public <T> void bindData(List<T> data) {
        try {
            mAllNodes = TreeCreateHelper.getSortedNodes(data,mNodeIdFormat,mDefaultExpandLevel);
            this.setAdapter(new TreeViewAdapter());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置叶子节点单击事件
     * @param nodeClickListener 节点单击事件监听，只有叶子节点的单击事件才会触发
     */
    public void setTreeNodeClickListener(OnTreeNodeClickListener nodeClickListener) {
        mNodeClickListener = nodeClickListener;
    }


    /**
     * 获取当前已选中节点集合
     * <p>
     * 注意，该方法只适用于开启了多选（通过 {@link AndroidTreeView#setMultiSelEnable(boolean)} 开启）
     * 且已经绑定数据(通过 {@link AndroidTreeView#bindData(List)} 绑定) 的情况下有效
     * <p>
     * 在单选状态下,可以通过 {@link AndroidTreeView#setTreeNodeClickListener(OnTreeNodeClickListener)},
     * 监听节点单击事件回调获得，该事件将会返回 {@link TreeNode} 类型的，被选中的单个叶子节点
     *
     * @return 满足上述条件时：返回已选中节点（叶子节点）集合,否则 返回 NUll
     */

    public List<TreeNode> getSelected() {
        if (!isMultiSelEnable() || mAllNodes == null) {
            return null;
        }
        List<TreeNode> selected = new ArrayList<>();
        for (TreeNode node : mAllNodes) {
            if (node.isChecked() && node.isLeaf()) {
                selected.add(node);
            }
        }
        return selected;
    }

    /**
     * 打开或关闭某节点,仅在已经通过
     *
     * @param position 节点位置
     */
    public void openOrCloseNode(int position) {
        if (getAdapter() != null && getAdapter() instanceof TreeViewAdapter) {
            ((TreeViewAdapter) getAdapter()).expandOrCollapse(position);
        }
    }

    /**
     * 是否开启多选状态
     * @return true 则表示当前支持多选 false 表示当前支持单选
     */
    public boolean isMultiSelEnable() {
        return mMultiSelEnable;
    }

    /**
     * 设置多选开关
     *
     * @param multiSelEnable true 开启多选，在节点文字左侧将出现 check box 控件
     *                       false 关闭多选
     */
    public void setMultiSelEnable(boolean multiSelEnable) {
        mMultiSelEnable = multiSelEnable;
    }

    /**
     * 设置树形节点显示的文本的字体大小
     *
     * @param titleTextSize 文字大小 单位 dp
     */
    public void setTitleTextSize(float titleTextSize) {
        mTitleTextSize = titleTextSize;
    }

    /**
     * 设置树形节点显示的文字的字体颜色
     *
     * @param textColor Sting格式的RGB颜色值，如 "#ababab" ,默认 #686766
     */
    public void setTitleTextColor(String textColor) {
        try {
            mTitleTextColor = Color.parseColor(textColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置层级间的缩进大小
     *
     * @param levelPadding 缩进大小，单位 dp
     */
    public void setLevelPadding(float levelPadding) {
        mLevelPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,levelPadding,
                getResources().getDisplayMetrics());
    }

    /**
     * 设置树形默认展开层级 默认 0，即展示根级
     * @param defaultExpandLevel 展开层级
     */
    public void setDefaultExpandLevel(int defaultExpandLevel) {
        if (defaultExpandLevel < 0){
            return;
        }
        mDefaultExpandLevel = defaultExpandLevel;
    }

    /**
     * 设置节点间分割线高度单位 dp
     * @param intervalHeight 分割线高度
     */
    public void setIntervalHeight(float intervalHeight) {
        mIntervalHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,intervalHeight,
                getResources().getDisplayMetrics());
    }

    /**
     * 设置节点分割线颜色，默认 浅灰色("#bfbfbf")
     * @param intervalColor 颜色值String表示，如 "#ffaabb"
     */
    public void setIntervalColor(String intervalColor) {
        mIntervalColor = Color.parseColor(intervalColor);
    }

    /**
     * 设置非叶子节点展开状态时显示的状态图标,默认为一个向上的 箭头
     * @param openIconRes 图标索引，如 R.mipmap.icon_open
     */
    public void setOpenIconRes(int openIconRes) {
        mOpenIconRes = openIconRes;
    }

    /**
     * 设置非叶子节点关闭状态时显示的状态图标,默认为一个向下的 箭头
     * @param closeIconRes 图标索引，如 R.mipmap.icon_open
     */
    public void setCloseIconRes(int closeIconRes) {
        mCloseIconRes = closeIconRes;
    }

    /**
     * 设置 bean 中 id / pid  值的类型
     *
     * <p>AndroidTreeView 使用 注解/反射 机制获取 id pid 值，以此来构建树形节点依赖关系,
     * 因此，id、pid 这两个属性是必须在bean中声明的，并且需要使用
     * {@link TreeNodeId} {@link TreeNodePid} 对字段进行注解 </p>
     *
     * 考虑到实际项目中，节点 ID 值基本都是使用{@link String} 或者 {@link Long}，所以本项目支持了这两种格式，
     * 而不同类型，解析方式不同，因此需要通过此设置来让节点解析器获知
     *
     * @param nodeIdFormat {@link NodeIDFormat#LONG} 代表你使用的 bean 中 id pid 属性类型为 long
     *                     {@link NodeIDFormat#STRING} 代表你使用的 bean 中 id pid 属性类型为 String
     */
    public void setNodeIdFormat(NodeIDFormat nodeIdFormat) {
        mNodeIdFormat = nodeIdFormat;
    }



    /***********   TreeView  数据适配器  ************/
    private class TreeViewAdapter extends RecyclerView.Adapter<RViewHolder> {

        private LayoutInflater mInflater;
        private List<TreeNode> mViewNodes;

        private TreeViewAdapter() {
            mInflater = LayoutInflater.from(getContext());
            mViewNodes = TreeCreateHelper.filterVisibleNode(mAllNodes);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public RViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            View view = mInflater.inflate(R.layout.item_tree_node, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandOrCollapse(position);
                    if (mNodeClickListener != null && mViewNodes.get(position).isLeaf()) {
                        mNodeClickListener.OnLeafNodeClick(mViewNodes.get(position), position);
                    }
                }
            });
            return new RViewHolder(getContext(), view);
        }

        @Override
        public void onBindViewHolder(RViewHolder holder, int position) {
            final TreeNode node = mViewNodes.get(position);
            final CheckBox checkBox = (CheckBox) holder.getConvertView()
                    .findViewById(R.id.checkbox);
            final ImageView iconLevel = holder.getImageView(R.id.iv_icon_right);
            final MarqueeTextView textView = (MarqueeTextView) holder.getConvertView()
                    .findViewById(R.id.tv_text);
            final View interval = holder.getConvertView().findViewById(R.id.interval);

            node.setViewHolder(holder);

            textView.setTextSize(mTitleTextSize);
            textView.setTextColor(mTitleTextColor);
            textView.setText(node.getTitle());
            textView.setPadding(0, 0, (int) (mLevelPadding * node.getLevel()),
                    0);

            // 若开启多选，则显示 checkBox ，checkBox 状态根据 node 的状态而定
            if (isMultiSelEnable()){
                checkBox.setVisibility(VISIBLE);
                checkBox.setChecked(false);
                if (node.isChecked()) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
            }else {
                checkBox.setVisibility(GONE);
            }

            // checkBox 单击事件事件监听,根据选中与否该表 treeNode 的选中状态
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        node.setChecked(true, true);
                    } else {
                        node.setChecked(false, true);
                    }
                    notifyDataSetChanged();
                }
            });

            // 若为叶子节点，则不显示 “展开/关闭” 状态指示箭头，否则根据节点状态显示向上或向下箭头
            if (node.isLeaf()) {
                iconLevel.setVisibility(View.INVISIBLE);
            } else if (node.isExpand()) {
                iconLevel.setVisibility(View.VISIBLE);
                iconLevel.setImageResource(mOpenIconRes);
            } else {
                iconLevel.setVisibility(View.VISIBLE);
                iconLevel.setImageResource(mCloseIconRes);
            }

            // 间隔线
            ViewGroup.LayoutParams layoutParams = interval.getLayoutParams();
            layoutParams.height = mIntervalHeight;
            interval.setLayoutParams(layoutParams);
            interval.setBackgroundColor(mIntervalColor);
        }

        @Override
        public int getItemCount() {
            return mViewNodes.size();
        }

        private void expandOrCollapse(int position) {
            TreeNode n = mViewNodes.get(position);
            if (n != null) {
                if (!n.isLeaf()) {
                    n.setExpand(!n.isExpand());
                    mViewNodes = TreeCreateHelper.filterVisibleNode(mAllNodes);
                    notifyDataSetChanged();
                }
            }
        }
    }

     private int px2dip(float pxValue){
        return (int) (pxValue / getContext().getResources ().getDisplayMetrics ().
                density + 0.5f);
    }

}
