
var plugin = require("./plugin");

module.exports = function() {
	
	return plugin(arguments, require("./fs/web"), [
		require("./loaders/web/script"),
		require("./loaders/web/js-sardines")
	]);
}

module.exports.BaseLoader = plugin.BaseLoader;