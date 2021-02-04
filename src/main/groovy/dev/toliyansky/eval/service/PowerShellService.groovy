package dev.toliyansky.eval.service

import dev.toliyansky.eval.EvaluatorProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.stereotype.Service

@Service
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = EvaluatorProperties.PREFIX, name = 'enabled', havingValue = 'true')
class PowerShellService {
    private final def log = LoggerFactory.getLogger(PowerShellService.class)

    def evaluate(String code) {
        log.debug("Evaluator try execute powershell code: {}", code)
        def scriptFile = new File('eval.ps1')
        scriptFile.delete()
        scriptFile.write(code)
        def process = 'powershell.exe -file eval.ps1'.execute()
        process.waitForOrKill(1000)
        process.text
    }
}
