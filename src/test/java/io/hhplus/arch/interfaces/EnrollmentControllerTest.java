package io.hhplus.arch.interfaces;

import io.hhplus.arch.domain.course.Course;
import io.hhplus.arch.domain.lecture.Lecture;
import io.hhplus.arch.domain.user.User;
import io.hhplus.arch.infrastructure.CourseRepository;
import io.hhplus.arch.infrastructure.LectureRepository;
import io.hhplus.arch.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EnrollmentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Course course;

    @BeforeEach
    void setUp() {
        Lecture lecture = new Lecture(1L, "test", "tester");
        course = new Course(1L, lecture, 30, 30, LocalDateTime.of(2024, 10, 1, 10, 0));
        lectureRepository.save(lecture);
        courseRepository.save(course);
    }

    void set40Users() {
        for (int i = 1; i <= 40; i++) {
            User user = new User((long) i);
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("동시에 40개의 요청이 들어올 경우 30개만 신청 성공")
    void testConcurrencyEnrollLimit() throws InterruptedException {
        set40Users();

        int maxCount = 30;
        int totalUserCount = 40;

        ExecutorService executor = Executors.newFixedThreadPool(totalUserCount);
        CountDownLatch latch = new CountDownLatch(totalUserCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        List<Long> enrolledList = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < totalUserCount; i++) {
            final long userId = i+1;

            executor.submit(() -> {
                try {
                    long courseId = course.getCourseId();

                    EnrollmentRequest.Enroll request =
                            EnrollmentRequest.Enroll.builder()
                                    .userId(userId)
                                    .courseId(courseId)
                                    .build();

                    String url = "http://localhost:" + port + "/api/enroll/enroll";

                    ResponseEntity<EnrollmentResponse.Enroll> response =
                            restTemplate.postForEntity(url, request, EnrollmentResponse.Enroll.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        enrolledList.add(userId);
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(maxCount, successCount.get(), "신청 성공 인원이 30명이 아님");
        assertEquals(totalUserCount - maxCount, failCount.get(), "신청 실패 인원이 10명이 아님");
        assertEquals(enrolledList.size(), successCount.get(), "성공 인원 리스트 크기와 인원수가 같지 않음");
        System.out.println("enrolledList = " + enrolledList);

        Course enrolledCourse = courseRepository.findById(course.getCourseId()).orElseThrow();
        assertEquals(0, enrolledCourse.getAvailableCount(), "남은 좌석 수가 0이 아님");
    }

    @Test
    @DisplayName("같은 유저가 같은 특강을 5회 신청 시 1회만 성공")
    void testMultipleEnroll() throws InterruptedException {
        User user = new User(1L);
        userRepository.save(user);

        int totalAttemptCount = 5;

        ExecutorService executor = Executors.newFixedThreadPool(totalAttemptCount);
        CountDownLatch latch = new CountDownLatch(totalAttemptCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < totalAttemptCount; i++) {
            executor.submit(() -> {
                try {
                    long courseId = course.getCourseId();

                    EnrollmentRequest.Enroll request =
                            EnrollmentRequest.Enroll.builder()
                                    .userId(user.getUserId())
                                    .courseId(courseId)
                                    .build();

                    String url = "http://localhost:" + port + "/api/enroll/enroll";

                    ResponseEntity<EnrollmentResponse.Enroll> response =
                            restTemplate.postForEntity(url, request, EnrollmentResponse.Enroll.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(1, successCount.get(), "신청 성공 횟수가 1번이 아님");
        assertEquals(4, failCount.get(), "신청 실패 횟수가 4번이 아님");

        Course enrolledCourse = courseRepository.findById(course.getCourseId()).orElseThrow();
        assertEquals(29, enrolledCourse.getAvailableCount(), "남은 좌석 수가 29가 아님");
    }

}