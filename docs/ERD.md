## 특강 신청 서비스 ERD

![ERD.png](ERD.png)

## Table
- `USER` 회원
- `LECTURE` 강의
- `COURSE` 특강
- `ENROLLMENT` 특강 신청

### `USER` 회원
- `USER_ID`: _PK_

### `LECTURE` 강의
- `LECTURE_ID`: _PK_
- `TITLE`: 강의명
- `LECTURER`: 강의자

하나의 강의에 대해 여러번의 특강이 배정될 것을 감안해 강의 테이블과 특강 테이블을 분리함

### `COURSE` 특강
- `COURSE_ID`: _PK_
- `LECTURE_ID`: _LECTURE 테이블 FK_
- `MAX_COUNT`: 최대 인원 수
- `AVAILABLE_COUNT`: 신청 가능 인원 수
- `COURSE_DATE`: 특강일 

동시성 고려했을 때 신청자수를 서브쿼리로 계산하기보다 신청가능 인원 컬럼이 있는게 나을 것 같아 추가


### `ENROLLMENT` 신청
- `ENROLLMENT_ID`: _PK_
- `USER_ID`: _USER 테이블 FK_
- `COURSE_ID`: _COURSE 테이블 FK_
- `ENROLL_DATE`: 신청일


## Relationship 
- `LECTURE` - `COURSE`: **1:N**   
    하나의 강의는 여러개의 특강에 배정될 수 있음
- `USER` - `ENROLLMENT`: **1:N**   
    하나의 사용자는 여러개의 특강을 신청할 수 있음
- `COURSE` - `ENROLLMENT`: **1:N**   
    하나의 특강에 여러명의 사용자가 신청할 수 있음   

