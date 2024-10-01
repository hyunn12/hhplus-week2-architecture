package io.hhplus.arch.domain.course;

import io.hhplus.arch.domain.enrollment.Enrollment;
import io.hhplus.arch.domain.lecture.Lecture;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COURSE")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COURSE_ID")
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LECTURE_ID", nullable = false)
    private Lecture lecture;

    @Column(name = "MAX_COUNT", nullable = false)
    @ColumnDefault("30")
    private int maxCount;

    @Column(name = "AVAILABLE_COUNT", nullable = false)
    @ColumnDefault("30")
    private int availableCount;

    @Column(name = "COURSE_DATE")
    private LocalDateTime courseDate;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollmentList = new ArrayList<>();

    public Course(Long courseId) {
        this.courseId = courseId;
    }

    public Course(Long courseId, Lecture lecture, int maxCount, int availableCount, LocalDateTime courseDate) {
        this.courseId = courseId;
        this.lecture = lecture;
        this.maxCount = maxCount;
        this.availableCount = availableCount;
        this.courseDate = courseDate;
    }

    public void decreaseAvailableCount() {
        if (availableCount > 0) {
            this.availableCount--;
        } else {
            throw new IllegalStateException("신청 마감된 특강입니다.");
        }
    }

}
