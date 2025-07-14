package com.aurfebre.household.repository;

import com.aurfebre.household.domain.IncomeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeProjectionRepository extends JpaRepository<IncomeProjection, Long> {

    List<IncomeProjection> findByUserIdAndYear(Long userId, Integer year);

    List<IncomeProjection> findByUserIdAndYearAndIsActive(Long userId, Integer year, Boolean isActive);

    List<IncomeProjection> findByUserIdAndIsActiveOrderByYearDescCreatedAtDesc(Long userId, Boolean isActive);

    Optional<IncomeProjection> findByUserIdAndYearAndIncomeType(Long userId, Integer year, IncomeProjection.IncomeType incomeType);

    @Query("SELECT SUM(ip.projectedAnnualIncome) FROM IncomeProjection ip " +
           "WHERE ip.userId = :userId AND ip.year = :year AND ip.isActive = true")
    Optional<Double> sumProjectedIncomeByUserIdAndYear(@Param("userId") Long userId, @Param("year") Integer year);

    @Query("SELECT ip FROM IncomeProjection ip " +
           "WHERE ip.userId = :userId AND ip.year >= :startYear AND ip.year <= :endYear AND ip.isActive = true " +
           "ORDER BY ip.year, ip.incomeType")
    List<IncomeProjection> findByUserIdAndYearRange(@Param("userId") Long userId, 
                                                     @Param("startYear") Integer startYear, 
                                                     @Param("endYear") Integer endYear);
}