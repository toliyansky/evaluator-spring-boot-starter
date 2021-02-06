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

let SHELL_EXAMPLE_TEXT = `# Examples:
# pwd
# ls -la
# curl other.host.domain:port/check-integration
`

let CMD_EXAMPLE_TEXT = `:: Examples:
:: dir
:: mkdir
`

let POWERSHELL_EXAMPLE_TEXT = `# Examples:
# Get-ChildItem
# Get-Process | Where-Object {$_.ProcessName -eq "java"}
`

let START_MESSAGE = `// Write here the code that you want to execute in your application
// Code examples can be found by click on button with symbol (?)
`

const EXAMPLES = [GROOVY_EXAMPLE_TEXT, SHELL_EXAMPLE_TEXT, CMD_EXAMPLE_TEXT, POWERSHELL_EXAMPLE_TEXT, START_MESSAGE];

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

document.getElementById('eval-button').onclick = function () {
    fetch(`/eval/${currentEditorLang}`, {
        method: 'POST',
        body: editor.getValue()
    }).then(function (response) {
        return response.text();
    }).then(function (data) {
        resultViewer.setValue(data);
        try {
            let jsonData = JSON.parse(data);
            resultViewer.session.setMode("ace/mode/json");
            let formattedText = JSON.stringify(jsonData, null, '\t');
            resultViewer.setValue(formattedText);
        } catch (e) {
            resultViewer.session.setMode("ace/mode/text");
        } finally {
            resultViewer.clearSelection();
        }
    }).catch(function () {
        console.log("Error while handle request");
    });
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