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
	var request = parseRequestFromPanel();
	
	if (request == null) {
		handleInvalidRequest(request);
	} else {
		handleValidRequest(request);
	}
}

function handleInvalidRequest(request) {
	$('#message').text('The request is invalid.');
	$('#message').addClass('errorMessage');
}

function handleValidRequest(request) {
	$('#message').removeClass('errorMessage');
	$('#message').text('');
	
	setUrlToRequest(request);
	
	// TODO Do some AJAX etc
	alert('TODO: ' + JSON.stringify(request));
}