/**
 * Main handling of all site functionality, as well as communication
 * with REST API server.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 *
 */

var mapboxToken = 'pk.eyJ1IjoiemFidXphIiwiYSI6ImNqZzZ1bDhrajlkbjAzMHBvcHhmY3l1cHEifQ.XsLjaSUMP9wVdeHc3SP32g';
var mapboxUrl = 'https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}';
var mapboxAttribution = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>';
var mapMaxZoom = 20;
var mapDefaultLat = 51.505;
var mapDefaultLong = -0.09;
var mapDefaultZoom = 10;
var map;
var mapStreetLayer;
var mapSatelliteLayer;
var isMapLayerStreet;

$(document).ready(init);
 
function init() {
	 initUI();
	 initMap();
}

function initUI() {
	// Departure date and time
	$('#departureDate').datepicker().datepicker("setDate", new Date());
	$('#departureTime').wickedpicker();
	
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

function planRouteHandler() {
	alert('Hello World');
}