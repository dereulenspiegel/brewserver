var subscribeStatus = function(socket) {
	var request = new $.atmosphere.AtmosphereRequest();
	request.url = document.location.toString() + "control/status/";
	request.contentType = "application/json";
	request.transport = "websocket";
	request.fallbackTransport = "long-polling";
	request.onMessage = statusReceived;
	request.onError = socketError;

	socket.subscribe(request);
}

var subscribeNotification = function(socket) {
	var request = new $.atmosphere.AtmosphereRequest();
	request.url = document.location.toString() + "control/status/notification";
	request.contentType = "application/json";
	request.transport = "websocket";
	request.fallbackTransport = "long-polling";
	request.onMessage = notificationReceived;
	request.onError = socketError;

	socket.subscribe(request);
}

var notificationReceived = function(message) {
	var notification = JSON.parse(message.responseBody);
	$('#notification').html(notification.message);
}

var statusReceived = function(message) {
	var status = JSON.parse(message.responseBody);
	if (status != null && status.mashing === true) {
		$('#start-mash-button').addClass('hidden');
		$('#stop-mash-button').removeClass('hidden');
		$('progressContainer').removeClass('hidden');
		$('#current-temp').html(status.currentTemp);
		updateStepProgress(status);
		updateTotalProgress(status);
	} else {
		$('#start-mash-button').removeClass('hidden');
		$('#stop-mash-button').addClass('hidden');
		$('progressContainer').addClass('hidden');
	}
}

var submitConfig = function(config) {
	var jsonString = JSON.stringify(config)
	$.ajax({
		type : "POST",
		url : document.location.toString() + "control/private/config/set",
		data : jsonString,
		contentType : 'application/json',
		error : handlePostError
	});
}

var executeGet = function(relativeUrl) {
	$.ajax({
		type : "GET",
		url : document.location.toString() + relativeUrl,
		error : handlePostError
	});
}

var startMashing = function() {
	executeGet("control/private/start");
	$('#start-mash-button').addClass('hidden');
}

var stopMashing = function() {
	executeGet("control/private/stop");
}

var socketError = function(response) {
	$('#errorContainer').removeClass();
	$('#errorContainer').addClass('alert');
	$('#errorContainer').html(response);
}

var handlePostError = function(XMLHttpRequest, textStatus, errorThrown) {
	$('#http-error-dialog').html(XMLHttpRequest.responseText);
	// $('#http-error-dialog').dialog("open");
}

var getTotalProgressInPercent = function(status) {
	var totalSteps = status.totalSteps;
	var currentStepNum = status.currentStepNumber;
	var lastProgress = (totalSteps / (currentStepNum - 1)) * 100;
	var stepProgressPart = 100 / totalSteps;
	var stepProgress = getStepProgressInPercent(status) / 100;
	return lastProgress + (stepProgressPart * stepProgress);
}

var getStepProgressInPercent = function(status) {
	var currentStep = status.currentStep;
	var timeLeft = status.timeLeft;
	var stepTime = currentStep.stepTime;
	return ((stepTime / timeLeft) * 100);
}

var updateTotalProgress = function(status) {
	var progress = getTotalProgressInPercent(status);
	// $("#totalProgress").progressbar({
	// value : progress
	// });
}

var updateStepProgress = function(status) {
	var progress = getStepProgressInPercent(status);
	// $("#stepProgress").progressbar({
	// value : progress
	// });
}