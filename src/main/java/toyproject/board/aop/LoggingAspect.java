package toyproject.board.aop;

import com.google.common.base.Joiner;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

// 로그 클래스
@Aspect
@Component
public class LoggingAspect {

    public static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private String paramMapToString(Map<String, String[]> paramMap) {
        return paramMap.entrySet().stream()
                .map(entry -> String.format("%s=%s",
                        entry.getKey(), Joiner.on(",").join(entry.getValue())))
                .collect(Collectors.joining("&"));
    }

    @Pointcut("within(toyproject.board.controller..*)")
    public void onRequest() {
    }

    @Pointcut("within(toyproject.board.service..*)")
    public void afterThrow() {
    }

    @Around("toyproject.board.aop.LoggingAspect.onRequest()")
    public Object doLogging(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Map<String, String[]> paramMap = request.getParameterMap();
        String params = "";
        if (!paramMap.isEmpty()) {
            params = "?" + paramMapToString(paramMap);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            return pjp.proceed(pjp.getArgs());
        } finally {
            stopWatch.stop();
            logger.info("Request: {} {}{} | {} | {}ms", request.getMethod(), request.getRequestURI(),
                    params, request.getRemoteHost(), stopWatch.getTotalTimeMillis());
        }
    }

    @AfterThrowing(pointcut = "toyproject.board.aop.LoggingAspect.afterThrow()", throwing = "exception")
    public void doExceptionLogging(JoinPoint joinPoint, Throwable exception) {
        if (exception.getMessage() == null) {

            StringBuilder builder = new StringBuilder();
            builder.append("Exception: ");

            builder.append(exception.getClass().toString().split("class ")[1]);
            builder.append(" | ");

            builder.append(joinPoint.getSignature().toShortString());

            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    builder.append(" | Args -> ");
                } else {
                    builder.append(", ");
                }

                builder.append(args[i]);
            }

            logger.error(builder.toString());

        }
    }

}
