/**
 * Methods that parse information out of the site.
 * Like parsing request fields and validating user input.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */

function parseRequestFromHash() {
	var hash = window.location.hash.substring(1);
	var request = deparam(hash);
	
	request = validateRequest(request);
	
	return request;
}

function parseRequestFromPanel() {
	var request = {};
	
	// Extract
	var from = $('#from').val();
	var to = $('#to').val();
	var depTimestamp = parseTimestampFromPanel();
	
	var transportationModes = [];
	$('.transportationMode.transportationModeSelected').each(function() {
		parseTransportationModesFromPanel(transportationModes, $(this));
	});
	
	// Build request
	request.from = from;
	request.to = to;
	request.depTime = depTimestamp;
	request.modes = transportationModes;
	
	request = validateRequest(request);
	
	return request;
}

function parseTransportationModesFromPanel(transpModeContainer, element) {
	var transpName = $(element).attr('id');
	var transpId = transModeToId[transpName];
	
	transpModeContainer.push(transpId);
}

function parseTimestampFromPanel() {
	var date = $('#departureDate').datepicker('getDate');
	date = $('#departureTime').timepicker('getTime', date);
	
	return date.getTime();
}

function validateRequest(request) {
	if (!isInteger(request.from)) {
		return null;
	}
	request.from = parseInt(request.from);
	
	if (!isInteger(request.to)) {
		return null;
	}
	request.to = parseInt(request.to);
	
	if (!isInteger(request.depTime)) {
		return null;
	}
	request.depTime = parseInt(request.depTime);
	
	if (request.depTime < 0) {
		return null;
	}
	
	if (typeof request.modes == 'undefined' || request.modes.length == 0) {
		return null;
	}
	
	for (var i = 0; i < request.modes.length; i++) {
		var mode = request.modes[i];
		if (!isInteger(mode)) {
			return null;
		}
		if (!transIdToMode.hasOwnProperty(mode)) {
			return null;
		}
		request.modes[i] = parseInt(mode);
	}
	
	return request;
}