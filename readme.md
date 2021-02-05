# Code evaluator for spring applications

## Description

This spring-boot-starter facilitates the debugging of your deployed web application. It allows you to execute arbitrary code in the context of your application or outside it via the web route.

## Requirements

- Java 11+
- Spring __WEB__ application

## Configuration

1) Add dependency in your project

```xml
<dependency>
    <groupId>dev.toliyansky</groupId>
    <artifactId>evaluator-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```

2) Since execution of arbitrary code is a security breach, the bean with route will not be instantiated by default. 
   You must enable the evaluator by adding this property to ```application.properties```:

```properties
evaluator.enabled=true
evaluator.web.ui.enabled=true
```

## Usage

1) WEB UI: Use ```http://host:port/eval``` to access the web interface of evaluator with syntax highlight

2) API: GET ```http://host:port/eval/{language}?code={your code}```<br>
Parameters:<br>
_your code_ - string with a valid script, URI encoded<br>
_language_ - your script language [groovy, shell, cmd, powershell] 

3) API: POST ```http://host:port/eval/{language}```<br>
_language_ - your script language [groovy, shell, cmd, powershell]<br>
_request body_ - string with a valid script

**WEB UI is a preferable way of a code execution.** Use API requests when the route is not accessible outside of execution environment (ex. kubernetes cluster, you can connect to POD terminal and use curl/wget for execute code you need to run).  

## Features

- Execution new code in runtime. Support languages:
    - groovy
    - java (by compatibility via groovy)
    - shell
    - cmd
    - powershell
- UI with code and response highlight

In next releases:

- Kotlin
- Autoformatting of yaml and xml responses in WEB UI
- Autoformatting/autocompletion of code in WEB UI

## WEB UI overview

![WEB-UI-IMAGE](https://i.ibb.co/gj5GtJH/Evaluator-spring-boot-starter.png)
