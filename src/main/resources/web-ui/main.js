let editor = ace.edit("editor");
editor.setTheme("ace/theme/monokai");
editor.session.setMode("ace/mode/groovy");
editor.setValue(`// Write here the groove/java code that you want to execute in your application
// Code examples can be found by click on button with symbol (?)
`);
editor.moveCursorTo(3, 0);
editor.clearSelection();

let resultViewer = ace.edit("result-viewer");
resultViewer.setTheme("ace/theme/monokai");
resultViewer.session.setMode("ace/mode/text");
resultViewer.setOptions({
    readOnly: true
})

document.getElementById('eval-button').onclick = function () {
    fetch('/eval/groovy', {
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
    let currentText = editor.getValue();
    currentText =
        `/*
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
` + currentText;
    editor.setValue(currentText);
    editor.clearSelection();
    editor.moveCursorTo(0, 0);
}