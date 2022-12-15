var path = require("path");
module.exports = require("../base").extend({

	/**
	 */

	"load": function(callback) {
		throw new Error("not implemented yet");
		
		var methodName = "onPlugin_" + Date.now();

		document[methodName] = function(err, module) {
			if(err) return callback(err);
			delete document[methodName];
			callback(null, module);
		}

		//TODO 
		//1. set global module.exports
		//2. on load, fetch module.exports
		//ISSUES - must be browserified

		var script = document.createElement("script");
		script.setAttribute("type", "text/javascript");
		script.setAttribute("src", "/plugin/wrap?callback=" + methodName + "&source=" + encodeURIComponent(this.source));
		document.getElementsByTagName("head")[0].appendChild(script);
	}
});

module.exports.test = function(source) {
	return (typeof source == "source") && source.substr(0, 4) == "http";
}