package com.freesia.worldclock.repository;


import com.freesia.worldclock.po.WorldClockSunriseSunsetPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 日出日落时间表 持久层
 * @date 2025-10-31
 */
@Repository
public interface WorldClockSunriseSunsetRepository extends JpaRepository<WorldClockSunriseSunsetPo, Long> {
}
