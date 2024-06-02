package com.example.backend;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.repository.query.Param;
@Repository
public interface UserRepository extends JpaRepository<User_s, Long> {
    List<User_s> findAllByOrderByEpochDataAsc();
Page<User_s> findByDeviceId(String deviceId, Pageable pageable);
 Page<User_s> findByDeviceIdAndEpochDataBetween(String deviceId, Long startEpoch, Long endEpoch, Pageable pageable);
 @Query(value = "SELECT d.device_id, COUNT(d), SUM(d.speed), SUM(d.delta_t), SUM(d.delta_distance), SUM(d.mov_avg_spd), " +
 "MIN(d.human_readable_date) AS start_date, " +
 "MAX(d.human_readable_date) AS end_date, " +
 "FLOOR(EXTRACT(EPOCH FROM age(to_timestamp(MAX(d.human_readable_date), 'YYYY-MM-DD HH24:MI:SS'), to_timestamp(MIN(d.human_readable_date), 'YYYY-MM-DD HH24:MI:SS')))/3600) || ' hours ' || " +
 "ROUND((EXTRACT(EPOCH FROM age(to_timestamp(MAX(d.human_readable_date), 'YYYY-MM-DD HH24:MI:SS'), to_timestamp(MIN(d.human_readable_date), 'YYYY-MM-DD HH24:MI:SS'))) % 3600) / 60) || ' minutes' AS time_difference " +
 "FROM ts d GROUP BY d.device_id", nativeQuery = true)
 List<Object[]> getDeviceIdCountsSumsAndMinMaxHumanReadableDatesAndDiff();
 
 @Query("SELECT u.epochData FROM User_s u")
Set<Long> findAllEpochData();

//  @Query(value = "SELECT SUM(d.delta_distance) FROM ts d WHERE d.device_id = :deviceId AND d.epoch_data BETWEEN :startEpoch AND :endEpoch", nativeQuery = true)
//     Double findTotalDistanceByDeviceIdAndEpochDataBetween(@Param("deviceId") String deviceId, @Param("startEpoch") Long startEpoch, @Param("endEpoch") Long endEpoch);
    // @Query(value = "SELECT SUM(d.delta_distance) FROM ts d WHERE d.device_id = :deviceId AND d.epoch_data BETWEEN :startEpoch AND :endEpoch AND d.speed>10 && d.speed <= 65", nativeQuery = true)
    // Double findTotalDistanceByDeviceIdAndEpochDataBetween(@Param("deviceId") String deviceId, @Param("startEpoch") Long startEpoch, @Param("endEpoch") Long endEpoch);
    @Query(value = "SELECT SUM(d.delta_distance) FROM ts d WHERE d.device_id = :deviceId AND d.epoch_data BETWEEN :startEpoch AND :endEpoch", nativeQuery = true)
Double findTotalDistanceByDeviceIdAndEpochDataBetween(@Param("deviceId") String deviceId, @Param("startEpoch") Long startEpoch, @Param("endEpoch") Long endEpoch);
@Query("SELECT u FROM User_s u WHERE u.deviceId = :deviceId AND u.epochData BETWEEN :startDate AND :endDate")
    List<User_s> findAllByDeviceIdAndEpochDataBetween(String deviceId, long startDate, long endDate);

}
