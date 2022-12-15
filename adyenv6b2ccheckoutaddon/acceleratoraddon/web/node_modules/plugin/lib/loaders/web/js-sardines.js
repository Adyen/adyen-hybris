var path = require("path");

module.exports = require("../base").extend({

	/**
	 */

	"load": function(callback) {
		var plugin  = require(this.source);
		plugin.name = path.basename(this.source).replace(/\.js$/, "");
		this._loaders.getLoader(plugin).load(callback);
	}
});

module.exports.test = function(source, loader) {
	try {
		return loader._fs.test() && !!require.resolve(source);
	} catch(e) {
		return false;
	}
}

function getPath(path) {
	var parts = path.split('/'),
	cp = allFiles;

	parts.forEach(function(part) {
		cp = cp[part];
	});

	return cp;
}