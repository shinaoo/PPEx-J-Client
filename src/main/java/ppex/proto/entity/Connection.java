package ppex.proto.entity;

import java.net.InetSocketAddress;

public class Connection {


    public String macAddress;                              //使用mac地址来识别每个Connection
    public String peerName;
    public InetSocketAddress address;
    public int natType;

    public Connection(String macAddress,InetSocketAddress address,String peerName,int natType) {
        this.macAddress = macAddress;
        this.address = address;
        this.peerName = peerName;
        this.natType = natType;
    }

    public Connection() {
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Connection that = (Connection) obj;
        return !(macAddress != null ? !macAddress.equals(that.macAddress) : that.macAddress != null);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "inetSocketAddress=" + address.toString() +
                ", ctx=, peerName='" + peerName + '\'' +
                '}';
    }
}
