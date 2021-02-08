package dev.toliyansky.eval.controller

import dev.toliyansky.eval.EvaluatorProperties
import dev.toliyansky.eval.service.CmdService
import dev.toliyansky.eval.service.GroovyService
import dev.toliyansky.eval.service.PowerShellService
import dev.toliyansky.eval.service.ShellService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.annotation.PostConstruct

@RestController
@RequestMapping('/eval')
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = EvaluatorProperties.PREFIX, name = 'enabled', havingValue = 'true')
class EvalController {
    private final def log = LoggerFactory.getLogger(EvalController.class)
    private def htmlPage

    @Value('${evaluator.webUiEnabled:false}')
    boolean webUiEnabled

    @Autowired
    GroovyService groovyService

    @Autowired
    ShellService shellService

    @Autowired
    CmdService cmdService

    @Autowired
    PowerShellService powerShellService

    @PostConstruct
    def init() {
        log.warn("ATTENTION. Code evaluator enabled. Don't forget disable it in production!")
        def resource = new DefaultResourceLoader().getResource("classpath:web-ui/index.html")
        htmlPage = new String(resource.getInputStream().readAllBytes())
    }

    @GetMapping
    def getHtmlPage() {
        return webUiEnabled ? htmlPage : """
For use WEB-UI you must add property 'evaluator.webUiEnabled=true' to application.properties.
For use HTTP route:
GET /eval/groovy?code={your code}
Parameters: {your code} - string with a valid Groovy script, URI encoded.
POST /eval/groovy
Request body: string with a valid Groovy script"""
    }

    @GetMapping('/groovy')
    def evaluateGroovyGet(@RequestParam String code) {
        groovyService.evaluate(code)
    }

    @PostMapping('/groovy')
    def evaluateGroovyPost(@RequestBody String code) {
        groovyService.evaluate(code)
    }

    @GetMapping('/java')
    def evaluateJavaGet(@RequestParam String code) {
        groovyService.evaluate(code)
    }

    @PostMapping('/java')
    def evaluateJavaPost(@RequestBody String code) {
        groovyService.evaluate(code)
    }

    @GetMapping('/shell')
    def evaluateShellGet(@RequestParam String code) {
        shellService.evaluate(code)
    }

    @PostMapping('/shell')
    def evaluateShellPost(@RequestBody String code) {
        shellService.evaluate(code)
    }

    @GetMapping('/cmd')
    def evaluateCmdGet(@RequestParam String code) {
        cmdService.evaluate(code)
    }

    @PostMapping('/cmd')
    def evaluateCmdPost(@RequestBody String code) {
        cmdService.evaluate(code)
    }

    @GetMapping('/powershell')
    def evaluatePowershellGet(@RequestParam String code) {
        powerShellService.evaluate(code)
    }

    @PostMapping('/powershell')
    def evaluatePowershellPost(@RequestBody String code) {
        powerShellService.evaluate(code)
    }
}