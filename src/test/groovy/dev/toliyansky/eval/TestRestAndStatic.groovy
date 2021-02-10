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

    private final def CMD_SCRIPT = """dir
mkdir pings
ping 0.0.0.0 -n 4 >> pings/ping.txt
cd pings
type ping.txt
del /q ping.txt"""

    private final def POWERSHELL_SCRIPT = """Get-ChildItem
New-Item -ItemType Directory pings
ping 0.0.0.0 -n 4 >> pings/ping.txt
Get-Content pings/ping.txt
Remove-Item -Force -Recurse pings
"""

    private final def SHELL_SCRIPT = """pwd
mkdir pings
ping 0.0.0.0 -c 4 >> pings/ping.txt
cat ./pings/ping.txt
rm -rf pings
"""

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
    void testGroovyEvalPostFailed() {
        ResponseEntity<String> result = this.restTemplate.postForEntity("http://localhost:${port}/eval/groovy", "qwerty", String)
        assertThat(result.body).contains("MissingPropertyException")
    }

    @Test
    void testCmdGet() {
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"))
        def result = this.restTemplate.getForObject("http://localhost:${port}/eval/cmd?code=${CMD_SCRIPT}", String)
        assertThat(result).contains("Pinging")
    }

    @Test
    void testCmdPost() {
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"))
        ResponseEntity<String> result = this.restTemplate.postForEntity("http://localhost:${port}/eval/cmd", CMD_SCRIPT, String)
        assertThat(result.body).contains("Pinging")
    }


    @Test
    void testPowerShellGet() {
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"))
        def result = this.restTemplate.getForObject("http://localhost:${port}/eval/powershell?code=${POWERSHELL_SCRIPT}", String)
        assertThat(result).contains("Pinging")
    }

    @Test
    void testPowerShellPost() {
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"))
        ResponseEntity<String> result = this.restTemplate.postForEntity("http://localhost:${port}/eval/powershell", POWERSHELL_SCRIPT, String)
        assertThat(result.body).contains("Pinging")
    }

    @Test
    void testShellGet() {
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("linux"))
        def result = this.restTemplate.getForObject("http://localhost:${port}/eval/shell?code=${SHELL_SCRIPT}", String)
        assertThat(result).contains("ping 0.0.0.0")
    }

    @Test
    void testShellPost() {
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("linux"))
        ResponseEntity<String> result = this.restTemplate.postForEntity("http://localhost:${port}/eval/shell", SHELL_SCRIPT, String)
        assertThat(result.body.toLowerCase()).contains("ping 0.0.0.0")
    }

}
