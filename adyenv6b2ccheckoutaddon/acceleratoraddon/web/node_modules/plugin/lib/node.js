
var plugin = require("./plugin");

module.exports = function() {
	return plugin(arguments, require("./fs/node"), [
		require("./loaders/node/js")
	]);
}

module.exports.BaseLoader = plugin.BaseLoader;