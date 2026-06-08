const fs = require('fs');
const path = require('path');

const directoryPath = path.join(__dirname, 'app');

const colorMap = {
    'rgba\\(37,99,235': 'rgba(13,107,82',
    '#F2FAFF': 'var(--green-ultra)',
    '#1a8fe3': 'var(--green-mid)',
    '#0d6cbf': 'var(--green-dark)',
    '#0d2d6b': 'var(--green-dark)',
    '#1CA7EC': 'var(--green-light)',
    '#56CCF2': 'var(--green-teal)'
};

function processDirectory(directory) {
    fs.readdir(directory, (err, files) => {
        if (err) throw err;
        files.forEach(file => {
            const fullPath = path.join(directory, file);
            fs.stat(fullPath, (err, stat) => {
                if (err) throw err;
                if (stat.isDirectory()) {
                    processDirectory(fullPath);
                } else if (fullPath.endsWith('.tsx') || fullPath.endsWith('.ts')) {
                    replaceInFile(fullPath);
                }
            });
        });
    });
}

function replaceInFile(filePath) {
    fs.readFile(filePath, 'utf8', (err, data) => {
        if (err) throw err;
        let result = data;
        
        for (const [key, value] of Object.entries(colorMap)) {
            const regex = new RegExp(key, 'gi');
            result = result.replace(regex, value);
        }

        if (result !== data) {
            fs.writeFile(filePath, result, 'utf8', err => {
                if (err) throw err;
                console.log(`Updated: ${filePath}`);
            });
        }
    });
}

processDirectory(directoryPath);
