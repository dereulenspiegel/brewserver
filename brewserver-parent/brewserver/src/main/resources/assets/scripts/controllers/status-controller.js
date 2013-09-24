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
		$scope.updateStatus(data);
		$rootScope.$broadcast('status', data);
	});

	$atmosphere.init({
		url : document.location.toString() + "control/status/"
	}, function(response) {
		var status = JSON.parse(response.responseBody);
		$scope.updateStatus(status);
	}, function(error) {

	});

	$scope.status = {
		mashing : false,
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
				title : {
					text : null
				},
				title : {
					text : 'Sekunden',
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
					dashStyle : 'Solid'
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

	$scope.onStatus = function(response) {
		var status = JSON.parse(response.responseBody);
	};

	$scope.onWSSError = function(error) {

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
			$scope.updateStatus(status);
			$rootScope.$broadcast('notification', {
				type : 'success',
				msg : 'Maischvorgang gestartet'
			});
		});
	};

	$scope.updateStatus = function(status) {
		$scope.$apply(function() {
			var processSteps = $scope.status.processSteps;
			var tempPoints = $scope.status.tempPoints;
			var name = $scope.status.name;
			$scope.status = status;
			if (!status.processSteps) {
				$scope.status.processSteps = processSteps;
				$scope.status.tempPoints = tempPoints;
				$scope.status.name = name;
			}
		});
		$scope.$apply(function() {
			$scope.tempGauge.series[0].data[0] = status.currentTemp;
		});
		if (status.timeRunning > 0) {
			$scope.$apply(function() {
				if (status.currentTemp && status.timeRunning) {
					$scope.tempGraph.series[0].data.push([
							(parseInt(status.timeRunning) / 1000),
							status.currentTemp ]);
				}
			});
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
			$scope.updateStatus(status);
			$rootScope.$broadcast('notification', {
				type : 'success',
				msg : 'Maischvorgang gestoppt'
			});
		});
	};

}