package io.hhplus.arch.application;

import io.hhplus.arch.domain.course.Course;
import io.hhplus.arch.domain.course.CourseDomainService;
import io.hhplus.arch.domain.lecture.Lecture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseDomainService courseDomainService;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        Lecture lecture1 = new Lecture(1L, "Lecture 1", "Lecturer A");
        Lecture lecture2 = new Lecture(2L, "Lecture 2", "Lecturer B");

        course1 = new Course(1L, lecture1, 30, 10, LocalDateTime.of(2024, 10, 1, 10, 0));
        course2 = new Course(2L, lecture2, 30, 0, LocalDateTime.of(2024, 10, 1, 12, 0));
    }

    @Test
    @DisplayName("특정 날짜에 신청 가능한 특강 목록 정상 조회")
    void testGetAvailableCourseList() {
        LocalDate targetDate = LocalDate.parse("2024-10-01");
        List<Course> mockCourse = Arrays.asList(course1, course2);

        when(courseDomainService.getAvailableCourseList(targetDate)).thenReturn(mockCourse);

        List<CourseInfo> result = courseService.getAvailableCourseList(targetDate);

        assertNotNull(result, "결과가 null");
        assertEquals(2, result.size());

        verify(courseDomainService, times(1)).getAvailableCourseList(targetDate);
    }

}