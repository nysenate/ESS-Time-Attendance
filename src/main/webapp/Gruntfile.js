module.exports = function(grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        /** Path locations to be used as templates */
        cssRoot: 'assets/css',
        cssSource: '<%= cssRoot %>/src',
        lessSource: '<%= cssRoot %>/less',
        cssVendor: '<%= cssRoot %>/vendor',
        cssDest: '<%= cssRoot %>/dest',
        jsRoot: 'assets/js',
        jsSource: '<%= jsRoot %>/src',
        jsVendor: '<%= jsRoot %>/vendor',
        jsDest: '<%= jsRoot %>/dest',

        /** Compile LESS into css and place it into the css source directory */
        less: {
            dev: {
                files: {
                    '<%= cssSource %>/main.css': ['<%= lessSource %>/**.less']
                }
            }
        },

        /** Minify all css into one file */
        cssmin: {
            combine: {
                src: ['<%= cssSource %>/*.css', '<%= cssVendor %>/*.css'],
                dest: '<%= cssDest %>/app.min.css'
            }
        },

        /** Compress all js into dev and prod files */
        uglify: {
            vendor: {
                options: {
                    mangle: false,
                    preserveComments: 'some'
                },
                files: {
                    '<%= jsDest %>/timesheets-vendor.min.js':
                        ['<%= jsVendor %>/jquery.min.js',
                         '<%= jsVendor %>/jquery.ui.core.min.js',
                         '<%= jsVendor %>/jquery.ui.widget.min.js',
                         '<%= jsVendor %>/jquery.ui.button.min.js',
                         '<%= jsVendor %>/jquery.ui.position.min.js',
                         '<%= jsVendor %>/jquery.ui.dialog.min.js',
                         '<%= jsVendor %>/jquery.ui.datepicker.min.js',
                         '<%= jsVendor %>/angular.min.js',
                         '<%= jsVendor %>/angular-route.min.js',
                         '<%= jsVendor %>/angular-animate.min.js',
                         '<%= jsVendor %>/odometer.min.js',
                         '<%= jsVendor %>/highcharts.js'
                        ],
                    '<%= jsDest %>/timesheets-vendor-ie.min.js':
                        ['<%= jsVendor %>/json2.js']
                }
            },
            dev: {
            },
            prod: {
                options: {
                    compress: {
                        drop_console: true
                    },
                    preserveComments: 'some', /** Preserve licensing comments */
                    banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' +'<%= grunt.template.today("yyyy-mm-dd") %> */'
                },
                files: {
                    '<%= jsDest %>/timesheets.min.js':
                        ['<%= jsSource %>/common/ess-app.js', '<%= jsSource %>/auth/*.js']
                }
            }
        },

        /** Automatically run certain tasks based on file changes */
        watch: {
            less: {
                files: ['<%= lessSource %>/**.less', '<%= lessSource %>/common/**.less'],
                tasks: ['less', 'cssmin']
            },
            css: {
                files: ['<%= cssVendor %>/**.css'],
                tasks: ['cssmin']
            },
            jsVendor: {
                files: ['<%= jsVendor %>/**.js'],
                tasks: ['uglify:vendor']
            },
            jsSource: {
                files: ['<%= jsSource %>/**.js'],
                tasks: ['uglify:dev', 'uglify:prod']
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.registerTask('default', ['less', 'cssmin', 'uglify']);
};