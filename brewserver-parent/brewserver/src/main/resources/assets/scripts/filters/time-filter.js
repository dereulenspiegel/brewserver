angular.module('brewFilters', []).filter('minutes', function() {
	return function(input) {
		return (input / 1000 / 60);
	}
});