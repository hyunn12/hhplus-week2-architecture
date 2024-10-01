package io.hhplus.arch.domain.course;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT course FROM Course course WHERE course.courseId = :courseId")
    Optional<Course> findByIdWithLock(@Param("courseId") Long courseId);

    @Query("SELECT course FROM Course course WHERE course.availableCount > 0 AND course.courseDate BETWEEN :startOfDay AND :endOfDay")
    List<Course> findByCourseDate(@Param("startOfDay")LocalDateTime startOfDay, @Param("endOfDay")LocalDateTime endOfDay);

}
