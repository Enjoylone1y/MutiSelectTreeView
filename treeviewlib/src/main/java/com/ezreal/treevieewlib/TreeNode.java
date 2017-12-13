package com.ezreal.treevieewlib;

import java.util.ArrayList;
import java.util.List;

/**
 * TREE VIEW NODE
 * Created by wudeng on 2017/3/29.
 */

public class TreeNode{

    private String id;
    private String pId;
    private String title;
    private int level;
    private boolean isExpand = false;
    private boolean checked = false;
    private List<TreeNode> children = new ArrayList<>();
    private TreeNode parent;
    private Object value;
    private RViewHolder mViewHolder;

    public TreeNode(String id, String pId,String title,Object value) {
        super();
        this.id = id;
        this.pId = pId;
        this.title = title;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pId;
    }

    public void setPid(String pId) {
        this.pId = pId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLevel() { return level; }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean isExpand){
        this.isExpand = isExpand;
        if (!isExpand) {
            for (TreeNode node : children) {
                node.setExpand(false);
            }
        }
    }

    public boolean isRoot(){ return parent == null;}

    public boolean isLeaf(){ return children.size() == 0;}

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public boolean isParentExpand(){
        return parent != null && parent.isExpand();
    }

    public Object getValue() {
        return value;
    }

    public RViewHolder getViewHolder() {
        return mViewHolder;
    }

    public void setViewHolder(RViewHolder viewHolder) {
        mViewHolder = viewHolder;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked,boolean updateChild) {
        this.checked = checked;
        updateParentState();
        if (updateChild){
            updateChildState(checked);
        }
    }

    private void updateChildState(boolean checked){
        // 更新叶节点
        for (TreeNode node : children){
            node.setChecked(checked,true);
        }
    }

    private void updateParentState(){
        if (parent != null){
            boolean allCheck = true;
            for (TreeNode node : parent.children){
                allCheck = allCheck && node.checked;
            }
            if (allCheck){
                parent.setChecked(true,false);
            }else {
                parent.setChecked(false,false);
            }
        }
    }
}
