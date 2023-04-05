module.exports = {
    content: ['generated-example-doc.html'],
    css: ['./src/main/resources/docu/styles/tailwind.min.css'],
    output: './src/main/resources/docu/styles/tailwind.purged.css',
    safelist: [ 'w-1/1', 'w-1/2', 'w-1/3', 'w-1/4', 'w-1/5', 'w-2/3', 'top-1/2', 'top-1/3', 'top-1/4', '-translate-y-2/4' ]
}