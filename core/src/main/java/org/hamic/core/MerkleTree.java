package org.hamic.core;

public class MerkleTree {

    public static int[] getRoadToLeaf(int id) {
        int h = (int) (Math.log(id + 1) / Math.log(2));
        int[] res_ids = new int[h];
        res_ids[h - 1] = id;
        for (int i = h - 2; i >= 0; i--) {
            if (id % 2 != 0) id = (id - 1) / 2;
            else id = (id - 2) / 2;
            res_ids[i] = id;
        }
        return res_ids;
    }

    public static int[] getMerkleProof(int id) {
        int[] roadToLeaf_ids = MerkleTree.getRoadToLeaf(id);
        int[] res_ids = new int[roadToLeaf_ids.length];
        int q = res_ids.length - 1;
        for (int i = 0; i < res_ids.length; i++) {
            if (roadToLeaf_ids[i] % 2 != 0) res_ids[q] = roadToLeaf_ids[i] + 1;
            else res_ids[q] = roadToLeaf_ids[i] - 1;
            q--;
        }
        return res_ids;
    }


}