
exports.isDirectory = function(path) {
	return !path.match(/\.\w+$/);
}

function getPath(path) {
	var parts = path.split('/'),
	cp = _sardines.allFiles;

	parts.forEach(function(part) {
		cp = cp[part];
	});

	return cp;
}

exports.readdirSync = function(path) {

	var cp = getPath(path);

	if(!cp) return [];


	return Object.keys(cp);
}

exports.existsSync = function(file) {
	return !!getPath(file);
}


exports.realpathSync = function(path) {
	return path;
}

exports.test = function() {
	return typeof _sardines != "undefined";
}