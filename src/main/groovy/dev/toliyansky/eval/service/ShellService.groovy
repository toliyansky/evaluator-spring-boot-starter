package dev.toliyansky.eval.service

import dev.toliyansky.eval.EvaluatorProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.stereotype.Service

@Service
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = EvaluatorProperties.PREFIX, name = 'enabled', havingValue = 'true')
class ShellService {
    private final def log = LoggerFactory.getLogger(ShellService.class)

    def evaluate(String code) {
        log.debug("Evaluator try execute shell code: {}", code)
        def scriptFile = new File('eval.sh')
        scriptFile.delete()
        scriptFile.write(code)
        scriptFile.setExecutable(true)
        def process = "${scriptFile.absolutePath}".execute()
        process.waitForOrKill(1000)
        process.text
    }
}
