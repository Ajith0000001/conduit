
package com.project.account_service.socket;
public class PacketHeader {


    private int length;
    private int type;

    public PacketHeader(int length, int type) {
        this.length = length;
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public int getType() {
        return type;
    }
}