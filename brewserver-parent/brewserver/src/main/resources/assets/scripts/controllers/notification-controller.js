function NotificationController($scope, $atmosphere, $rootScope, $modal) {

	$atmosphere.init({
		url : document.location.toString() + "control/status/notify"
	}, function(response) {
		var notification = JSON.parse(response.responseBody);
		$scope.showModalDialog(notification);
	}, function(error) {
		$rootScope.$broadcast('notification', {
			msg : error.reasonPhrase,
			type : 'error'
		});
	});

	$scope.notifications = [];

	$scope.modalNotification = null;

	$scope.closeNotification = function(index) {
		$scope.notifications.splice(index, 1);
	};

	$scope.$on('notification', function(event, notification) {
		$scope.notifications.push(notification);
	});

	$scope.$on('notification-modal', function(event, modalNotification) {
		$scope.showModalDialog(modalNotification)
	});

	$scope.showModalDialog = function(notification) {
		$scope.modalNotification = notification;

		var modalInstance = $modal.open({
			templateUrl : 'modalNotificationDialog.html',
			controller : ModalInstanceCtrl,
			resolve : {
				notification : function() {
					return $scope.modalNotification;
				}
			}
		});
	};
}

function ModalInstanceCtrl($scope, $modalInstance, $rootScope, $http,
		notification) {

	$scope.notification = notification;

	$scope.ok = function() {
		$http({
			method : 'POST',
			data : $scope.notification,
			url : document.location.toString() + "control/status/notify"
		}).error(function(data, status, headers, config) {
			$rootScope.$broadcast('notification', {
				type : 'error',
				msg : data
			});
			$modalInstance.close(null);
		}).success(function(data, status) {
			$modalInstance.close(null);
		});
	};

};