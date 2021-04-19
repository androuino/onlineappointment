const gulp = require('gulp'),
    terser = require('gulp-terser'),
    concat = require('gulp-concat'),
    cssmin = require('gulp-cssmin'),
    sassGlob = require('gulp-sass-glob'),
    rename = require('gulp-rename'),
    sass   = require('gulp-sass');

const paths = {
    prefix: 'system',
    js : "resources/private/jsrc/*.js",
    js_out : 'resources/public/js/',
    scss: 'resources/private/scss/*.scss',
    css: 'resources/public/css/',
};

gulp.task('js', gulp.series([], function() {
    return gulp.src(paths.js)
        .pipe(concat(paths.prefix + ".min.js"))
        //.pipe(terser())
        .pipe(gulp.dest(paths.js_out));
}));

sass.compiler = require('node-sass');
gulp.task('css', gulp.series([], function () {
    return gulp.src(paths.scss)
        .pipe(sassGlob())
        .pipe(sass().on('error', sass.logError))
        .pipe(cssmin())
        .pipe(gulp.dest(paths.css));
}));

// Build
gulp.task('default', gulp.series(['js','css']));
