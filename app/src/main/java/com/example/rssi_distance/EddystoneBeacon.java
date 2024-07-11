package com.example.rssi_distance;

public class EddystoneBeacon {

    private String namespace;
    private String instance;



    public EddystoneBeacon(String namespace, String instance) {
        this.namespace = namespace;
        this.instance = instance;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Boolean equals(EddystoneBeacon beacon) {
        return this.namespace.equals(beacon.getNamespace()) && this.instance.equals(beacon.getInstance());
    }

    @Override
    public String toString() {
        return "EddystoneBeacon{'" + namespace + ", " + '\''
                + instance + '\'' + '}';
    }
}
