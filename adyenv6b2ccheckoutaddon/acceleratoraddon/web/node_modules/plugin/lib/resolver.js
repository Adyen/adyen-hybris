var structr = require("structr"),
resolve     = require("resolve");

module.exports = structr({

	/**
	 */

	"__construct": function(loader) {
		this._loader = loader;
		this._paths  = [process.cwd()];
	},

	/**
	 */

	"resolve": function(path) {

		//catch resolution error - this will happen with coffeescript
		try {
			return resolve.sync(path, {
				paths: this._paths
			});
		} catch(e) {
			return require.resolve(path);
		}
	},

	/**
	 */

	"paths": function() {
		if(arguments.length) {
			this._paths.push.apply(this._paths, arguments);
		} else {
			return this._paths;
		}
		return this._loader;
	}
});