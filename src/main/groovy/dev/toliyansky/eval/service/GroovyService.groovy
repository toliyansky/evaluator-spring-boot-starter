package dev.toliyansky.eval.service

import dev.toliyansky.eval.EvaluatorProperties
import groovy.json.JsonOutput
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

import java.util.function.Supplier

@Service
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = EvaluatorProperties.PREFIX, name = 'enabled', havingValue = 'true')
class GroovyService {
    private static final def groovyClassLoader = new GroovyClassLoader()
    private static final def EXPRESSION_CLASS_TEMPLATE = """
            package dev.toliyansky.eval.service
            class ExpressionClass implements java.util.function.Supplier<Object> {
                def get() {
                    %s
                }
            }
            """
    private final def log = LoggerFactory.getLogger(GroovyService.class)

    @Autowired
    public ApplicationContext applicationContext

    def evaluate(String code) {
        log.debug("Evaluator try execute groovy code: {}", code)
        def finalClassCode = String.format(EXPRESSION_CLASS_TEMPLATE, code)
        def supplier = groovyClassLoader.parseClass(finalClassCode).getDeclaredConstructor().newInstance() as Supplier<Object>
        supplier.metaClass."applicationContext" = applicationContext
        def result = supplier.get()
        log.debug("Result of executing code: {}", result)
        try {
            if (result instanceof String || result instanceof Integer || result instanceof Long || result instanceof Boolean) {
                return result
            } else {
                return JsonOutput.toJson(result)
            }
        } catch (StackOverflowError ignored) {
            log.debug("StackOverflowError while try serialize result. Return one level depth JSON response.")
            def oneLevelDepthResult = new Object()
            for (def property : result.getProperties()) {
                oneLevelDepthResult.metaClass."$property.key" = property.value.toString()
            }
            return JsonOutput.toJson(oneLevelDepthResult)
        } catch (e) {
            log.debug("Can't serialize result. Return .toString() in response", e)
            return result.toString()
        }
    }
}
