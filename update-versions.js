const fs = require('fs');


// Hopefully file hashes don't change here
var versionToSet = process.argv.slice(2);

var files = fs.readdirSync("build/libs");


files.forEach(function(file) {
    if (file.endsWith(".jar")) {
        // var fileHash = file.split("-")[1];
        // var newFileName = "build/libs/" + file.replace(fileHash, versionToSet);
        // if (!newFileName.includes("jar")) newFileName += ".jar";
        // fs.renameSync("build/libs/" + file, newFileName);
        console.log("Not renaming " + file + "due to semantic policies");
    }
});