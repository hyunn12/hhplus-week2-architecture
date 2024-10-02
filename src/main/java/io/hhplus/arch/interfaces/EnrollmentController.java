package io.hhplus.arch.interfaces;

import io.hhplus.arch.application.EnrollFacade;
import io.hhplus.arch.application.EnrollmentInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<EnrollmentInfo.Main>> list(@PathVariable Long userId) {
        List<EnrollmentInfo.Main> infoList = enrollFacade.getUserEnrollList(userId);
        return new ResponseEntity<>(infoList, HttpStatus.OK);
    }

}
