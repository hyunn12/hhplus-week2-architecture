package io.hhplus.arch.domain.enrollment;

import io.hhplus.arch.domain.course.Course;
import io.hhplus.arch.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ENROLLMENT",
        uniqueConstraints = @UniqueConstraint(columnNames = {"USER_ID", "COURSE_ID"}))
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ENROLLMEMT_ID")
    private Long enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID", nullable = false)
    private Course course;

    @Column(name = "ENROLL_DATE")
    private LocalDateTime enrollDate = LocalDateTime.now();

    @Builder(builderMethodName = "enrollBuilder")
    public Enrollment(User user, Course course) {
        this.user = user;
        this.course = course;
    }

}
