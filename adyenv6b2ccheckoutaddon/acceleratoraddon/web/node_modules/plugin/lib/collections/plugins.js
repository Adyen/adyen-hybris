var structr = require("structr"),
sift        = require("sift"),
_           = require("underscore"),
async       = require("async"),
step = require("step"),
EventEmitter = require("events").EventEmitter,
outcome = require("outcome");


var PluginLoader = structr(EventEmitter, {

	/**
	 */

	"__construct": function(collection, plugin) {
		this.collection = collection;
		if(plugin) this.plugin(plugin);
	},

	/**
	 */

	"plugin": function(plugin) {
		if(!arguments.length) return this._plugin;
		var reload = false;
		if(!!this.module) reload = true;
		plugin.priority = plugin.priority || 0;
		this._plugin = plugin;
		this.module  = null;
		this._copyPluginAttrs();
		if(reload) this.load();
	},

	/**
	 */

	"dispose": function() {
		if(this.plugin.unplug) this.plugin.unplug();
		delete this.collection.exports[this.name];
	},

	/**
	 */

	"load": function(callback) {
		if(!callback) callback = function() { };
		if(!!this.module) return callback(null, this.module);
		this.once("loaded", callback);
		if(this._loading) return;

		this._loading = true;

		var args, 
		self = this,
		on = outcome.e(callback);


		step(
			function() {
				self.collection.loadDeps(self._plugin._deps, this);
			},
			on.s(function(deps) {

				args = deps.
				concat(self.collection.plugInto).
				concat(self.collection.loader);

				if(self._plugin.load) {
					self._plugin.load.apply(self._plugin, args.concat(this));
				} else {
					this(null, self._plugin.plugin.apply(self._plugin, args));
				}
			}),
			on.s(function(module) {
				self.collection.exports[self.name] = self.module = module || { };
				self._loading = false;
				self.emit("loaded", null, module);
			})
		);
	},

	/**
	 */

	"_copyPluginAttrs": function() {
		for(var property in this._plugin) {
			var v = this._plugin[property];
			if((typeof v == "function") || !!this[property]) continue;
			this[property] = v;
		}
	}
});

module.exports = structr({

	/**
	 */

	"__construct": function(plugInto, loader) {

		//plugins that have yet to be loaded
		this._pluginLoaders = [];

		//item to plugin into
		this.plugInto = plugInto;

		//loader which glues everything together
		this.loader   = loader;

		//all the modules combined
		this.exports = {};
	},

	/**
	 */

	"add": function(plugin) {

		//exists?
		var pluginLoader = this._findPluginLoaderByName(plugin.name);

		if(!pluginLoader) {
			pluginLoader = new PluginLoader(this);
			this._pluginLoaders.push(pluginLoader);
		}

		pluginLoader.plugin(plugin);
		//this._sort();
	},

	/**
	 */

	"remove": function(pluginName) {
		var pluginLoader = this._findPluginLoaderByName(pluginName);
		if(!pluginLoader) return;
		pluginLoader.dispose();
		var i = this._pluginLoaders.indexOf(pluginLoader);
		if(~i) this._pluginLoaders.splice(i, 1);
	},

	/**
	 */

	"loadModule": function(name, callback) {
		if(!callback) callback = function(){}
		this.loadModules(name, outcome.e(callback).s(function(modules) {
			if(!modules.length) return callback(new Error("module '" + name + "' does not exist"));
			callback(null, modules.pop());
		}));
	},

	/**
	 */

	"loadModules": function(q, callback) {
		if(!callback) callback = function(){}
		async.mapSeries(sift(this._query(q), this._pluginLoaders), function(loader, next) {
			loader.load(next);
		}, callback);
	},

	/**
	 */

	"loadDeps": function(deps, next) {

		var self = this;

		async.mapSeries(deps || [], function(dep, next) {
			self.loadModules(dep.names, outcome.e(next).s(function(deps) {
				next(null, dep.multi ? deps : deps.pop());
			}));
		}, next);
	},

	/**
	 */

	"load": function(callback) {
		async.forEachSeries(this._pluginLoaders, function(loader, next) {
			//console.log(loader.name)
			loader.load(next);
		}, callback);
	},

	/**
	 */

	"_findPluginLoaderByName": function(name) {
		return _.find(this._pluginLoaders, function(pluginLoader) {
			return pluginLoader.name == name;
		});
	},

	/**
	 */

	"_sort": function() {
		this._pluginLoaders = this._pluginLoaders.sort(function(a, b) {
			return a.priority > b.priority ? -1 : 1;
		});
	},

	/**
	 */

	"_query": function(q) {

		if(q instanceof Array) {
			var self = this;
			return { $or: q.map(function(q) {
				return self._query(q);
			})}
		}

		var query = q;

		if(typeof q == "string") {
			q = new RegExp("^" + q + "$");
		}

		if(q instanceof RegExp) {
			query = { name: q };
		}

		return query;
	}
	
});