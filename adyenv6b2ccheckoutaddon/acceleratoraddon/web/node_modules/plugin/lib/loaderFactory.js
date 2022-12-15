var _ = require("underscore"),
structr = require("structr");

exports = module.exports = structr({

	/**
	 */

	"__construct": function(plugins, loaders) {
		this._plugins = plugins;
		this._loaders = loaders;
		this._sortLoaders();
	},

	/**
	 */

	"addLoaderClass": function(loaderClass) {
		this._loaders.push(loaderClass);
	},

	/**
	 */

	"getLoader": function(source) {
		var self = this;
		var clazz = _.find(this._loaders, function(loader) {
			return loader.test(source, self._plugins.loader);
		});

		if(!clazz) {
			throw new Error("unable to find plugin loader for \"" + (source.path || source) + "\".");
		}

		return new clazz(source, this, this._plugins);
	},

	/**
	 */

	"_sortLoaders": function() {
		this._loaders.sort(function(a, b) {
			return a.priority > b.priority ? -1 : 1;
		});	
	}
});
