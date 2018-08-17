/**
 * Configuration of all site functionality, should be included
 * before any other script that relies on it.
 *
 * @module
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */

/** The URL of the routing server which offers a REST API. */
var routeRequestServer = 'http://localhost:2845/route';
/** The URL of the name search server which offers a REST API. */
var nameSearchRequestServer = 'http://localhost:2846/namesearch';
/** The URL of the nearest search server which offers a REST API. */
var nearestSearchRequestServer = 'http://localhost:2847/nearestsearch';
/**The access-token of the Mapbox API to use. */
var mapboxToken = 'pk.eyJ1IjoiemFidXphIiwiYSI6ImNqZzZ1bDhrajlkbjAzMHBvcHhmY3l1cHEifQ.XsLjaSUMP9wVdeHc3SP32g';
/** The URL of the Mapbox server. */
var mapboxUrl = 'https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}';
/** The attribution text to display on the map. */
var mapboxAttribution = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, '
  + '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>';
/** The maximal allowed zoom for the map. */
var mapMaxZoom = 20;
/** The default latitude to center the map view at. */
var mapDefaultLat = 49.23299;
/** The default longitude to center the map view at. */
var mapDefaultLong = 6.97633;
/** The default zoom to start the map view at */
var mapDefaultZoom = 6;
/** The map to use. */
var map;
/** The layer which displays streets. */
var mapStreetLayer;
/** The layer which displays a satellite image. */
var mapSatelliteLayer;
/** Whether or not the map currently displays the street layer. */
var isMapLayerStreet;
/** An array consisting of all route markers currently placed on the map. */
var mapRouteMarkerGroup = [];
/** A map connecting transportation mode strings to their corresponding IDs. */
var transModeToId = {
	'carMode' : 0,
	'tramMode' : 1,
	'footMode' : 2,
	'bikeMode' : 3
}
/** A map connecting transportation mode IDs to their corresponding strings. */
var transIdToMode = {
	0 : 'carMode',
	1 : 'tramMode',
	2 : 'footMode',
	3 : 'bikeMode'
}
/** A map connecting transportation mode IDs to their color their edges should be drawn in. */
var transIdToColor = {
	0 : 'red',
	1 : 'blue',
	2 : 'orange',
	3 : 'green'
}
/** Maximal amount of name search matches to request. */
var matchLimit = 5;
/** Map which connects input IDs to their current matches*/
var currentMatches = {
	'from': [],
	'to': []
}
/** The maximal time to wait for a server response until canceling it for a timeout, in milliseconds. */
var serverTimeout = 30 * 1000;