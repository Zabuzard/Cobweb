/**
 * Utility methods not directly related to the application.
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