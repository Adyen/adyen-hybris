var Loader = require("./loader");

module.exports = function(args, fs, loaders) {

	loaders.unshift(
		require("./loaders/common/array"),
		require("./loaders/common/object"),
		require("./loaders/common/directory")
	);

	return new Loader(Array.prototype.slice.call(args, 0), fs, loaders);
}

module.exports.BaseLoader = require("./loaders/base");