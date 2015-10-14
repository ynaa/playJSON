var _ = require('underscore'),
  names = require('./importmodule.js');

findSuperman(names());

function findSuperman(values) {
  _.find(values, function(name) {
    if (name === 'Clark Kent') {
      console.log('It\'s Superman!');
    } else {
      console.log('... No superman!');
    }
  });
}
