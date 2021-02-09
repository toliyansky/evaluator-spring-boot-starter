package dev.toliyansky.eval

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestPropertySource

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assume.assumeTrue


@TestPropertySource(properties = ["evaluator.enabled=true", "evaluator.webUiEnabled=true"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestRestAndStatic {

    @LocalServerPort
    private int port

    @Autowired
    private TestRestTemplate restTemplate

    @Test
    void testStatic() {
        def result = this.restTemplate.getForObject("http://localhost:${port}/eval", String)
        assertThat(result).contains("<title>Code Evaluator</title>")
    }

    @Test
    void testGroovyEvalGet() {
        def result = this.restTemplate.getForObject("http://localhost:${port}/eval/groovy?code=5*5", String)
        assertThat(result).contains("25")
    }

    @Test
    void testGroovyEvalPost() {
        ResponseEntity<String> result = this.restTemplate.postForEntity("http://localhost:${port}/eval/groovy", "applicationContext", String)
        assertThat(result.body).contains("webServer")
    }

    @Test
    void testCmd() {
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"))
        def cmd = """dir
mkdir pings
ping 0.0.0.0 -n 4 >> pings/ping.txt
cd pings
type ping.txt
del /q ping.txt"""
        ResponseEntity<String> result = this.restTemplate.postForEntity("http://localhost:${port}/eval/cmd", cmd, String)
        assertThat(result.body).contains("Pinging")
    }

    @Test
    void testPowerShell() {
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"))
        def cmd = """Get-ChildItem
New-Item -ItemType Directory pings
ping 0.0.0.0 -n 4 >> pings/ping.txt
Get-Content pings/ping.txt
Remove-Item -Force -Recurse pings
"""
        ResponseEntity<String> result = this.restTemplate.postForEntity("http://localhost:${port}/eval/powershell", cmd, String)
        assertThat(result.body).contains("Pinging")
    }

    @Test
    void testShell() {
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("linux"))
        def script = """pwd
mkdir pings
ping 0.0.0.0 -c 4 >> pings/ping.txt
cat ./pings/ping.txt
rm -rf pings
"""
        ResponseEntity<String> result = this.restTemplate.postForEntity("http://localhost:${port}/eval/shell", script, String)
        assertThat(result.body.toLowerCase()).contains("ping 0.0.0.0")
    }

}
