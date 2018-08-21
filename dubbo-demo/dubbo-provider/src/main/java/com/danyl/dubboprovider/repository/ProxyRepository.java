package com.danyl.dubboprovider.repository;

import com.danyl.dubboapi.entity.Proxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProxyRepository extends JpaRepository<Proxy, String> {
    List<Proxy> findAllByIpLike(String ip);
}