var structr = require("structr");

module.exports = structr({

	/**
	 */

	"priority": 1,

	/**
	 */

	"__construct": function(source, loaders, plugins) {
		this.source   = source;
		this._plugins = plugins;
		this.loader   = plugins.loader;
		this._loaders = loaders;
	},

	/**
	 */

	"abstract load": function(callback) {

	}
});

