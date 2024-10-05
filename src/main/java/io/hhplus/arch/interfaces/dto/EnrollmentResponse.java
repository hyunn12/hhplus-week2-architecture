package io.hhplus.arch.interfaces.dto;

import io.hhplus.arch.application.EnrollmentInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnrollmentResponse {

    @Getter
    @Builder
    public static class Enroll {

        private Long enrollmentId;
        private Long userId;
        private Long courseId;

        public static Enroll of(EnrollmentInfo.Main info) {
            return Enroll.builder()
                    .enrollmentId(info.getEnrollmentId())
                    .userId(info.getUserId())
                    .courseId(info.getCourseId())
                    .build();
        }
    }
}
