module.exports = {
    outputDir: 'app/www',
    publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
    runtimeCompiler: true,
};