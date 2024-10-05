package io.hhplus.arch.domain.lecture;

import io.hhplus.arch.domain.course.Course;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LECTURE")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LECTURE_ID")
    private Long lectureId;

    @Column(name = "TITLE", length = 100, nullable = false)
    private String title;

    @Column(name = "LECTURER", length = 100, nullable = false)
    private String lecturer;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courseList = new ArrayList<>();

    public Lecture(Long lectureId,String title, String lecturer) {
        this.lectureId = lectureId;
        this.title = title;
        this.lecturer = lecturer;
    }

}
