let GROOVY_EXAMPLE_TEXT = `/*
Examples:
1) Work with spring application context from your application
applicationContext
applicationContext.getBean(SomeBean.class)
applicationContext.getBean(SomeBean.class).someStateProperty
applicationContext.getBean(SomeBean.class).someMethod()

2) Work with static variables or methods from your application
YourSomeClass.someStaticVariable
YourSomeClass.someStaticMethod()

3) Any valid groovy/java code
def a = 3;
def b = 4;
a * b
*/
`

let JAVA_EXAMPLE_TEXT = `/*
Examples:
1) Work with spring application context from your application
applicationContext
applicationContext.getBean(SomeBean.class)
applicationContext.getBean(SomeBean.class).someStateProperty
applicationContext.getBean(SomeBean.class).someMethod()

2) Work with static variables or methods from your application
YourSomeClass.someStaticVariable
YourSomeClass.someStaticMethod()

3) Any valid java code
int a = 3;
double b = 4.5;
return a * b;
*/
`

let SHELL_EXAMPLE_TEXT = `: '
Execute arbitrary shell script in system
Important: 
1) This is NOT interactive terminal. This script will be run as shell script.
2) Every run current directory reset to default.
Examples:
pwd
mkdir pings
ping 0.0.0.0 -c 4 >> pings/ping.txt
cat ./pings/ping.txt
rm -rf pings
'
`

let CMD_EXAMPLE_TEXT = `:: Execute arbitrary cmd script in system (for windows only).
:: Important: 
:: 1) This is NOT interactive terminal. This script will be run as batch script.
:: 2) Every run current directory reset to default.
:: Examples:
:: dir
:: mkdir pings
:: ping 0.0.0.0 -n 4 >> pings/ping.txt
:: cd pings
:: type ping.txt
:: del /q ping.txt
`

let POWERSHELL_EXAMPLE_TEXT = `<#
Execute arbitrary PowerShell script in system (for windows only).
Important: 
1) This is NOT interactive terminal. This script will be run as PowerShell script.
2) Every run current directory reset to default.
Examples:
Get-ChildItem
New-Item -ItemType Directory pings
ping 0.0.0.0 -n 4 >> pings/ping.txt
Get-Content pings/ping.txt
Remove-Item -Force -Recurse pings
#>
`

let START_MESSAGE = `// Write here the code that you want to execute in your application
// Code examples can be found by click on button with symbol (?)
`

const EXAMPLES = [GROOVY_EXAMPLE_TEXT, JAVA_EXAMPLE_TEXT, SHELL_EXAMPLE_TEXT, CMD_EXAMPLE_TEXT, POWERSHELL_EXAMPLE_TEXT, START_MESSAGE];

let editor = ace.edit("editor");
editor.setTheme("ace/theme/monokai");
editor.session.setMode("ace/mode/groovy");
editor.setValue(START_MESSAGE);
editor.moveCursorTo(3, 0);
editor.clearSelection();

let resultViewer = ace.edit("result-viewer");
resultViewer.setTheme("ace/theme/monokai");
resultViewer.session.setMode("ace/mode/text");
resultViewer.setOptions({
    readOnly: true
})

let currentEditorLang = 'groovy';
let loadingSpinner = document.getElementById("loading-spinner");

document.getElementById('eval-button').onclick = function () {
    loadingSpinner.style.display = 'flex';
    fetch(`/eval/${currentEditorLang}`, {
        method: 'POST',
        body: editor.getValue()
    }).then(function (response) {
        loadingSpinner.style.display = 'none';
        return response.text();
    }).then(function (data) {
        checkResponseTypeTextAndSetMode(data);
    }).catch(function () {
        loadingSpinner.style.display = 'none';
        console.log("Error while handle request");
    });
}

function checkResponseTypeTextAndSetMode(data) {
    loadingSpinner.style.display = 'none';
    resultViewer.setValue(data);

    if (checkJSON(data) || checkYAML(data) || checkXML(data)) {
        resultViewer.clearSelection();
    } else {
        resultViewer.session.setMode("ace/mode/text");
    }
}

function checkJSON(data) {
    try {
        let jsonData = JSON.parse(data);
        resultViewer.session.setMode("ace/mode/json");
        let formattedText = JSON.stringify(jsonData, null, '\t');
        resultViewer.setValue(formattedText);
        return true;
    } catch {
        return false;
    }
}

function checkYAML(data) {
    let yamlData = jsyaml.load(data);
    if (typeof yamlData === 'string') {
        return false;
    }
    resultViewer.session.setMode("ace/mode/yaml");
    return true;
}

function checkXML(data) {
    let domParser = new DOMParser();
    let xmlData = domParser.parseFromString(data, 'text/xml');
    if (xmlData.getElementsByTagName('parsererror').length) {
        return false;
    }
    resultViewer.session.setMode("ace/mode/xml");
    return true;
}

document.getElementById('help-button').onclick = function () {
    replaceHelpMessages(true);
}

document.getElementById('lang-select').onchange = function () {
    currentEditorLang = document.getElementById('lang-select').value;
    switch (currentEditorLang) {
        case 'groovy':
            editor.session.setMode("ace/mode/groovy");
            break;
        case 'java':
            editor.session.setMode("ace/mode/java");
            break;
        case 'shell':
            editor.session.setMode("ace/mode/sh");
            break;
        case 'cmd':
            editor.session.setMode("ace/mode/batchfile");
            break;
        case 'powershell':
            editor.session.setMode("ace/mode/powershell");
            break;
        default:
            editor.session.setMode("ace/mode/text");
    }
    replaceHelpMessages(false);
}

function replaceHelpMessages(isNeedAddNewExample) {
    let currentText = editor.getValue();
    EXAMPLES.forEach(function (example, i) {
        currentText = currentText.replaceAll(example, "");
    });
    if (isNeedAddNewExample) {
        switch (currentEditorLang) {
            case 'groovy':
                currentText = GROOVY_EXAMPLE_TEXT + currentText;
                break;
            case 'java':
                currentText = JAVA_EXAMPLE_TEXT + currentText;
                break;
            case 'shell':
                currentText = SHELL_EXAMPLE_TEXT + currentText;
                break;
            case 'cmd':
                currentText = CMD_EXAMPLE_TEXT + currentText;
                break;
            case 'powershell':
                currentText = POWERSHELL_EXAMPLE_TEXT + currentText;
                break;
            default:
                console.log()
        }
    }
    editor.setValue(currentText);
    editor.clearSelection();
    editor.moveCursorTo(0, 0);
}