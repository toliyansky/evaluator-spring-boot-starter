package dev.toliyansky.eval.service

import org.slf4j.LoggerFactory

abstract class EvaluationService {
    private final def log = LoggerFactory.getLogger(EvaluationService.class)

    abstract def eval(String code)

    def evaluate(String code) {
        try {
            return eval(code)
        } catch (e) {
            log.error("Error while evaluate code", e)
            def sw = new StringWriter()
            def pw = new PrintWriter(sw)
            e.printStackTrace(pw)
            return """Message: ${e.getMessage()}
LocalizedMessage: ${e.getLocalizedMessage()}
Cause: ${e.getCause()}
StackTrace: 
${sw.toString()}
"""
        }
    }
}