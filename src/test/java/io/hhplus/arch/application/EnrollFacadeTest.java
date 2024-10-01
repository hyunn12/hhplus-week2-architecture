package io.hhplus.arch.application;

import io.hhplus.arch.domain.course.Course;
import io.hhplus.arch.domain.course.CourseDomainService;
import io.hhplus.arch.domain.enrollment.Enrollment;
import io.hhplus.arch.domain.enrollment.EnrollmentDomainService;
import io.hhplus.arch.domain.lecture.Lecture;
import io.hhplus.arch.domain.user.User;
import io.hhplus.arch.domain.user.UserDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollFacadeTest {

    @InjectMocks
    private EnrollFacade enrollFacade;

    @Mock
    private EnrollmentDomainService enrollmentDomainService;
    @Mock
    private CourseDomainService courseDomainService;
    @Mock
    private UserDomainService userDomainService;

    @Test
    @DisplayName("최대인원 초과해 신청 시 초과된 유저에 대해 예외 발생")
    void testOverAvailableCount() {
        int maxCount = 30;
        int totalUserCount = 40;

        AtomicInteger enrollmentCount = new AtomicInteger(0);

        AtomicInteger enrollSuccessCount = new AtomicInteger(0);
        AtomicInteger enrollFailCount = new AtomicInteger(0);

        Lecture lecture = new Lecture(1L, "test lecture", "lecturer1");
        Course course = new Course(1L, lecture, maxCount, maxCount, LocalDateTime.now());

        when(userDomainService.getUserById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return new User(userId);
        });

        when(courseDomainService.getCourseWithLock(anyLong())).thenReturn(course);

        doNothing().when(enrollmentDomainService).checkUserEnroll(any(User.class), any(Lecture.class));

        when(enrollmentDomainService.enroll(any(User.class), any(Course.class))).thenAnswer(invocation -> {
            if (enrollmentCount.incrementAndGet() <= maxCount) {
                return new Enrollment((long) enrollmentCount.get(),
                        invocation.getArgument(0),
                        invocation.getArgument(1),
                        LocalDateTime.now()
                );
            } else {
                return null;
            }
        });

        doAnswer(invocation -> {
            Course courseArg = invocation.getArgument(0);
            courseArg.decreaseAvailableCount();
            return null;
        }).when(courseDomainService).decreaseAvailableCount(any(Course.class));

        for (int i = 1; i <= totalUserCount; i++) {
            EnrollmentCommand.Enroll command = EnrollmentCommand.Enroll.builder()
                    .userId((long) i)
                    .courseId(1L)
                    .build();

            try {
                enrollFacade.enroll(command);
                enrollSuccessCount.incrementAndGet();
            } catch (Exception e) {
                enrollFailCount.incrementAndGet();
            }
        }

        assertEquals(maxCount, enrollSuccessCount.get(), "수강 성공 인원과 최대 인원 수 불일치");
        assertEquals(totalUserCount - maxCount, enrollFailCount.get(), "수강 실패 인원과 초과 인원 수 불일치");
        assertEquals(0, course.getAvailableCount(), "잔여좌석은 0이어야함");

        verify(enrollmentDomainService, times(totalUserCount)).checkUserEnroll(any(User.class), any(Lecture.class));
        verify(enrollmentDomainService, times(maxCount)).enroll(any(User.class), any(Course.class));
        verify(courseDomainService, times(maxCount)).decreaseAvailableCount(any(Course.class));
    }

    @Test
    @DisplayName("이미 신청한 강의를 신청할 경우 예외 발생")
    void testAlreadyEnrolled() {
        Long userId = 1L;
        Long courseId = 1L;

        User user = new User(userId);
        Lecture lecture = new Lecture(1L, "test lecture", "lecturer1");
        Course course = new Course(courseId, lecture, 30, 10, LocalDateTime.now());

        when(userDomainService.getUserById(userId)).thenReturn(user);

        when(courseDomainService.getCourseWithLock(courseId)).thenReturn(course);

        doThrow(new IllegalStateException("이미 신청한 특강입니다."))
                .when(enrollmentDomainService).checkUserEnroll(user, lecture);

        EnrollmentCommand.Enroll command = EnrollmentCommand.Enroll.builder()
                .userId(userId)
                .courseId(courseId)
                .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            enrollFacade.enroll(command);
        });

        assertEquals("이미 신청한 특강입니다.", exception.getMessage());

        verify(enrollmentDomainService, times(1)).checkUserEnroll(user, lecture);
        verify(enrollmentDomainService, never()).enroll(any(User.class), any(Course.class));
        verify(courseDomainService, never()).decreaseAvailableCount(any(Course.class));
    }

    @Test
    @DisplayName("유저가 특강에 신청")
    void testEnroll() {
        Long userId = 1L;
        Long courseId = 1L;

        User user = new User(userId);
        Lecture lecture = new Lecture(1L, "test lecture", "lecturer1");
        Course course = new Course(courseId, lecture, 30, 30, LocalDateTime.now());

        when(userDomainService.getUserById(userId)).thenReturn(user);

        when(courseDomainService.getCourseWithLock(courseId)).thenReturn(course);

        doNothing().when(enrollmentDomainService).checkUserEnroll(user, lecture);

        Enrollment enrollment = new Enrollment(1L, user, course, LocalDateTime.now());
        when(enrollmentDomainService.enroll(user, course)).thenReturn(enrollment);

        doAnswer(invocation -> {
            Course courseArg = invocation.getArgument(0);
            courseArg.decreaseAvailableCount();
            return null;
        }).when(courseDomainService).decreaseAvailableCount(any(Course.class));

        EnrollmentInfo.Main result = enrollFacade.enroll(
                EnrollmentCommand.Enroll.builder()
                        .userId(userId)
                        .courseId(courseId)
                        .build()
        );

        assertNotNull(result, "EnrollmentInfo.Main 객체가 null");
        assertEquals(enrollment.getEnrollmentId(), result.getEnrollmentId(), "신청ID 불일치");
        assertEquals(user.getUserId(), result.getUserId(), "회원ID 불일치");
        assertEquals(course.getCourseId(), result.getCourseId(), "특강ID 불일치");

        verify(enrollmentDomainService, times(1)).checkUserEnroll(user, lecture);
        verify(enrollmentDomainService, times(1)).enroll(user, course);
        verify(courseDomainService, times(1)).decreaseAvailableCount(course);

        assertEquals(29, course.getAvailableCount(), "잔여좌석이 29가 아님");
    }

}