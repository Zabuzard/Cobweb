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

	var names = parseNamesFromHash();

	setUiToRequest(request, names.fromName, names.toName);
	handleValidRequest(request, names.fromName, names.toName);
}

/**
 * Plans a route request which is given in the user interface.
 */
function planRouteHandler() {
	// Clear any previous messages
	clearMessages();
	clearMiscInfo();
	// Delete any previous routes
	clearMapRouteMarker();

	var request = parseRequestFromPanel();

	if (request == null) {
		handleInvalidRequest(request);
	} else {
		var names = parseNamesFromPanel();
		handleValidRequest(request, names.fromName, names.toName);
	}
}

/**
 * Searches for a name given in the passed reference to <tt>this</tt>.
 * Results are displayed as drop-down list.
 */
function nameSearchHandler() {
	var name = $(this).val();
	// Ignore empty names
	if (name.length === 0 || !name.trim()) {
		return;
	}

	// Build request
	var request = {};
	request.name = name;
	request.amount = matchLimit;

	sendNameSearchRequestToServer(request, $(this).attr('id'));
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
 * @param {string} fromName - The current value in the from field
 * @param {string} toName - The current value in the to field
 */
function handleValidRequest(request, fromName, toName) {
	clearMessages();
	clearMiscInfo();

	setUrlToRequest(request, fromName, toName);

	sendRouteRequestToServer(request);
}

/**
 * Handles an error that appeared while communicating with the routing server.
 * @param {Object} status - The status of the error
 * @param {Object} error - The error itself
 */
function handleRouteServerError(status, error) {
	var text = "Error while communicating with the route server.\n";
	text += "Status: " + status + "\n";
	text += "Message: " + error;
	setErrorMessage(text);
}

/**
 * Handles a routing response from the server.
 * @param {{from:number, to:number, journeys:{depTime:number, arrTime:number,
 * route:{time:number, type:number, mode:number, name:string, geom:number[][]}[]}[]}} response - The response from the
 * server as JSON according to the REST API specification
 */
function handleRouteServerResponse(response) {
	setMiscInfo(response.time);

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

/**
 * Handles a name search response from the server.
 * @param {{time:number, matches:{id:number, name:string}[]} response - The response from the server as JSON
 * according to the REST API specification
 * @param {number} inputId - The ID of the input field corresponding to this response.
 */
function handleNameSearchServerResponse(response, inputId) {
	if (response.matches.length === 0) {
		return;
	}

	// Iterate all matches and build the data-source
	var dataSource = [];
	for (var i = 0; i < response.matches.length; i++) {
		var match = response.matches[i];
		dataSource[i] = {
			value: match.id,
			label: match.name
		}
	}
	
	currentMatches[inputId] = dataSource;
}

/**
 * Handles an error that appeared while communicating with the name search server.
 * @param {Object} status - The status of the error
 * @param {Object} error - The error itself
 */
function handleNameSearchServerError(status, error) {
	var text = "Error while communicating with the name search server.\n";
	text += "Status: " + status + "\n";
	text += "Message: " + error;
	setErrorMessage(text);
}