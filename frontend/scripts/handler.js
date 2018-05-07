/**
 * Methods that are used as callbacks for certain UI actions.
 *
 * @module
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */

/**
 * Handler which handles a click on a transportation mode.<br>
 * <br>
 * It toggles a style class and attributes to make the element appear
 * selected or not selected.
 * @param {Object} this - The instance to handle
 */
function transportationModeClickHandler() {
	$(this).toggleClass('transportationModeSelected');
}

/**
 * Handler which handles a layer change for the map layers.<br>
 * <br>
 * It exchanges the street against the satellite layer and vice versa.
 */
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

/**
 * Plans a route request which is given in the sites hash.
 */
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

/**
 * Plans a route request which is given in the user interface.
 */
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

/**
 * Handles an invalid routing request.
 * @param {Object} request - The invalid routing request
 */
function handleInvalidRequest(request) {
	setErrorMessage('The request is invalid.');
}

/**
 * Handles a valid routing request. This will send the request to the server.
 * @param {{depTime:number, modes:number[], from:number, to:number}} request - The request
 * to handle in the JSON format specified by the REST API
 */
function handleValidRequest(request) {
	clearMessages();

	setUrlToRequest(request);

	sendRequestToServer(request);
}

/**
 * Handles an error that appeared while communicating with the routing server.
 * @param {Object} status - The status of the error
 * @param {Object} error - The error itself
 */
function handleRouteServerError(status, error) {
	var text = "Error while communicating with the server.\n";
	text += "Status: " + status + "\n";
	text += "Message: " + error;
	setErrorMessage(text);
}

/**
 * Handles a routing response from the server.
 * @param {{from:number, to:number, journeys:{depTime:number, arrTime:number,
 * route:{type:number, mode:number, name:string, geom:number[][]}[]}[]}} response - The response from the server as JSON
 * according to the REST API specification
 */
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

/**
 * Handles the given journey. The journey will be displays on the map.
 * @param {{depTime:number, arrTime:number, route:{type:number, mode:number, name:string, geom:number[][]}[]}[]}
 * journey - The journey from the server as JSON according to the REST API specification
 */
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