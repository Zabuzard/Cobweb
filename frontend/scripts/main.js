// Starts the init function when the document is loaded.
$(document).ready(init);
 
/**
 * Initializes the site functionality. That is, it initializes the UI and the map.<br>
 *<br>
 * If the site hash already has a request , it will be parsed and submitted.
 */
function init() {
	initUI();
	initMap();

	if (hasHash()) {
		planRouteFromHashHandler();
	}
}

/**
 * Initializes the user interface of the site.
 */
function initUI() {
	// Departure date and time
	$('#departureDate').datepicker().datepicker("setDate", new Date());
	$('#departureTime').timepicker({'scrollDefault' : 'now', 'timeFormat' : 'H:i'});
	$('#departureTime').timepicker('setTime', new Date());

	// Transportation modes
	$('#carMode').addClass('transportationModeSelected');
	$('.transportationMode').click(transportationModeClickHandler);

	// Plan route
	$('#planRoute').click(planRouteHandler);

	// Layer changer
	$('#mapLayerChanger').click(layerChangeHandler);
	
	// Name search handler
	initNameSearchHandler('from');
	initNameSearchHandler('to');
}

/**
 * Initializes name search handler for the given input field.
 * @param {string} inputId - The ID of the input field to initialize handlers for
 */
function initNameSearchHandler(inputId) {
	$('#' + inputId).keyup(nameSearchHandler);

	$('#' + inputId).autocomplete({
		source: function (request, response) {
			// Respond with an unfiltered data set
			response(currentMatches[inputId]);
		},
		select: function(e, ui) {
			e.preventDefault();

			// Display the label, not the value
			$('#' + inputId).val(ui.item.label);

			// Set the value to a hidden field
			$('#' + inputId + 'Val').val(ui.item.value);
		},
		focus: function(e, ui) {
			e.preventDefault();

			// Display the label, not the value
			$('#' + inputId).val(ui.item.label);
		},
		search: function(e, ui) {
			// Clear the hidden value before every search
			$('#' + inputId + 'Val').val('');
		}
	});
}

/**
 * Initializes the map.
 */
function initMap() {
	map = L.map('mapContainer', {
		contextmenu: true,
		contextmenuWidth: 140,
		contextmenuItems: [
			{
				text: 'From here',
				callback: fromHereHandler
			}, {
				text: 'To here',
				callback: toHereHandler
			}
		]
	}).setView([mapDefaultLat, mapDefaultLong], mapDefaultZoom);

	mapStreetLayer = L.tileLayer(mapboxUrl, {
		attribution: mapboxAttribution,
    	maxZoom: mapMaxZoom,
    	id: 'mapbox.streets',
    	accessToken: mapboxToken
	});

	mapSatelliteLayer = L.tileLayer(mapboxUrl, {
		attribution: mapboxAttribution,
    	maxZoom: mapMaxZoom,
    	id: 'mapbox.satellite',
    	accessToken: mapboxToken
	});

	map.addLayer(mapStreetLayer);
	isMapLayerStreet = true;
}

/**
 * Sends the given request to the routing server.
 * @param {{depTime:number, modes:number[], from:number, to:number}} request - The request
 * to send in the JSON format specified by the REST API
 */
function sendRouteRequestToServer(request) {
	$.ajax({
		url: routeRequestServer,
		method: 'POST',
		timeout: serverTimeout,
		contentType: 'application/json; charset=utf-8',
		dataType: 'json',
		data: JSON.stringify(request),
		success: function(response) {
			handleRouteServerResponse(response);
		},
		error: function (jqXHR, textStatus, errorThrown) {
			handleRouteServerError(textStatus, errorThrown);
		}
	});
}

/**
 * Sends the given request to the name search server.
 * @param {{name:string, amount:number}} request - The request
 * to send in the JSON format specified by the REST API
 * @param {number} inputId - The ID of the input field corresponding
 * to this request. Will be passed to the handler for displaying a drop-down list.
 */
function sendNameSearchRequestToServer(request, inputId) {
	$.ajax({
		url: nameSearchRequestServer,
		method: 'POST',
		timeout: serverTimeout,
		contentType: 'application/json; charset=utf-8',
		dataType: 'json',
		data: JSON.stringify(request),
		success: function(response) {
			handleNameSearchServerResponse(response, inputId);
		},
		error: function (jqXHR, textStatus, errorThrown) {
			handleNameSearchServerError(textStatus, errorThrown);
		}
	});
}

/**
 * Sends the given request to the nearest search server.
 * @param {{latitude:number, longitude:number}} request - The request
 * to send in the JSON format specified by the REST API
 * @param {number} inputId - The ID of the input field corresponding
 * to this request. Will be passed to the handler for setting to the matched node.
 */
function sendNearestSearchRequestToServer(request, inputId) {
	$.ajax({
		url: nearestSearchRequestServer,
		method: 'POST',
		timeout: serverTimeout,
		contentType: 'application/json; charset=utf-8',
		dataType: 'json',
		data: JSON.stringify(request),
		success: function(response) {
			handleNearestSearchServerResponse(response, inputId);
		},
		error: function (jqXHR, textStatus, errorThrown) {
			handleNearestSearchServerError(textStatus, errorThrown);
		}
	});
}