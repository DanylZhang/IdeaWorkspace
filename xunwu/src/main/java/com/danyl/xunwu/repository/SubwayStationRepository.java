package com.danyl.xunwu.repository;

import com.danyl.xunwu.entity.SubwayStation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by 瓦力.
 */
public interface SubwayStationRepository extends CrudRepository<SubwayStation, Long> {
    List<SubwayStation> findAllBySubwayId(Long subwayId);
}
