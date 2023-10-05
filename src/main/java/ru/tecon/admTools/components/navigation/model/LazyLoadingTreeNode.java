package ru.tecon.admTools.components.navigation.model;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.TreeNodeChildren;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Maksim Shchelkonogov
 * 12.09.2023
 */
public class LazyLoadingTreeNode<T extends TreeNodeLazy> extends DefaultTreeNode<T> {

    private Function<String, List<T>> loadFunction;
    private boolean lazyLoaded;

    public LazyLoadingTreeNode(T data, Function<String, List<T>> loadFunction) {
        super(data);
        this.loadFunction = loadFunction;
    }

    private LazyLoadingTreeNode(String type, T data, Function<String, List<T>> loadFunction) {
        super(data);
        setType(type);
        this.loadFunction = loadFunction;
    }

    @Override
    public List<TreeNode<T>> getChildren() {
        if (isLeaf()) {
            return Collections.emptyList();
        }

        lazyLoad();

        return super.getChildren();
    }

    @Override
    public int getChildCount() {
        if (isLeaf()) {
            return 0;
        }

        lazyLoad();

        return super.getChildCount();
    }

    @Override
    public boolean isLeaf() {
        return !getData().isDirectory();
    }

    private void lazyLoad() {
        if (!lazyLoaded) {
            lazyLoaded = true;

            String parentId = getData().getItemId();

            List<LazyLoadingTreeNode<T>> childNodes;
            if (getRowKey().equals("root")) {
                childNodes = loadFunction.apply(parentId).stream()
                        .map(f -> {
                            if (f.isDirectory()) {
                                return new LazyLoadingTreeNode<>("firstDirectory", f, loadFunction);
                            } else {
                                return new LazyLoadingTreeNode<>("firstLeaf", f, loadFunction);
                            }
                        }).collect(Collectors.toList());
            } else {
                childNodes = loadFunction.apply(parentId).stream()
                        .map(f -> {
                            if (f.isDirectory()) {
                                return new LazyLoadingTreeNode<>(f, loadFunction);
                            } else {
                                return new LazyLoadingTreeNode<>("leaf", f, loadFunction);
                            }
                        }).collect(Collectors.toList());
            }

            super.getChildren().addAll(childNodes);
        }
    }

    public void reload() {
        super.getChildren().clear();
        lazyLoaded = false;
        lazyLoad();
    }

    @Override
    protected List<TreeNode<T>> initChildren() {
        return new LazyLoadingTreeNodeChildren<>(this);
    }

    public static class LazyLoadingTreeNodeChildren<T extends TreeNodeLazy> extends TreeNodeChildren<T> {

        public LazyLoadingTreeNodeChildren(LazyLoadingTreeNode<T> parent) {
            super(parent);
        }

        @Override
        protected void updateRowKeys(TreeNode<?> node) {
            if (((LazyLoadingTreeNode<?>) node).lazyLoaded) {
                super.updateRowKeys(node);
            }
        }

        @Override
        protected void updateRowKeys(int index, TreeNode<?> node) {
            if (((LazyLoadingTreeNode<?>) node).lazyLoaded) {
                super.updateRowKeys(index, node);
            }
        }

        @Override
        protected void updateRowKeys(TreeNode<?> node, TreeNode<?> childNode, int i) {
            if (((LazyLoadingTreeNode<?>) node).lazyLoaded) {
                super.updateRowKeys(node, childNode, i);
            }
        }
    }

    public void setLoadFunction(Function<String, List<T>> loadFunction) {
        this.loadFunction = loadFunction;
    }
}
