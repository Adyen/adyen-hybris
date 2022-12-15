var structr      = require("structr"),
dref             = require("dref"),
EventEmitter     = require("events").EventEmitter,
async            = require("async"),
LoaderFactory    = require("./loaderFactory"),
LoaderCollection = require("./collections/loaders"),
PluginCollection = require("./collections/plugins"),
Resolver         = require("./resolver"),
outcome          = require("outcome");

module.exports = structr(EventEmitter, {

	/**
	 */

	"__construct": function(plugInto, fs, loaders) {
		this._params        = {};
		this._fs            = fs;
		this._loaders       = new LoaderCollection();
		this._plugins       = new PluginCollection(plugInto, this);
		this.loaderFactory  = new LoaderFactory(this._plugins, loaders);
		this.resolver       = new Resolver(this);
	},

	/**
	 * returns params, or sets params. Note that you can
	 * deeply reference a param - this helps avoid null exceptions
	 */

	"params": function(keyOrParams, value) {
		if(!arguments.length) return this._params;
		if(arguments.length === 1) {
			if(typeof keyOrParams === "object") {
				for(var key in keyOrParams) {
					this.params(key, keyOrParams[key]);
				}
				return this;
			}
			
			return dref.get(this._params, keyOrParams);
		}
		dref.set(this._params, keyOrParams, value);
		return this;
	},

	/**
	 * extend onto this loader. Useful for doing stuff like adding
	 * custom loaders e.g dnode
	 */

	"use": function(extension) {
		extension(this);
		return this;
	},

	/**
	 * adds plugins to be loaded in on .load()
	 */

	"require": function() {
		var req = this._loaders, self = this;
		Array.prototype.slice.call(arguments, 0).forEach(function(dep) {
			req.add(self.loaderFactory.getLoader(dep));
		});
		return this;
	},

	/**
	 */

	"paths": function() {
		return this.resolver.paths.apply(this.resolver, arguments);
	},

	/**
	 * return one plugin
	 */

	"loadModule": function(search, callback) {
		return this._plugins.loadModule(search, callback);
	},

	/**
	 * return multiple plugins, OR loads based on the search. This is similar to 
	 * require, but it's immediate. 
	 */

	"loadModules": function(search, callback) {
		return this._plugins.loadModules(search, callback);
	},

	/**
	 */

	"load": function(onLoad) {

		if(onLoad) {
			this.once("ready", onLoad);
		}

		//cannot reload.
		if(this._loading) return this;
		this._loading = true;

		var self = this, on = outcome.error(onLoad);

		//first load in the sources where the plugins live - need to unravel shit
		this._loaders.load(on.success(function() {
			
			//finally, load the plugins - this should be instant.
			self._plugins.load(on.success(function() {

				//apply this exports to this loader, and finish!
				self.exports = self._plugins.exports;

				//notify any listeners
				self.emit("ready", null, self.exports);
			}));
		}));

		return this;
	}
});
