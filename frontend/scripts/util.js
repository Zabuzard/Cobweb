/**
 * Utility methods
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */

function isInteger(value) {
	if (Number.isInteger(value)) {
		return true;
	}
    var n = Math.floor(Number(value));
    return n !== Infinity && String(n) === value;
}

function hasHash() {
	return window.location.hash.length > 1;
}

function setUrlToRequest(request) {
	var hash = window.location.hash.substring(1);
	var currentParams = deparam(hash);
	
	for (var key in request) {
		if (request.hasOwnProperty(key)) {
			var value = request[key]
			currentParams[key] = value;
		}
	}
	
	window.location.hash = $.param(currentParams);
}

function setUiToRequest(request) {
	$('#from').val(request.from);
	$('#to').val(request.to);
	
	var date = new Date(request.depTime);
	$('#departureDate').datepicker("setDate", date);
	$('#departureTime').timepicker('setTime', date);
	
	// Deselect all transportation modes
	$('.transportationMode.transportationModeSelected').each(function() {
		$(this).removeClass('transportationModeSelected');
	});
	
	// Select according to request
	for (var i = 0; i < request.modes.length; i++) {
		var transId = request.modes[i];
		transMode = transIdToMode[transId];
		$('#' + transMode).addClass('transportationModeSelected');
	}
}

function setInfoMessage(message) {
	clearMessages();
    $('#message').text(message);
	$('#message').addClass('infoMessage');
}

function setErrorMessage(message) {
	clearMessages();
	$('#message').text(message);
	$('#message').addClass('errorMessage');
}

function clearMessages() {
	$('#message').removeClass('infoMessage errorMessage');
	$('#message').text('');
}

function placedMapRouteMarker(marker) {
	mapRouteMarkerGroup.push(marker);
}

function clearMapRouteMarker() {
	for (var i = 0; i < this.mapRouteMarkerGroup.length; i++) {
		map.removeLayer(mapRouteMarkerGroup[i]);
	}
}

function placeDepartureNode(departureNode, depTime) {
	var departureNodeMarker = L.marker(departureNode.geom[0]).addTo(map);
	placedMapRouteMarker(departureNodeMarker);
	
	departureNodeMarker.bindPopup('Departure at: ' + depTime);
}

function placeArrivalNode(arrivalNode, arrTime) {
	var arrivalNodeMarker = L.marker(arrivalNode.geom[0]).addTo(map);
	placedMapRouteMarker(arrivalNodeMarker);
	
	arrivalNodeMarker.bindPopup('Arrival at: ' + arrTime);
}

function placeNode(node) {
	var nodeMarker = L.circleMarker(node.geom[0]).addTo(map);
	placedMapRouteMarker(nodeMarker);
	
	// TODO Remove debug popup
	nodeMarker.bindPopup('mode: ' + node.mode + ', name: ' + node.name);
}

function placeEdge(edge) {
	var edgeMarker = L.polyline(edge.geom, {'color' : 'red', 'weight' : 8, 'opacity' : 0.5}).addTo(map);
	placedMapRouteMarker(edgeMarker);
	
	// TODO Remove debug popup
	edgeMarker.bindPopup('mode: ' + edge.mode + ', name: ' + edge.name);
}

function zoomIntoNode(node) {
	map.fitBounds([node.geom], {'maxZoom' : 14});
}