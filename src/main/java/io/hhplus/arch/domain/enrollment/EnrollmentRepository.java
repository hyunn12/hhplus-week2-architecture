package io.hhplus.arch.domain.enrollment;

import io.hhplus.arch.domain.lecture.Lecture;
import io.hhplus.arch.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserAndCourseLecture(User user, Lecture lecture);

}
