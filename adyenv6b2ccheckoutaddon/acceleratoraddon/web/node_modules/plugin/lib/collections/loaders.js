var structr = require("structr"),
async       = require("async");

module.exports = structr({

	/**
	 */

	"__construct": function(source) {
		this._loaders = source || [];
	},

	/**
	 */

	"add": function(loader) {
		this._loaders.push(loader);
	},

	/**
	 */

	"load": function(callback) {
		async.forEach(this._loaders, function(loader, next) {
			loader.load(next);
		}, callback);
	}
});