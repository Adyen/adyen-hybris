var path = require("path"),
fs = require("fs"),
async = require("async");

module.exports = require("../base").extend({

	/**
	 */

	"load": function(next) {
		var dir = this.source, self = this;

		var files = this.loader._fs.readdirSync(dir).
		filter(function(file) {
			return file.substr(0, 1) !== ".";
		}).
		map(function(file) {
			return [dir, file].join("/");
		});

		files.sort();

		self._loaders.getLoader(files).load(next);
	}
});

module.exports.test = function(source, loader) {
	try {
		return (typeof source === "string") && loader._fs.existsSync(source) && loader._fs.isDirectory(source);//fs.statSync(source).isDirectory();
	} catch(e) {
		try {
			return path.existsSync(source); //fallback to other versions
		} catch(e) { }
		return false;
	}
}