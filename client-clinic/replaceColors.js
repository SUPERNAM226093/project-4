const fs = require('fs');
const path = require('path');

const directoryPath = path.join(__dirname, 'app');

const colorMap = {
    '#2563EB': '#0d6b52',
    '#1D4ED8': '#0a5241',
    '#EFF6FF': '#e8f9f4',
    '#EEF6FF': '#e8f9f4',
    '#EAF4FF': '#e8f9f4',
    '#D6EAFE': '#b2e8d9',
    '#F8FCFF': '#f0fdf8',
    '#102A56': '#0a3d2e',
    '#5F789A': '#4d8871',
    '#56CCF2': '#0ea882',
    'bg-blue-': 'bg-emerald-',
    'text-blue-': 'text-emerald-',
    'border-blue-': 'border-emerald-',
    'ring-blue-': 'ring-emerald-',
    'from-blue-': 'from-emerald-',
    'to-blue-': 'to-emerald-',
    'hover:bg-blue-': 'hover:bg-emerald-',
    'hover:text-blue-': 'hover:text-emerald-',
    'hover:border-blue-': 'hover:border-emerald-',
    'focus:ring-blue-': 'focus:ring-emerald-',
    'sky-300': 'emerald-300',
    'blue-600': 'emerald-600',
    'blue-500': 'emerald-500',
    'blue-50': 'emerald-50',
    'blue-100': 'emerald-100',
    'blue-200': 'emerald-200',
    'blue-800': 'emerald-800',
    'blue-700': 'emerald-700',
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
        
        // Custom regex replacements for tailwind classes if simple replace is not enough
        // But simple string replacement usually works fine
        
        // Loop through all mappings
        for (const [key, value] of Object.entries(colorMap)) {
            // Use regex to replace all occurrences globally, ignore case for hex codes
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
