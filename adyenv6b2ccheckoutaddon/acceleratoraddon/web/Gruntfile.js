module.exports = function (grunt) {
    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        watch: {
            syncadyenaddon: {
                files: ['webroot/_ui/responsive/common/**/*.js'],
                tasks: ['sync:syncadyenaddon'],
            },
        },

        sync: {
            syncadyenaddon: {
                files: [
                    {
                        cwd: 'webroot/_ui/responsive/common/',
                        src: '**/*.js',
                        dest: '../../../../build/hybris/bin/modules/base-accelerator/yacceleratorstorefront/web/webroot/_ui/addons/adyenv6b2ccheckoutaddon/responsive/common/',
                    }
                ]
            },
        }

    });

    // Plugins
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-sync');

    // Default task(s). Run 'grunt watch' to start the watching task or add 'watch' to the task list and run 'grunt'.
    grunt.registerTask('default', ['sync', 'watch']);


};
