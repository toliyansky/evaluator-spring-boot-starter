package dev.toliyansky.eval.service

import dev.toliyansky.eval.EvaluatorProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.stereotype.Service

@Service
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = EvaluatorProperties.PREFIX, name = 'enabled', havingValue = 'true')
class CmdService extends EvaluationService {
    private final def log = LoggerFactory.getLogger(CmdService.class)

    @Autowired
    EvaluatorProperties evaluatorProperties

    @Override
    def eval(String code) {
        log.debug("Evaluator try execute cmd code: {}", code)
        def scriptFile = new File('eval.bat')
        scriptFile.delete()
        scriptFile.write(code)
        def process = "${scriptFile.absolutePath}".execute()
        process.waitForOrKill(evaluatorProperties.executionTimeoutInMilliseconds)
        def result = process.text
        scriptFile.delete()
        return  result
    }
}
