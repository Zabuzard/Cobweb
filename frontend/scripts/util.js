/**
 * Utility methods.
 *
 * @module
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */

/**
 * Whether or not the given value is an integer.
 * @param {*} value - The value in question
 * @returns <tt>True</tt> if the given value is an integer, <tt>false</tt> otherwise
 */
function isInteger(value) {
	if (Number.isInteger(value)) {
		return true;
	}
    var n = Math.floor(Number(value));
    return n !== Infinity && String(n) === value;
}

/**
 * Whether or not the site has a hash value set in its URL.
 * @returns <tt>True</tt> if the site has a hash set, <tt>false</tt> otherwise
 */
function hasHash() {
	return window.location.hash.length > 1;
}

/**
 * Fills the URL hash with the information given by the given routing request.
 * @param {{depTime:number, modes:number[], from:number, to:number}} request - The request
 * to use in the JSON format specified by the REST API
 */
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

/**
 * Fills the user interface with the information given by the given routing request.
 * @param {{depTime:number, modes:number[], from:number, to:number}} request - The request
 * to use in the JSON format specified by the REST API
 */
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

/**
 * Displays the given info message in a message box on the site.
 * @param {string} message - The message to display
 */
function setInfoMessage(message) {
	clearMessages();
    $('#message').text(message);
	$('#message').addClass('infoMessage');
}

/**
 * Displays the given error message in a message box on the site.
 * @param {string} message - The message to display
 */
function setErrorMessage(message) {
	clearMessages();
	$('#message').text(message);
	$('#message').addClass('errorMessage');
}

/**
 * Clears any displays message in the message box on the site.
 */
function clearMessages() {
	$('#message').removeClass('infoMessage errorMessage');
	$('#message').text('');
}

/**
 * Places the given route marker on the map.
 * @param {Marker} marker - A map route marker
 */
function placedMapRouteMarker(marker) {
	mapRouteMarkerGroup.push(marker);
}

/**
 * Clears any route marker currently displays on the map.
 */
function clearMapRouteMarker() {
	for (var i = 0; i < this.mapRouteMarkerGroup.length; i++) {
		map.removeLayer(mapRouteMarkerGroup[i]);
	}
}

/**
 * Places the given departure node on the map.
 * @param {{type:number, mode:number, name:string, geom:number[][]}} departureNode -
 * A departure node in the JSON format for route elements of a journey as specified in the REST API
 * @param {Date} depTime - The departure date and time as date object
 */
function placeDepartureNode(departureNode, depTime) {
	var departureNodeMarker = L.marker(departureNode.geom[0]).addTo(map);
	placedMapRouteMarker(departureNodeMarker);

	departureNodeMarker.bindPopup('Departure at: ' + depTime);
}

/**
 * Places the given arrival node on the map.
 * @param {{type:number, mode:number, name:string, geom:number[][]}} arrivalNode -
 * A arrival node in the JSON format for route elements of a journey as specified in the REST API
 * @param {Date} arrTime - The arrival date and time as date object
 */
function placeArrivalNode(arrivalNode, arrTime) {
	var arrivalNodeMarker = L.marker(arrivalNode.geom[0]).addTo(map);
	placedMapRouteMarker(arrivalNodeMarker);

	arrivalNodeMarker.bindPopup('Arrival at: ' + arrTime);
}

/**
 * Places the given node on the map.
 * @param {{type:number, mode:number, name:string, geom:number[][]}} node -
 * A node in the JSON format for route elements of a journey as specified in the REST API
 */
function placeNode(node) {
	var nodeMarker = L.circleMarker(node.geom[0]).addTo(map);
	placedMapRouteMarker(nodeMarker);

	// TODO Remove debug popup
	nodeMarker.bindPopup('mode: ' + node.mode + ', name: ' + node.name);
}

/**
 * Places the given edge on the map.
 * @param {{type:number, mode:number, name:string, geom:number[][]}} edge -
 * An edge in the JSON format for route elements of a journey as specified in the REST API
 */
function placeEdge(edge) {
	var edgeMarker = L.polyline(edge.geom, {'color' : 'red', 'weight' : 8, 'opacity' : 0.5}).addTo(map);
	placedMapRouteMarker(edgeMarker);

	// TODO Remove debug popup
	edgeMarker.bindPopup('mode: ' + edge.mode + ', name: ' + edge.name);
}

/**
 * Zooms the map view into the given node.
 * @param {{type:number, mode:number, name:string, geom:number[][]}} node -
 * A node in the JSON format for route elements of a journey as specified in the REST API
 */
function zoomIntoNode(node) {
	map.fitBounds([node.geom], {'maxZoom' : 14});
}