function NoAtmospherePluginError(message) {
	this.prototype.name = 'NoAtmospherePluginError';
	this.message = (message || 'The Atmosphere plugin for jQuery was not found');
}

NoAtmospherePluginError.prototype = new Error();

angular
		.module('ng.atmosphere', [])
		.factory(
				'$atmosphere',
				function() {

					if (!$.atmosphere) {
						throw new NoAtmospherePluginError();
					}

					var debug = false;
					var listeners = {};
					var listenerIndex = {};

					var connections = {};

					var connection;

					function handleResponse(response) {
						var data = response.responseBody;
						if (typeof data === 'string') {
							data = angular.fromJson(data);
						}
						if (debug) {
							console
									.log(
											'ngAtmosphere DEBUG: received response from server',
											data.type, data);
						}
						if (listeners.hasOwnProperty(data.type)) {
							angular.forEach(listeners[data.type], function(
									listener) {
								listener.fn.call(this, data);
							});
						}
					}

					// Public API here
					return {
						init : function(requestObj, onMessage, onError) {
							if (!connections[requestObj.url]) {
								var request = requestObj;
								request.contentType = 'application/json';
								request.transport = 'websocket';
								request.fallbackTransport = 'long-polling';
								request.onMessage = onMessage;
								request.onError = onError;
								request.logLevel = 'debug';

								var connection = $.atmosphere
										.subscribe(request);
								connections[request.url] = connection;
								if (debug) {
									console
											.log('ngAtmosphere DEBUG: connection made to: '
													+ connection.getUrl());
								}
							}
						},
						on : function(type, callbackFn) {

							var id = Math.random();

							if (!listeners.hasOwnProperty(type)) {
								listeners[type] = [];
							}
							listenerIndex[id] = type;
							listeners[type].push({
								id : id,
								fn : callbackFn
							});

							if (debug) {
								console
										.log('ngAtmosphere DEBUG: added callback to '
												+ type
												+ ' and given the id of ' + id);
							}

							return id;
						},
						off : function(id) {
							var type = listenerIndex[id];
							var typeListeners = listeners[type];
							var removed = false;

							for ( var i = 0; i < typeListeners.length; i++) {
								if (typeListeners[i].id === id) {
									typeListeners.splice(i, 1);
									delete listenerIndex[id];

									removed = true;
									break;
								}
							}
							if (debug) {
								console
										.log('ngAtmosphere DEBUG: removed callback from '
												+ type
												+ ' with the id of: '
												+ id);
							}

							return removed;
						},
						emit : function(type, data) {
							if (debug) {
								console.log(
										'ngAtmosphere DEBUG: sending data with type: '
												+ type, connection, data);
							}
							connection.push(angular.toJson({
								type : type,
								data : data
							}));
						},
						debug : function(enable) {
							debug = enable;
						}
					};
				});