package io.hhplus.arch.infrastructure;

import io.hhplus.arch.domain.enrollment.Enrollment;
import io.hhplus.arch.domain.lecture.Lecture;
import io.hhplus.arch.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserAndCourseLecture(User user, Lecture lecture);

    List<Enrollment> findByUser(User user);

}
