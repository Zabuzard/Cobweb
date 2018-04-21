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

function setErrorMessage(message) {
	$('#message').text(message);
	$('#message').addClass('errorMessage');
}

function clearErrorMessage() {
	$('#message').removeClass('errorMessage');
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
	var departureNodeMarker = L.marker(departureNode.geom).addTo(map);
	placedMapRouteMarker(departureNodeMarker);
	
	departureNodeMarker.bindPopup('Departure at: ' + depTime);
}

function placeArrivalNode(arrivalNode, arrTime) {
	var arrivalNodeMarker = L.marker(arrivalNode.geom).addTo(map);
	placedMapRouteMarker(arrivalNodeMarker);
	
	arrivalNodeMarker.bindPopup('Arrival at: ' + arrTime);
}

function placeNode(node) {
	var nodeMarker = L.circleMarker(node.geom).addTo(map);
	placedMapRouteMarker(nodeMarker);
	
	// TODO Remove debug popup
	nodeMarker.bindPopup(JSON.stringify(node, null, 2));
}

function placeEdge(edge) {
	var edgeMarker = L.polyline(edge.geom, {'color' : 'red', 'weight' : 8, 'opacity' : 0.5}).addTo(map);
	placedMapRouteMarker(edgeMarker);
	
	// TODO Remove debug popup
	edgeMarker.bindPopup(JSON.stringify(edge, null, 2));
}

function zoomIntoNode(node) {
	map.fitBounds([node.geom], {'maxZoom' : 17});
}

// TODO Remove mock and activate AJAX again
function mockServerResponse() {
	var response = {};
	response.from = 5843453;
	response.to = 3445345;
	
	var journeys = [];
	var firstJourney = {};
	firstJourney.depTime = 1524258300000;
	firstJourney.arrTime = 1524261782839;
	var firstJourneyRoute = [];
	firstJourneyRoute.push({
		"type" : 0,
		"name" : "Berliner Allee 7-5",
		"geom" : [48.007877, 7.828493]
	});
	firstJourneyRoute.push({
		"type" : 1,
		"mode" : 0,
		"name" : "Berliner Allee",
		"geom" : [[48.007877, 7.828493],
			[48.009139, 7.830648],
			[48.010454, 7.832698],
			[48.011682, 7.834658]]
	});
	firstJourneyRoute.push({
		"type" : 0,
		"name" : "Madisonallee 2",
		"geom" : [48.011682, 7.834658]
	});
	firstJourneyRoute.push({
		"type" : 1,
		"mode" : 0,
		"name" : "Madisonallee",
		"geom" : [[48.011682, 7.834658],
			[48.012436, 7.835941]]
	});
	firstJourneyRoute.push({
		"type" : 0,
		"name" : "Madisonallee 10",
		"geom" : [48.012436, 7.835941]
	});
	firstJourneyRoute.push({
		"type" : 1,
		"mode" : 3,
		"name" : "Emmy-Noether-Straße",
		"geom" : [[48.012436, 7.835941],
			[48.011273, 7.838019]]
	});
	firstJourneyRoute.push({
		"type" : 0,
		"name" : "Emmy-Noether-Straße 10",
		"geom" : [48.011273, 7.838019]
	});
	firstJourneyRoute.push({
		"type" : 1,
		"mode" : 2,
		"name" : "Hirtenweg",
		"geom" : [[48.011273, 7.838019],
			[48.011273, 7.838019],
			[48.011200, 7.837792],
			[48.011157, 7.837446],
			[48.011157, 7.837142]]
	});
	firstJourneyRoute.push({
		"type" : 0,
		"name" : "Hirtenweg 9",
		"geom" : [48.011157, 7.837142]
	});
	firstJourney.route = firstJourneyRoute;
	journeys.push(firstJourney);
	
	var secondJourney = {};
	secondJourney.depTime = 1524258300000;
	secondJourney.arrTime = 1524261782839;
	var secondJourneyRoute = [];
	secondJourneyRoute.push({
		"type" : 0,
		"name" : "Elefantenweg 37",
		"geom" : [48.011933, 7.833614]
	});
	secondJourneyRoute.push({
		"type" : 1,
		"mode" : 0,
		"name" : "Elefantenweg",
		"geom" : [[48.011933, 7.833614],
			[48.011514, 7.834106]]
	});
	secondJourneyRoute.push({
		"type" : 0,
		"name" : "Elefantenweg 1",
		"geom" : [48.011514, 7.834106]
	});
	secondJourney.route = secondJourneyRoute;
	journeys.push(secondJourney);
	
	response.journeys = journeys;
	
	return response;
}