package com.freesia.worldclock.repository;


import com.freesia.worldclock.po.WorldClockCityPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Evad.Wu
 * @Description 城市表 持久层
 * @date 2025-10-31
 */
@Repository
public interface WorldClockCityRepository extends JpaRepository<WorldClockCityPo, Long> {
    @Modifying
    @Query("""
            DELETE FROM WorldClockSunriseSunsetPo WHERE cityId = :cityId AND YEAR(date) = :year
            """)
    @Transactional(rollbackFor = Exception.class)
    void deleteExistingData(@Param(value = "cityId") Long cityId,@Param(value = "year") int year);
}
