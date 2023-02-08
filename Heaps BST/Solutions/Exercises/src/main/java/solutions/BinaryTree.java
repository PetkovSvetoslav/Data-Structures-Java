package solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BinaryTree {
    private int key;
    private BinaryTree left;
    private BinaryTree right;

    public BinaryTree(int key, BinaryTree left, BinaryTree right) {
        this.key = key;
        this.left = left;
        this.right = right;
    }

    public int getKey() {
        return this.key;
    }

    public BinaryTree getLeft() {
        return this.left;
    }

    public BinaryTree getRight() {
        return this.right;
    }

    public Integer findLowestCommonAncestor(int first, int second) {

        List<Integer> firstAncestors = new ArrayList<>();
        List<Integer> secondAncestors = new ArrayList<>();

        Integer ancestor = null;

        if (hasPath(this, first, firstAncestors) &&
                hasPath(this, second, secondAncestors)) {
            int minSize = Math.min(firstAncestors.size(), secondAncestors.size());
            for (int i = 0; i < minSize; i++) {
                if (firstAncestors.get(i).equals(secondAncestors.get(i))) {
                    ancestor = firstAncestors.get(i);
                } else {
                    break;
                }
            }
        }

        return ancestor;
    }

    private boolean hasPath(BinaryTree from, int to, List<Integer> path) {
        if (from == null) {
            return false;
        }

        path.add(from.key);

        if (from.key == to) {
            return true;
        }

        if (hasPath(from.left, to, path) ||
                hasPath(from.right, to, path)) {
            return true;
        }

        path.remove(path.size() - 1);
        return false;
    }

    public List<Integer> topView() {
        Map<Integer, Integer> abscissaValue = new TreeMap<>();

        traverseTree(this, 0, abscissaValue);

        return new ArrayList<>(abscissaValue.values());
    }

    private void traverseTree(BinaryTree tree, int abscissa, Map<Integer, Integer> abscissaValue) {
        if (tree == null) {
            return;
        }
        
        abscissaValue.putIfAbsent(abscissa, tree.getKey());

        traverseTree(tree.getLeft(), abscissa - 1,  abscissaValue);
        traverseTree(tree.getRight(), abscissa + 1,  abscissaValue);
    }
}