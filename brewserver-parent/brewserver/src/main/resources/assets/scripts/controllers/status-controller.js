function StatusController($scope, $http, $rootScope, $atmosphere) {

	$http({
		method : 'GET',
		headers : {
			'Accept' : 'application/json'
		},
		responseType : 'json',
		url : document.location.toString() + 'control/state/full'
	}).error(function(data, status, headers, config) {
		$rootScope.$broadcast('notification', {
			type : 'error',
			msg : data
		});
	}).success(function(data, status) {
		if (data.currentStep.stepType == 'NOTIFICATION') {
			$rootScope.$broadcast('notification-modal', {
				message : $scope.status.currentStep.message
			});
		}
		$scope.updateStatus(data);
	});

	$atmosphere.init({
		url : document.location.toString() + "control/status/"
	}, function(response) {
		var status = JSON.parse(response.responseBody);
		$scope.updateStatus(status);
	}, function(error) {
		$rootScope.$broadcast('notification', {
			msg : error,
			type : 'error'
		});
	});

	$scope.status = {
		operationMode : 'OFF',
		currentTemp : null,
		currentStep : {},
		totalSteps : 0,
		currentStepNumber : 0,
		timeLeft : -1,
		stepStarted : false,
		name : null,
		processSteps : [],
		tempPoints : []
	};

	$scope.targetTemp = null;

	$scope.tempGraph = {

		options : {
			chart : {
				type : 'line',
				zoomType : 'x',
				spacingRight : 20
			},

			xAxis : {
				type : 'linear',
				maxZoom : 10, // 10 minutes
				min : 0,
				title : {
					text : null
				},
				title : {
					text : 'Minuten',
					y : 1
				}
			},

			yAxis : [ {
				min : 0,
				max : 100,
				title : {
					text : 'Temperature (°C)',
					y : 0,
					x : 0
				},
				plotLines : [ {
					value : 0,
					width : 1,
					color : '#808080'
				} ],
				plotBands : [ {
					from : 78,
					to : 100,
					color : '#C02316',
					innerRadius : '100%',
					outerRadius : '120%'
				}, {
					from : 0,
					to : 78,
					color : '#7cfc00',
					innerRadius : '100%',
					outerRadius : '110%'
				} ],
			} ],

			plotOptions : {
				line : {
					dashStyle : 'Solid',
					marker : {
						enabled : false
					}
				}
			},

			tooltip : {
				valueSuffix : '°C'
			},

			legend : {
				enabled : false
			}
		},

		title : {
			text : 'Temperaturverlauf'
		},

		series : [ {
			name : 'Temperatur',
			pointStart : 1,
			data : []
		} ]
	};

	$scope.tempGauge = {
		options : {
			chart : {
				type : 'gauge',
				plotBorderWidth : 0,
				plotBackgroundColor : '#FFFFFF',
				plotBackgroundImage : null,
				height : 250
			},

			pane : [ {
				startAngle : -135,
				endAngle : 135,
				background : null,
				center : [ '50%', '55%' ],
				size : 100
			} ],

			yAxis : [ {
				min : 0,
				max : 100,
				minorTickPosition : 'outside',
				tickPosition : 'outside',
				labels : {
					rotation : 'auto',
					distance : 10
				},
				plotBands : [ {
					from : 78,
					to : 100,
					color : '#C02316',
					innerRadius : '100%',
					outerRadius : '120%'
				}, {
					from : 0,
					to : 78,
					color : '#7cfc00',
					innerRadius : '100%',
					outerRadius : '110%'
				} ],
				pane : 0,
				title : {
					text : 'Temperatur in Grad Celsius',
					y : 100
				}
			} ],

			plotOptions : {
				gauge : {
					dataLabels : {
						enabled : true
					},
					dial : {
						radius : '100%'
					}
				}
			},

			tooltip : {
				valueSuffix : '°C'
			}
		},

		title : {
			"text" : "Temperatur"
		},

		series : [ {
			name : 'Temperatur',
			data : [ 0 ],
			yAxis : 0
		} ]

	};

	$scope.startMash = function() {
		$http({
			method : 'GET',
			url : document.location.toString() + 'control/private/start'
		}).error(function(data, status, headers, config) {
			$rootScope.$broadcast('notification', {
				type : 'error',
				msg : data
			});
		}).success(function(data, status) {
			$scope.updateStatus(data);
			$rootScope.$broadcast('notification', {
				type : 'success',
				msg : 'Maischvorgang gestartet'
			});
			$scope.tempGraph.series[0].data = [];
		});
	};

	$scope.updateStatus = function(status) {
		var processSteps = $scope.status.processSteps;
		var tempPoints = $scope.status.tempPoints;
		var name = $scope.status.name;
		$scope.status = status;
		if (!status.processSteps || status.processSteps.length === 0) {
			$scope.status.processSteps = processSteps;
			$scope.status.tempPoints = tempPoints;
			$scope.status.name = name;
		}
		$scope.tempGauge.series[0].data[0] = status.currentTemp;
		if (status.timeRunning && status.timeRunning > 0) {
			if (status.tempPoints && status.tempPoints.length > 1) {
				$scope.tempGraph.series[0].data = [];
				for ( var i = 0; i < status.tempPoints.length; i++) {
					var tempPoint = status.tempPoints[i];
					$scope.tempGraph.series[0].data.push([
							tempPoint.time / (1000 * 60), tempPoint.temp ]);
				}
			} else if (status.currentTemp && status.timeRunning) {
				$scope.tempGraph.series[0].data.push([
						(parseInt(status.timeRunning) / 60000),
						status.currentTemp ]);
			}
		}

		$rootScope.$broadcast('status', status);
	}

	$scope.stopMash = function() {
		$http({
			method : 'GET',
			url : document.location.toString() + 'control/private/stop'
		}).error(function(data, status, headers, config) {
			$rootScope.$broadcast('notification', {
				type : 'error',
				msg : data
			});
		}).success(function(data, status) {
			$scope.updateStatus(data);
			$rootScope.$broadcast('notification', {
				type : 'success',
				msg : 'Maischvorgang gestoppt'
			});
		});
	};

	$scope.startCooking = function() {
		$http({
			method : 'GET',
			url : document.location.toString() + 'control/private/cook/start'
		}).error(function(data, status, headers, config) {
			$rootScope.$broadcast('notification', {
				type : 'error',
				msg : data
			});
		}).success(function(data, status) {
			$scope.updateStatus(data);
			$rootScope.$broadcast('notification', {
				type : 'success',
				msg : 'Kochen gestartet'
			});
		});
	};

	$scope.stopCooking = function() {
		$http({
			method : 'GET',
			url : document.location.toString() + 'control/private/cook/stop'
		}).error(function(data, status, headers, config) {
			$rootScope.$broadcast('notification', {
				type : 'error',
				msg : data
			});
		}).success(function(data, status) {
			$scope.updateStatus(data);
			$rootScope.$broadcast('notification', {
				type : 'success',
				msg : 'Kochen gestoppt'
			});
		});
	};

	$scope.setFixedTemperature = function() {
		$http({
			method : 'POST',
			data : {
				targetTemp : $scope.targetTemp
			},
			url : document.location.toString() + 'control/private/temp'
		}).error(function(data, status, headers, config) {
			$rootScope.$broadcast('notification', {
				type : 'error',
				msg : data
			});
		}).success(function(data, status) {
			$scope.updateStatus(status);
			$rootScope.$broadcast('notification', {
				type : 'success',
				msg : 'Temperatur wird auf ' + $scope.targetTemp + ' gehalten'
			});
		});
	}

	$scope.showStartMashing = function() {
		return $scope.status.operationMode == 'OFF';
	}

	$scope.showStopMasing = function() {
		return $scope.status.operationMode == 'MASHING';
	}

}