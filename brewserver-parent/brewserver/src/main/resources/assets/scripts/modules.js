app.directive('stepEntry', function() {
    return {
        replace: false,
        template: "<div>Text type :{{entry.text}}</div>"
    };    
})