package org.hamic.core;

public class MerkleNode {
    int id;
    String node_color;
    String node_hashString;

    public MerkleNode() {
    }

    public MerkleNode(int id, String node_color, String node_hashString) {
        this.id = id;
        this.node_color = node_color;
        this.node_hashString = node_hashString;
    }


}