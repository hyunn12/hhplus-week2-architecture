package io.hhplus.arch.interfaces.dto;

import io.hhplus.arch.application.EnrollmentCommand;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnrollmentRequest {

    @Getter
    public static class Enroll {

        @NotNull
        private Long userId;

        @NotNull
        private Long courseId;

        public EnrollmentCommand.Enroll toCommand() {
            return EnrollmentCommand.Enroll.builder()
                    .userId(userId)
                    .courseId(courseId)
                    .build();
        }
    }

}
