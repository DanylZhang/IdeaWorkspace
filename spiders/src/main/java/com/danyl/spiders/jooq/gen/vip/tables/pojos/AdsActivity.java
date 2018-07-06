/*
 * This file is generated by jOOQ.
*/
package com.danyl.spiders.jooq.gen.vip.tables.pojos;


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
public class AdsActivity implements Serializable {

    private static final long serialVersionUID = 531572713;

    private Integer       id;
    private String        name;
    private String        url;
    private Integer       type;
    private LocalDateTime lastUpdateTime;

    public AdsActivity() {}

    public AdsActivity(AdsActivity value) {
        this.id = value.id;
        this.name = value.name;
        this.url = value.url;
        this.type = value.type;
        this.lastUpdateTime = value.lastUpdateTime;
    }

    public AdsActivity(
        Integer       id,
        String        name,
        String        url,
        Integer       type,
        LocalDateTime lastUpdateTime
    ) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.type = type;
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getId() {
        return this.id;
    }

    public AdsActivity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public AdsActivity setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public AdsActivity setUrl(String url) {
        this.url = url;
        return this;
    }

    public Integer getType() {
        return this.type;
    }

    public AdsActivity setType(Integer type) {
        this.type = type;
        return this;
    }

    public LocalDateTime getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public AdsActivity setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
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
        final AdsActivity other = (AdsActivity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        }
        else if (!url.equals(other.url))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        }
        else if (!type.equals(other.type))
            return false;
        if (lastUpdateTime == null) {
            if (other.lastUpdateTime != null)
                return false;
        }
        else if (!lastUpdateTime.equals(other.lastUpdateTime))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.lastUpdateTime == null) ? 0 : this.lastUpdateTime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AdsActivity (");

        sb.append(id);
        sb.append(", ").append(name);
        sb.append(", ").append(url);
        sb.append(", ").append(type);
        sb.append(", ").append(lastUpdateTime);

        sb.append(")");
        return sb.toString();
    }
}
