package dev.toliyansky.eval

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Configuration of WEB-UI files supplier 
 */
@Configuration
@EnableWebMvc
@ConditionalOnProperty(prefix = EvaluatorProperties.PREFIX, name = 'webUiEnabled', havingValue = 'true')
class MvcConfig implements WebMvcConfigurer {
    @Override
    void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/eval/**")
                .addResourceLocations("classpath:web-ui/")
    }
}