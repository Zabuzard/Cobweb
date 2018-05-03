/**
 * Main handling of all site functionality, as well as communication
 * with REST API server.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */

var routeRequestServer = 'http://localhost:845/route';
var mapboxToken = 'pk.eyJ1IjoiemFidXphIiwiYSI6ImNqZzZ1bDhrajlkbjAzMHBvcHhmY3l1cHEifQ.XsLjaSUMP9wVdeHc3SP32g';
var mapboxUrl = 'https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}';
var mapboxAttribution = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>';
var mapMaxZoom = 20;
var mapDefaultLat = 49.23299;
var mapDefaultLong = 6.97633;
var mapDefaultZoom = 6;
var map;
var mapStreetLayer;
var mapSatelliteLayer;
var isMapLayerStreet;
var mapRouteMarkerGroup = [];

var transModeToId = {
	"carMode" : 0,
	"tramMode" : 1,
	"footMode" : 2,
	"bikeMode" : 3
}

var transIdToMode = {
	0 : "carMode",
	1 : "tramMode",
	2 : "footMode",
	3 : "bikeMode"
}

$(document).ready(init);
 
function init() {
	initUI();
	initMap();
	
	if (hasHash()) {
		planRouteFromHashHandler();
	}
}

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
}

function initMap() {
	map = L.map('mapContainer').setView([mapDefaultLat, mapDefaultLong], mapDefaultZoom);
	
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

function sendRequestToServer(request) {
	$.ajax({
		url: routeRequestServer,
		method: 'POST',
		timeout: 5 * 1000,
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