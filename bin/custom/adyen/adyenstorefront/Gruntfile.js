module.exports = function(grunt) {
  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    watch: {
        less: {
            files: ['web/webroot/WEB-INF/_ui-src/shared/less/variableMapping.less','web/webroot/WEB-INF/_ui-src/shared/less/generatedVariables.less','web/webroot/WEB-INF/_ui-src/**/themes/**/less/variables.less','web/webroot/WEB-INF/_ui-src/**/themes/**/less/style.less'],
            tasks: ['less'],
        },
        fonts: {
            files: ['web/webroot/WEB-INF/_ui-src/**/themes/**/fonts/*'],
            tasks: ['sync:syncfonts'],
        },
        ybasejs: {
            files: ['web/webroot/WEB-INF/_ui-src/responsive/lib/ybase-0.1.0/js/**/*.js'],
            tasks: ['sync:syncybase'],
        },
        jquery: {
            files: ['web/webroot/WEB-INF/_ui-src/responsive/lib/jquery*.js'],
            tasks: ['sync:syncjquery'],
        },
    },
    less: {
        default: {
            files: [
                {
                    expand: true,
                    cwd: 'web/webroot/WEB-INF/_ui-src/',
                    src: '**/themes/**/less/style.less',
                    dest: 'web/webroot/_ui/',
                    ext: '.css',
                    rename:function(dest,src){
                       var nsrc = src.replace(new RegExp("/themes/(.*)/less"),"/theme-$1/css");
                       return dest+nsrc;
                    }
                }
            ]
        },
    },

    sync : {
    	syncfonts: {
    		files: [{
                expand: true,
    			cwd: 'web/webroot/WEB-INF/_ui-src/',
    			src: '**/themes/**/fonts/*',
    			dest: 'web/webroot/_ui/',
    			rename:function(dest,src){
                	var nsrc = src.replace(new RegExp("/themes/(.*)"),"/theme-$1");
                	return dest+nsrc;
             }
    		}]
    	},
    	syncybase: {
    		files: [{
    			cwd: 'web/webroot/WEB-INF/_ui-src/responsive/lib/ybase-0.1.0/js/',
    			src: '**/*.js',
    			dest: 'web/webroot/_ui/responsive/common/js',
    		}]
    	},
    	syncjquery: {
    		files: [{
    			cwd: 'web/webroot/WEB-INF/_ui-src/responsive/lib',
    			src: 'jquery*.js',
    			dest: 'web/webroot/_ui/responsive/common/js',
    		}]
    	}
    }
    
});
 
  // Plugins
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-sync');


  // Default task(s).
  grunt.registerTask('default', ['less', 'sync']);



};