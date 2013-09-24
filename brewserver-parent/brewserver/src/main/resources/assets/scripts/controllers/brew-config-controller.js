function BrewConfigController($scope, $http, $rootScope) {

	$scope.config = {
		processSteps : [],
		name : null,
	};
	$scope.editable = true;

	$scope.stepTypes = [ {
		type : 'MASH',
		name : "Maischschritt"
	}, {
		type : 'NOTIFICATION',
		name : "Benachrichtigung"
	} ];

	$scope.stepType = $scope.stepTypes[0];

	$scope.rawBeerXML = null;

	$scope.addProcessStep = function() {
		var stepTime = ($scope.stepTime * 60 * 1000);
		var step = {
			stepType : $scope.stepType,
			name : $scope.stepName,
			targetTemp : $scope.targetTemp,
			stepTime : stepTime,
			message : $scope.stepMessage
		};
		$scope.config.processSteps.push(step);
		$scope.clearFields();
	};

	$scope.removeStep = function(index) {
		$scope.config.processSteps.splice(index, 1);
	};

	$scope.clearProcessSteps = function() {
		$scope.config.processSteps = [];
		$scope.clearFields();
	};

	$scope.saveConfig = function() {
		$http({
			method : 'POST',
			url : document.location.toString() + 'control/private/config/set',
			data : $scope.config,
			headers : {
				'Content-type' : 'application/json'
			},
			transformRequest : [ function(data, headersGetter) {
				var configString = JSON.stringify(data);
				var config = JSON.parse(configString);
				for ( var i = 0; i < config.processSteps.length; i++) {
					var processStep = config.processSteps[i];
					processStep.stepType = processStep.stepType.type;
				}
				return JSON.stringify(config);
			} ]
		}).error(function(data, status, headers, config) {
			console.log("Can't save mash data: " + status + " " + data);
			$rootScope.$broadcast('notification', {
				type : 'error',
				msg : data
			});
		}).success(function(data, status) {
			$rootScope.$broadcast('notification', {
				type : 'success',
				msg : 'Konfiguration wurde auf dem Server gespeichert'
			});
		});
	};

	$scope.clearFields = function() {
		$scope.stepType = $scope.stepTypes[0];
		$scope.stepName = '';
		$scope.targetTemp = '';
		$scope.stepTime = '';
		$scope.stepMessage = '';
		$scope.config.name = '';
	};

	$scope.readBeerXML = function() {
		var recipes = Brauhaus.Recipe.fromBeerXml($scope.rawBeerXML.contents);
		if (recipes.length > 0) {
			var recipe = recipes[0];
			$scope.config.name = recipe.name;
			if (recipe.steps.length > 0) {
				$scope.config.processSteps = [];
				for ( var i = 0; i < recipe.mash.steps.length; i++) {
					var step = recipe.mash.steps[i];
					$scope.config.processSteps.push({
						targetTemp : step.temp,
						stepType : 'MASH',
						stepTime : step.time * 60 * 1000
					});
				}
			} else {
				$rootScope.$broadcast('notification', {
					type : 'warning',
					msg : 'Keine Prozessschritte im Rezept gefunden'
				});
			}
		} else {
			$rootScope.$broadcast('notification', {
				type : 'warning',
				msg : 'Kein gültigen Rezepte in der Datei gefunden'
			});
		}
	}

	$scope.$on('status', function(event, status) {
		$scope.editable = !status.mashing;
	});

}