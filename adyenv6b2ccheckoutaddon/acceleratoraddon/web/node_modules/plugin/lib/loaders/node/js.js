var path = require("path"),
_count   = 0;

function nextName() {
	return String("_module" + (_count++));
}

module.exports = require("../base").extend({

	/**
	 */

	"load": function(callback) {
		
		var realpath = this.loader.resolver.resolve(this.source),
		plugin       = require(realpath),
		basename     = path.basename(realpath),
		dirname      = path.dirname(realpath);

		//if it's a function, then it MUST be converted into an object. - cannot attach "name" otherwise
		if(typeof plugin == "function") {
			plugin = {
				require: plugin.require,
				plugin: plugin.plugin
			};
		}



		if(!plugin.name) {
			if(!this.source.match(/[\/\\]/g)) {
				plugin.name = this.source;
			} else {
				plugin.name  = /*/index.coffee|index.js/.test(basename) && */this.source.substr(0, 1) != "." ? path.basename(this.source).replace(/\.(js|coffee)$/, "") : nextName();
				// console.log(this.source);
			}
		}

		if(plugin.require) {
			plugin.require = plugin.require.map(function(deps) {

				function fixDep(dep) {
					
					if(/^\./.test(dep)) {
						dep = dep.replace(/^\./, dirname);

						//might be a directory - so ignore any thrown errors.
						try {

							//this fixes any name collision issues
							require(dep).name = nextName();
						} catch(e) { }
					}

					return dep;
				}

				if(!(deps instanceof Array)) return fixDep(deps);
				return deps.map(fixDep);
			});
		}
		plugin.path  = realpath;

		this._loaders.getLoader(plugin).load(callback);
	}
});

module.exports.test = function(source, loader) {
	try {
		return (typeof source === "string") && loader.resolver.resolve(source);
	} catch(e) {
		return false;
	}
}