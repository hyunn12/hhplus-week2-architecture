package io.hhplus.arch.application;

import io.hhplus.arch.domain.enrollment.Enrollment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnrollmentInfo {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Main {

        private Long enrollmentId;
        private Long userId;
        private Long courseId;

        public static Main of(Enrollment enrollment) {
            return new Main(enrollment.getEnrollmentId(), enrollment.getUser().getUserId(), enrollment.getCourse().getCourseId());
        }
    }
}
