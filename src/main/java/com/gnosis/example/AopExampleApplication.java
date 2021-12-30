package com.gnosis.example;

import lombok.NoArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/exampleEndpoint")
@SpringBootApplication
@Aspect
public class AopExampleApplication {

    @Before("execution(public String com.gnosis.example.AopExampleApplication.ExampleService.giveMeResponse(String))")
    public void preExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Pre execution of " + methodName + " method with message : " + joinPoint.getArgs()[0]);
    }

    @After("execution(public String com.gnosis.example.AopExampleApplication.ExampleService.giveMeResponse(String))")
    public void postExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Post execution of " + methodName + " method with message : " + joinPoint.getArgs()[0]);
    }

    @Around("execution(public String com.gnosis.example.AopExampleApplication.ExampleService.giveMeResponse(String))()")
    public Object doNotTryAtHomeCauseYouMustChoiceBeforeAndAfterOrAround(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        Object retval = pjp.proceed();
        long end = System.nanoTime();
        String methodName = pjp.getSignature().getName();
        System.out.println("Execution of " + methodName + "returns '" + retval + "'" + " and took " + TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");
        return retval;
    }

    @Service
    @NoArgsConstructor
    private static class ExampleService {
        public String giveMeResponse(String input) {
            System.out.println("Preparing response...");
            return "Your Input : " + input;
        }
    }

    private @Autowired
    ExampleService exampleService;

    @GetMapping
    public ResponseEntity<String> createMessage(@RequestParam String input) {
        return ResponseEntity.ok(exampleService.giveMeResponse(input));
    }

    // 4Test : http://localhost:8080/exampleEndpoint?input=hi
    public static void main(String[] args) {
        SpringApplication.run(AopExampleApplication.class, args);
    }
}
