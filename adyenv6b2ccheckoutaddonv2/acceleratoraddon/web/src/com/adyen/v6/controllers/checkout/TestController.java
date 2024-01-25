package com.adyen.v6.controllers.checkout;

import com.adyen.v6.request.TestRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/checkoutApi/")
public class TestController {

    @PostMapping("/postTest")
    public ResponseEntity postTest(final HttpServletRequest request,
                                   final HttpServletResponse response,
                                   @RequestBody TestRequest testRequest) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
