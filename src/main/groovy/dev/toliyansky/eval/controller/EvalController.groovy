package dev.toliyansky.eval.controller

import dev.toliyansky.eval.EvaluatorProperties
import dev.toliyansky.eval.service.GroovyService
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

    @Value('${evaluator.web.ui.enabled:false}')
    boolean webUiEnabled

    @Autowired
    GroovyService groovyService

    @PostConstruct
    def init() {
        log.warn("ATTENTION. Code evaluator enabled. Don't forget disable it in production!")
        def resource = new DefaultResourceLoader().getResource("classpath:web-ui/index.html")
        htmlPage = new String(resource.getInputStream().readAllBytes())
    }

    @GetMapping
    def getHtmlPage() {
        return webUiEnabled ? htmlPage : """
For use WEB-UI you must add property 'evaluator.web.ui.enabled=true' to application.properties.
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

    @SuppressWarnings('unused')
    @GetMapping('/java')
    def evaluateJavaGet(@RequestParam String code) {
        "java evaluator not implemented yet"
    }

    @SuppressWarnings('unused')
    @PostMapping('/java')
    def evaluateJavaPost(@RequestBody String code) {
        "java evaluator not implemented yet"
    }

    @SuppressWarnings('unused')
    @GetMapping('/kotlin')
    def evaluateKotlinGet(@RequestParam String code) {
        "kotlin evaluator not implemented yet"
    }

    @SuppressWarnings('unused')
    @PostMapping('/kotlin')
    def evaluateKotlinPost(@RequestBody String code) {
        "kotlin evaluator not implemented yet"
    }

    @SuppressWarnings('unused')
    @GetMapping('/shell')
    def evaluateShellGet(@RequestParam String code) {
        "shell evaluator not implemented yet"
    }

    @SuppressWarnings('unused')
    @PostMapping('/shell')
    def evaluateShellPost(@RequestBody String code) {
        "shell evaluator not implemented yet"
    }
}