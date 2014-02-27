var essApp = angular.module('ess');

essApp.directive('timeRecordInput', [function(){
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.on('focus', function(event){
                $(this).attr('type', 'number');
                $(this).parent().parent().addClass("active");
            });
            element.on('blur', function(event){
                $(this).attr('type', 'text');
                $(this).parent().parent().removeClass("active");
            });
        }
    }
}]);