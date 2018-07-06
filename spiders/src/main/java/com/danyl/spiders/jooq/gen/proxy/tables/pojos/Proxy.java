/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.proxy.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Proxy implements Serializable {

    private static final long serialVersionUID = -1157636069;

    private String        ip;
    private Integer       port;
    private Integer       speed;
    private String        type;
    private Boolean       isValid;
    private String        comment;
    private LocalDateTime createTime;

    public Proxy() {}

    public Proxy(Proxy value) {
        this.ip = value.ip;
        this.port = value.port;
        this.speed = value.speed;
        this.type = value.type;
        this.isValid = value.isValid;
        this.comment = value.comment;
        this.createTime = value.createTime;
    }

    public Proxy(
        String        ip,
        Integer       port,
        Integer       speed,
        String        type,
        Boolean       isValid,
        String        comment,
        LocalDateTime createTime
    ) {
        this.ip = ip;
        this.port = port;
        this.speed = speed;
        this.type = type;
        this.isValid = isValid;
        this.comment = comment;
        this.createTime = createTime;
    }

    public String getIp() {
        return this.ip;
    }

    public Proxy setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return this.port;
    }

    public Proxy setPort(Integer port) {
        this.port = port;
        return this;
    }

    public Integer getSpeed() {
        return this.speed;
    }

    public Proxy setSpeed(Integer speed) {
        this.speed = speed;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public Proxy setType(String type) {
        this.type = type;
        return this;
    }

    public Boolean getIsValid() {
        return this.isValid;
    }

    public Proxy setIsValid(Boolean isValid) {
        this.isValid = isValid;
        return this;
    }

    public String getComment() {
        return this.comment;
    }

    public Proxy setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public Proxy setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
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
        final Proxy other = (Proxy) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        }
        else if (!ip.equals(other.ip))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        }
        else if (!port.equals(other.port))
            return false;
        if (speed == null) {
            if (other.speed != null)
                return false;
        }
        else if (!speed.equals(other.speed))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        }
        else if (!type.equals(other.type))
            return false;
        if (isValid == null) {
            if (other.isValid != null)
                return false;
        }
        else if (!isValid.equals(other.isValid))
            return false;
        if (comment == null) {
            if (other.comment != null)
                return false;
        }
        else if (!comment.equals(other.comment))
            return false;
        if (createTime == null) {
            if (other.createTime != null)
                return false;
        }
        else if (!createTime.equals(other.createTime))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.ip == null) ? 0 : this.ip.hashCode());
        result = prime * result + ((this.port == null) ? 0 : this.port.hashCode());
        result = prime * result + ((this.speed == null) ? 0 : this.speed.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.isValid == null) ? 0 : this.isValid.hashCode());
        result = prime * result + ((this.comment == null) ? 0 : this.comment.hashCode());
        result = prime * result + ((this.createTime == null) ? 0 : this.createTime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Proxy (");

        sb.append(ip);
        sb.append(", ").append(port);
        sb.append(", ").append(speed);
        sb.append(", ").append(type);
        sb.append(", ").append(isValid);
        sb.append(", ").append(comment);
        sb.append(", ").append(createTime);

        sb.append(")");
        return sb.toString();
    }
}
