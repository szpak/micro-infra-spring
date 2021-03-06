package com.ofg.infrastructure.scheduling;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.*;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Aspect that sets correlationId for executions of methods annotated with {@link Scheduled} annotation.
 */
@Aspect
public class ScheduledTaskWithCorrelationIdAspect {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskWithCorrelationIdAspect.class);

    private final IdGenerator uuidGenerator;
    private final Trace trace;

    public ScheduledTaskWithCorrelationIdAspect(IdGenerator uuidGenerator, Trace trace) {
        this.uuidGenerator = uuidGenerator;
        this.trace = trace;
    }

    @Around("execution (@org.springframework.scheduling.annotation.Scheduled  * *.*(..))")
    public Object setNewCorrelationIdOnThread(final ProceedingJoinPoint pjp) throws Throwable {
        final Span span = TraceContextHolder.isTracing() ? TraceContextHolder.getCurrentSpan() :
                MilliSpan.builder().begin(System.currentTimeMillis())
                .traceId(uuidGenerator.create()).spanId(uuidGenerator.create()).build();
        try (TraceScope traceScope = trace.startSpan(Thread.currentThread().getName(), span)) {
            return pjp.proceed();
        } catch (Throwable throwable) {
            log.error("Failed to proceed with the next advice or target method invocation", throwable);
            throw throwable;
        }
    }
}
