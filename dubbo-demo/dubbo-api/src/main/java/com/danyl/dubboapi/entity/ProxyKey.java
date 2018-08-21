package com.danyl.dubboapi.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ProxyKey implements Serializable {

    private static final long serialVersionUID = -1157636129;

    private String ip;
    private Integer port;

    public ProxyKey() {
    }

    public ProxyKey(ProxyKey value) {
        this.ip = value.ip;
        this.port = value.port;
    }

    public ProxyKey(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return this.ip;
    }

    public ProxyKey setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return this.port;
    }

    public ProxyKey setPort(Integer port) {
        this.port = port;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProxyKey other = (ProxyKey) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.ip == null) ? 0 : this.ip.hashCode());
        result = prime * result + ((this.port == null) ? 0 : this.port.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Proxy (");
        sb.append(ip);
        sb.append(", ").append(port);
        sb.append(")");
        return sb.toString();
    }
}
