package io.hhplus.arch.application;

import io.hhplus.arch.domain.enrollment.Enrollment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnrollmentInfo {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Main {

        private Long enrollmentId;
        private Long userId;
        private Long courseId;
        private LocalDateTime courseDate;
        private Long lectureId;
        private String title;
        private String lecturer;

        public static Main of(Enrollment enrollment) {
            return new Main(
                    enrollment.getEnrollmentId(),
                    enrollment.getUser().getUserId(),
                    enrollment.getCourse().getCourseId(),
                    enrollment.getCourse().getCourseDate(),
                    enrollment.getCourse().getLecture().getLectureId(),
                    enrollment.getCourse().getLecture().getTitle(),
                    enrollment.getCourse().getLecture().getLecturer()
            );
        }
    }
}
