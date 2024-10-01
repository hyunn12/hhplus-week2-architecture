package io.hhplus.arch.interfaces;

import io.hhplus.arch.application.EnrollFacade;
import io.hhplus.arch.application.EnrollmentInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/enroll")
public class EnrollmentController {
    private final EnrollFacade enrollFacade;

    public EnrollmentController(EnrollFacade enrollFacade) {
        this.enrollFacade = enrollFacade;
    }

    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResponse.Enroll> enroll(@RequestBody EnrollmentRequest.Enroll request) {
        EnrollmentInfo.Main info = enrollFacade.enroll(request.toCommand());
        return new ResponseEntity<>(EnrollmentResponse.Enroll.of(info), HttpStatus.OK);
    }

    @GetMapping("/list}")
    public void list(@PathVariable Long userId) {
        // todo 특강 신청 완료 목록 조회 구현
    }

}
