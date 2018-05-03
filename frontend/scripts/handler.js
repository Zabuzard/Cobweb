/**
 * Methods that are used as callbacks for certain UI actions.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */

function transportationModeClickHandler() {
	$(this).toggleClass('transportationModeSelected');
}

function layerChangeHandler() {
	var layerToRemove;
	var layerToAdd;
	
	if (isMapLayerStreet) {
		layerToRemove = mapStreetLayer;
		layerToAdd = mapSatelliteLayer;
	} else {
		layerToRemove = mapSatelliteLayer;
		layerToAdd = mapStreetLayer;
	}
	
	map.removeLayer(layerToRemove);
	map.addLayer(layerToAdd);
	
	isMapLayerStreet = !isMapLayerStreet;
}

function planRouteFromHashHandler() {
	var request = parseRequestFromHash();
	
	// Just ignore an invalid hash received from the hash
	// and do not display an error message
	if (request == null) {
		return;
	}
	
	setUiToRequest(request);
	handleValidRequest(request);
}

function planRouteHandler() {
	// Clear any previous messages
	clearMessages();
	// Delete any previous routes
	clearMapRouteMarker();
	
	var request = parseRequestFromPanel();
	
	if (request == null) {
		handleInvalidRequest(request);
	} else {
		handleValidRequest(request);
	}
}

function handleInvalidRequest(request) {
	setErrorMessage('The request is invalid.');
}

function handleValidRequest(request) {
	clearMessages();
	
	setUrlToRequest(request);
	
	sendRequestToServer(request);
}

function handleRouteServerError(status, error) {
	var text = "Error while communicating with the server.\n";
	text += "Status: " + status + "\n";
	text += "Message: " + error;
	setErrorMessage(text);
}

function handleRouteServerResponse(response) {
	// If no path could be computed
	if (response.journeys.length == 0) {
		var text = "Not reachable";
		setInfoMessage(text);
		return;
	}
	
	// Iterate all journeys
	for (var i = 0; i < response.journeys.length; i++) {
		handleJourney(response.journeys[i]);
	}
}

function handleJourney(journey) {
	var depTime = new Date(journey.depTime);
	var arrTime = new Date(journey.arrTime);
	
	// Place departure node
	placeDepartureNode(journey.route[0], depTime);
	
	// Process all nodes and edges except arrival and departure node
	for (var i = 1; i < journey.route.length - 1; i++) {
		var element = journey.route[i];
		if (element.type == 1) {
			// Element is edge
			placeEdge(element);
		} else {
			// Element is node
			placeNode(element);
		}
	}
	
	// Place arrival node
	placeArrivalNode(journey.route[journey.route.length - 1], arrTime);
	
	// Zoom into departure node
	zoomIntoNode(journey.route[0]);
}