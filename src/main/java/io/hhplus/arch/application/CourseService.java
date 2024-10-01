package io.hhplus.arch.application;

import io.hhplus.arch.annotation.ApplicationService;
import io.hhplus.arch.domain.course.Course;
import io.hhplus.arch.domain.course.CourseDomainService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationService
public class CourseService {

    private final CourseDomainService courseDomainService;

    public CourseService(CourseDomainService courseDomainService) {
        this.courseDomainService = courseDomainService;
    }

    public List<CourseInfo> getAvailableCourseList(LocalDate date) {
        List<Course> courseList = courseDomainService.getAvailableCourseList(date);

        return courseList.stream()
                .map(CourseInfo::of)
                .collect(Collectors.toList());
    }

}
