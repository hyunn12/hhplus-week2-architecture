package io.hhplus.arch.application;

import io.hhplus.arch.domain.course.Course;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CourseInfo {

    private Long courseId;
    private Long lectureId;
    private int maxCount;
    private int availableCount;
    private LocalDateTime courseDate;
    private String title;
    private String lecturer;

    public static CourseInfo of(Course course) {
        return new CourseInfo(
                course.getCourseId(),
                course.getLecture().getLectureId(),
                course.getMaxCount(),
                course.getAvailableCount(),
                course.getCourseDate(),
                course.getLecture().getTitle(),
                course.getLecture().getLecturer()
        );
    }

}
