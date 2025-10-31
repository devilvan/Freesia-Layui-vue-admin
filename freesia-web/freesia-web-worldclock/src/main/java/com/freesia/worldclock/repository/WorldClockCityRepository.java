package com.freesia.worldclock.repository;


import com.freesia.worldclock.po.WorldClockCityPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 城市表 持久层
 * @date 2025-10-31
 */
@Repository
public interface WorldClockCityRepository extends JpaRepository<WorldClockCityPo, Long> {
    void deleteExistingData(Long cityId, int year);
}
