package io.hhplus.arch.application;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnrollmentCommand {

    @Builder
    @Getter
    public static class Enroll {
        private Long userId;
        private Long courseId;
    }

}
