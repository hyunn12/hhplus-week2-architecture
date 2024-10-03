package io.hhplus.arch.domain.enrollment;

import io.hhplus.arch.domain.course.Course;
import io.hhplus.arch.domain.lecture.Lecture;
import io.hhplus.arch.domain.user.User;
import io.hhplus.arch.infrastructure.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentDomainService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentDomainService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public void checkUserEnroll(User user, Lecture lecture) {
        boolean isEnrolled = enrollmentRepository.existsByUserAndCourseLecture(user, lecture);
        if (isEnrolled) {
            throw new IllegalStateException("이미 신청한 특강입니다.");
        }
    }

    public Enrollment enroll(User user, Course course) {
        Enrollment enrollment = Enrollment.enrollBuilder()
                .user(user)
                .course(course)
                .build();
        enrollmentRepository.save(enrollment);
        return enrollment;
    }

    public List<Enrollment> getUserEnrollmentList(User user) {
        return enrollmentRepository.findByUser(user);
    }

}
