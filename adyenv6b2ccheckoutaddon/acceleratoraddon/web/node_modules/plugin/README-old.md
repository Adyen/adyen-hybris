### Plugin Library for node.js

### Motivation

- Modularity - encourages code-reuse, abstraction, and encapsulation
- Easily drop plugins in and out without breaking your program
- Maintainability
- Flexibility over dependencies
- Asyncronously load remote plugins via [dnode](/substack/dnode) (*soon* - [now.js](/flotype/now), [beanpoll](beanpole)). 
- *soon* double as online async module loader (similar to [head](https://github.com/headjs/headjs)).

## Basic Usage

A simple use case with express:

```javascript

var plugin = require('plugin').loader(),
server = require('express').createServer();

plugin.options(server, true).
require("path/to/plugins/dir");

server.listen(8080);

```

In your `hello world` plugin:

```javascript

exports.plugin = function(server) {
	
	server.get('/', function(req, res) {
		
		res.send("Hello World!");
	});
}

```

## Plugins

- [plugin.dnode](/crcn/plugin.dnode) - dnode plugin


## Plugin API

### plugin.require(path)

includes target dependencies

```javascript
plugins.require('path/to/plugin.js').      // require one plugin
require('path/to/plugins/dir').          // require all plugins in directory
require('path/to/plugins/**/*.plugin.js'). // find plugins, and load them
require('plugin1.js','plugin2.js','plugin3.js'). //multiple plugin args
require('./config.json').load(); //load plugins in configuration file { plugins: ['my/plugin.js','...'] }
```

### plugin.paths(path)

adds a path to scan when requiring plugins. Similar to the old `require.paths.unshift`

```javascript
plugin.paths('/path/to/plugins').require('my-plugin');

console.log(plugin.paths());// ['/path/to/plugins','/path/to/node_modules','...'];
```

### plugin.params(params)

params specific to plugin - like constructor parameters

bootstrap.js:

```javascript
plugin.params({
	'server': {
		'port': 8080
	}
}).

//or
params('server', { port: 8080 }).
require('server');
```

api.server/index.js:

```javascript
exports.plugin = function(ops, params) {
	console.log(params.port); //8080	
}
```


### plugin.options(ops)

Adds / returns options which are passed in the first parameter for each plugin.

bootstrap.js:

```javascript
plugin.options({ message: 'hello world!' }).require('hello.plugin.js');
```

hello.plugin.js:

```javascript
exports.plugin = function(ops) {
	console.log(ops.message); //hello world!
}
```

### plugin.onLoad(pluginSearch, ret, callback)

Listens for when a plugin is ready - useful especially if a dnode server resets

```javascript

plugin.onLoad('my.plugin', function() {
	
	console.log("ready!");
}).require('my.plugin');
```

### plugin.local plugin.remote

remote/local collections. Same api as plugin (emit, plugin, plugins)

### plugin.emit(type, data)

emits a method against all loaded plugins. If the method doesn't exist, it'll be ignored.

bootstrap.js:

```javascript
plugin.loader().require('api.server').load().emit('doStuff');
```

api.server/index.js:

```javascript
exports.plugin = function() {
	
	return {
		doStuff: function() {
			console.log("PREPARE");	
		},
		init: function() {
			console.log("INIT");
		}
	};
}
```

### plugin.load()

Loads the plugins, and initializes them.

### plugin.next(callback)

Queue function called after loading in all modules

### plugin.exports

All the invokable methods against modules




### plugin.plugins(search)

Returns *multiple* plugins based on the search criteria.

```

var loader = plugin.loader();

loader.require('oauth.part.twitter','oauth.part.facebook','oauth.core').
load(function() {
	loader.plugins(/^oauth.part.\w+$/).forEach(function(service) {
	
		//do stuff with the oauth plugins

	});
});
```


### plugin.plugin(search)

Returns a *single* based on the search criteria given.


### plugin.loaders

Loads plugins passed into `plugin.require()`.

```javascript
//dnode plugin
plugin.loaders.push({
	test: function(path) {
		return !!path.match(/dnode+\w+:\/\//); //dnode+https://my-dnode-server.com
	},
	load: function(path, callback) {
		//load dnode module here
	}
});
```

### plugin.factory(fn)

Plugin factory function

plugin.factory(function(module, options, params) {
	return module(options, params); //instead of exports.plugin = function(){}, it would be module.exports = function(options, params)
});

### plugin.newPlugin

Plugin factory for plugin. Setting this method will change the way modules are loaded in.

```javascript


plugin.newPlugin = function(module, options, params) {	
	return module(options, params); //instead of exports.plugin = function(){}, it would be module.exports = function(options, params)
};

```


## Plugins API


### exports.require

Dependencies for the given plugin. This is checked once `plugin.call`, or `plugin.load` is invoked. An exception is thrown if there are any missing dependencies.

```javascript

exports.require = ['api.services.photos.*','another-plugin']; //requires any photo services. E.g: api.services.photos.facebook, api.services.photos.flickr

exports.require = [/api\.\w+/]; //regexp test

exports.require = function(name) { //function test
	return name.indexOf('api.services.photos') > -1
};


```

You can also load in any given plugin via `exports.require`:

```javascript

exports.require = 'my-plugin';


exports.plugin = function() {
	
	var plugin = this;

	return {
		init: function() {
			
			plugin.plugin('my-plugin').doStuff();//return a single instance
			plugin.plugins('my-plugin').forEach(funtion(plugin) {//return multiple instances
				plugin.doStuff();
			});
		}
	}
}
```

### exports.name

Optional name for the plugin. The default value is name provided when requiring the plugin.


### Plugin exports.plugin(options, params, plugin)

Called when the plugin is loaded. 

- `options` - options which are passed to the plugin, along with every other plugin.
- `params` - parameters which are specific to the loaded plugin.
- `plugin` - the plugin loader. Also accessible via `this`.
- return type can be `void`, or an `object`.







 

