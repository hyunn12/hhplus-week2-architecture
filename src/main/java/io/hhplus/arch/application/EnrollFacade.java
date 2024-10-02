package io.hhplus.arch.application;

import io.hhplus.arch.annotation.ApplicationService;
import io.hhplus.arch.domain.course.Course;
import io.hhplus.arch.domain.course.CourseDomainService;
import io.hhplus.arch.domain.enrollment.Enrollment;
import io.hhplus.arch.domain.enrollment.EnrollmentDomainService;
import io.hhplus.arch.domain.lecture.Lecture;
import io.hhplus.arch.domain.user.User;
import io.hhplus.arch.domain.user.UserDomainService;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationService
public class EnrollFacade {
    private final EnrollmentDomainService enrollmentDomainService;
    private final CourseDomainService courseDomainService;
    private final UserDomainService userDomainService;

    public EnrollFacade(EnrollmentDomainService enrollmentDomainService,
                        CourseDomainService courseDomainService,
                        UserDomainService userDomainService) {
        this.enrollmentDomainService = enrollmentDomainService;
        this.courseDomainService = courseDomainService;
        this.userDomainService = userDomainService;
    }

    @Transactional
    public EnrollmentInfo.Main enroll(EnrollmentCommand.Enroll command) {
        // 회원정보 확인
        User user = userDomainService.getUserById(command.getUserId());

        // 특강정보 조회
        Course course = courseDomainService.getCourseWithLock(command.getCourseId());

        // 기신청강의여부 확인
        Lecture lecture = course.getLecture();
        enrollmentDomainService.checkUserEnroll(user, lecture);

        // 잔여좌석 확인
        if (course.getAvailableCount() <= 0) {
            throw new IllegalStateException("신청 마감된 특강입니다.");
        }

        // 수강신청
        Enrollment enrollment = enrollmentDomainService.enroll(user, course);

        // 잔여좌석 감소
        courseDomainService.decreaseAvailableCount(course);

        return EnrollmentInfo.Main.of(enrollment);
    }

    public List<EnrollmentInfo.Main> getUserEnrollList(Long userId) {
        User user = userDomainService.getUserById(userId);
        List<Enrollment> enrollmentList = enrollmentDomainService.getUserEnrollmentList(user);

        return enrollmentList.stream()
                .map(EnrollmentInfo.Main::of)
                .collect(Collectors.toList());
    }

}
