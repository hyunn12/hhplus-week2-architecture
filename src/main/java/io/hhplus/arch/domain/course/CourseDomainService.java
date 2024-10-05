package io.hhplus.arch.domain.course;

import io.hhplus.arch.infrastructure.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseDomainService {
    private final CourseRepository courseRepository;

    public CourseDomainService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Course getCourseWithLock(Long courseId) {
        return courseRepository.findByIdWithLock(courseId)
                .orElseThrow(() -> new EntityNotFoundException("특강 정보를 찾을 수 없습니다."));
    }

    public void decreaseAvailableCount(Course course) {
        course.decreaseAvailableCount();
        courseRepository.save(course);
    }

    public List<Course> getAvailableCourseList(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return courseRepository.findByCourseDate(startOfDay, endOfDay);
    }

}
