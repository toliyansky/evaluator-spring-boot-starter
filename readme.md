# Code evaluator for spring applications

[![Maven Central](https://img.shields.io/maven-central/v/dev.toliyansky/evaluator-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22dev.toliyansky%22%20AND%20a:%22evaluator-spring-boot-starter%22)
[![codecov](https://codecov.io/github/AnatoliyKozlov/evaluator-spring-boot-starter/branch/master/graph/badge.svg?token=YNWDP1PBGR)](https://codecov.io/github/AnatoliyKozlov/evaluator-spring-boot-starter)
[![Maintainability](https://api.codeclimate.com/v1/badges/673f4509d34da62878f5/maintainability)](https://codeclimate.com/github/AnatoliyKozlov/evaluator-spring-boot-starter/maintainability)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Description

This spring-boot-starter facilitates the debugging of your deployed web application. It allows you to execute arbitrary code in the context of your application or outside it via the web route.

## Requirements

- Java 11+
- Spring boot __WEB__ application

## Configuration

1) Add dependency in your project. [See versions on Maven Central.](https://search.maven.org/artifact/dev.toliyansky/evaluator-spring-boot-starter)

For maven project: 
```xml
<dependency>
    <groupId>dev.toliyansky</groupId>
    <artifactId>evaluator-spring-boot-starter</artifactId>
    <version>1.2.2</version>
</dependency>
```

For gradle project:
```groovy
implementation 'dev.toliyansky:evaluator-spring-boot-starter:1.2.2'
```

2) Since execution of arbitrary code is a security breach, the bean with route will not be instantiated by default. 
   You must enable the evaluator by adding this property to ```application.properties```:

```properties
evaluator.enabled=true
evaluator.webUiEnabled=true
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

- Execution arbitrary code in runtime. Support languages:
    - groovy
    - java (by compatibility via groovy)
    - shell
    - cmd
    - powershell
- UI with code and response highlight

## WEB UI overview
Dynamic usage example:
![dynamic-method](https://i.ibb.co/5h2DX56/dinamic-method.png)

Simple groovy code example:
![groovy-operation](https://i.ibb.co/nBF5gR3/groovy-operation.png)

You can use Shell, Cmd or PowerShell for execute scripts in system context.
![powershell](https://i.ibb.co/Wx0Psg4/powershell.png)
