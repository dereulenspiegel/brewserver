function NotificationController($scope) {
	
	$scope.notifications = [];

	$scope.closeNotification = function(index) {
		$scope.notifications.splice(index, 1);
	};
	
	$scope.$on('notification', function(event,notification){
		$scope.notifications.push(notification);
	});
}