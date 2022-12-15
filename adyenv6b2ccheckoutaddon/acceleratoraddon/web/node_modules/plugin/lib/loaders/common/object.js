var outcome = require("outcome"),
async       = require("async"),
step        = require("step"),
toarray     = require("toarray");

module.exports = require("../base").extend({

	/**
	 */

	"load": function(callback) {

		var self = this,
		source   = this.source,
		on = outcome.e(callback);


		step(

			/**
			 * first load all the dependencies
			 */

			function() {
				var req = toarray(source.require);


				async.map(req, function(depsOrPluginNames, next) {
					var multi = depsOrPluginNames instanceof Array,
					dopns = toarray(depsOrPluginNames);

					//if the dep is embedded in an array, then each of the embedded deps will be concatenated
					//in the end
					async.map(dopns, function(depOrPluginName, next) {
						//dep? it'll load
						try {
							self._loaders.getLoader(depOrPluginName).load(outcome.e(next).s(function(pgn) {
								var plugins = pgn instanceof Array ? pgn : [pgn];

								next(null, plugins.map(function(plugin) {
									return plugin.name;
								}));
							}));

						//otherwise it's a plugin name
						} catch(e) {
							//TODO - emit("warning", e)
							next(null, [depOrPluginName]);
						}	
						
					}, function(err, items) {
						next(null, {
							multi: multi,
							names: Array.prototype.concat.apply([], items)
						});
					});

					
				}, this);
			},

			/**
			 * next, load THIS plugin
			 */

			on.s(function(deps) {
				source._deps = deps;
				/*if(source.load) {
					var next = this;
					source.load.apply(source, self._plugins.loadDeps(deps).concat(self.loader).concat(on.s(function(module) {
						next(null, {
							plugin: function() {
								return module;
							}
						})
					})));
				} else {
					this(null, source);
				}*/

				this(null, source);
			}),

			/**
			 */

			on.s(function(exports) {
				exports._deps = source._deps;
				self._plugins.add(exports);
				this(null, exports);
			}),

			/**
			 */

			callback

		);
	}
});

module.exports.test = function(source) {
	return typeof source != "undefined" && (source.load || source.plugin) /*typeof source === "object"*/ && !(source instanceof Array);
}