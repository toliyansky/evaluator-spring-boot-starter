package dev.toliyansky.eval

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import static org.assertj.core.api.Assertions.assertThat


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestOnDefaultProperties {

    @LocalServerPort
    private int port

    @Autowired
    private TestRestTemplate restTemplate

    @Test
    void testStatic() {
        ResponseEntity<String> result = this.restTemplate.getForEntity("http://localhost:${port}/eval", String)
        assertThat(result.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    void testGroovyEvalGet() {
        ResponseEntity<String> result = this.restTemplate.getForEntity("http://localhost:${port}/eval/groovy?code=5*5", String)
        assertThat(result.statusCode == HttpStatus.NOT_FOUND)
    }
}
