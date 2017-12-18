package com.ezreal.treevieewlib;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * helper for create tree node by the value list which user input
 * Created by wudeng on 2017/3/30.
 */

class TreeCreateHelper {

    // 获取根据 “父子” 关系排好序的所以节点的集合
    static <T> List<TreeNode> getSortedNodes(List<T> data, NodeIDFormat nodeFormat, int defaultExpandLevel)
            throws IllegalArgumentException, IllegalAccessException {
        List<TreeNode> result = new ArrayList<>();
        List<TreeNode> nodes;
        if (nodeFormat == NodeIDFormat.LONG) {
            nodes = data2NodeWithLong(data);
        } else {
            nodes = data2NodeWithString(data);
        }

        List<TreeNode> rootNodes = getRootNodes(nodes);

        for (TreeNode node : rootNodes) {
            addNode(result, node, defaultExpandLevel, 1);
        }

        return result;
    }

    // 通过 isParentExpand 属性，得到节点展开/关闭状态,从而得到当前节点可见状态
    static List<TreeNode> filterVisibleNode(List<TreeNode> nodes) {
        List<TreeNode> result = new ArrayList<>();
        for (TreeNode node : nodes) {
            if (node.isRoot() || node.isParentExpand()) {
                result.add(node);
            }
        }
        return result;
    }

    // 使用 注解-反射 机制，得到用户 bean 中的 id pid 以及 title 的值
    private static <T> List<TreeNode> data2NodeWithLong(List<T> data)
            throws IllegalAccessException {
        List<TreeNode> nodes = new ArrayList<>();
        TreeNode node;

        for (T t : data) {
            long id = -1;
            long pId = -1;
            String title = "";
            Class<?> clazz = t.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();

            for (Field f : declaredFields) {
                if (f.getAnnotation(TreeNodeId.class) != null) {
                    f.setAccessible(true);
                    id = (long) f.get(t);
                }
                if (f.getAnnotation(TreeNodePid.class) != null) {
                    f.setAccessible(true);
                    pId = (long) f.get(t);
                }
                if (f.getAnnotation(TreeNodeTitle.class) != null) {
                    f.setAccessible(true);
                    title = (String) f.get(t);
                }
                // 如果所有必要属性已经找到，则停止搜索
                if (id != -1 && pId != -1 && !TextUtils.isEmpty(title)) {
                    break;
                }
            }
            if (id == -1 || pId == -1 || TextUtils.isEmpty(title)) {
                continue;
            }
            node = new TreeNode(String.valueOf(id), String.valueOf(pId), title, t);
            nodes.add(node);
        }

        if (!nodes.isEmpty()){
            setInterrelation(nodes);
        }
        return nodes;
    }

    // 使用 注解-反射 机制，得到用户 bean 中的 id pid 以及 title 的值
    private static <T> List<TreeNode> data2NodeWithString(List<T> data)
            throws IllegalAccessException {
        List<TreeNode> nodes = new ArrayList<>();
        TreeNode node;

        for (T t : data) {
            String id = "";
            String pId = "";
            String title = "";
            Class<?> clazz = t.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();

            for (Field f : declaredFields) {
                if (f.getAnnotation(TreeNodeId.class) != null) {
                    f.setAccessible(true);
                    id = (String) f.get(t);
                }
                if (f.getAnnotation(TreeNodePid.class) != null) {
                    f.setAccessible(true);
                    pId = (String) f.get(t);
                }
                if (f.getAnnotation(TreeNodeTitle.class) != null) {
                    f.setAccessible(true);
                    title = (String) f.get(t);
                }
                // 如果所有必要属性已经找到，则停止搜索
                if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(pId) && !TextUtils.isEmpty(title)) {
                    break;
                }
            }
            if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pId) || TextUtils.isEmpty(title)) {
                continue;
            }
            node = new TreeNode(id, pId, title, t);
            nodes.add(node);
        }

        if (!nodes.isEmpty()){
            setInterrelation(nodes);
        }
        return nodes;
    }

    // 设置节点间 父子 关系
    private static void setInterrelation(List<TreeNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            TreeNode n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                TreeNode m = nodes.get(j);
                if (m.getPid().equals(n.getId())) {
                    n.getChildren().add(m);
                    m.setParent(n);
                } else if (m.getId().equals(n.getPid())) {
                    m.getChildren().add(n);
                    n.setParent(m);
                }
            }
        }
    }

    // 获取根节点集
    private static List<TreeNode> getRootNodes(List<TreeNode> nodes) {
        List<TreeNode> root = new ArrayList<>();
        for (TreeNode node : nodes) {
            if (node.isRoot())
                root.add(node);
        }
        return root;
    }

    // 根据节点间的“父子”关系，递归连接节点
    private static void addNode(List<TreeNode> nodes, TreeNode node,
                                int defaultExpandLevel, int currentLevel) {
        node.setLevel(currentLevel);
        if (defaultExpandLevel >= currentLevel) {
            node.setExpand(true);
        }
        nodes.add(node);
        if (node.isLeaf())
            return;
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(nodes, node.getChildren().get(i), defaultExpandLevel,
                    currentLevel + 1);
        }
    }


}
