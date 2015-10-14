define(['react'], function(React){
	var Common = {};


	Common.createSelect = function(name, list, selectedDetail, filter, includeNone, defaults){
	    var options = [];
	    if(list == undefined){
	    	return;
	    }
	    for (var i = 0; i < list.length; i++) {
	        var option = list[i];
	        options.push(
	            <option key={i} value={option._id}>{option.typeName}</option>
	        );
	    }
	    if(includeNone){
	    	 options.unshift(
            <option key='' value=''>Ingen</option>
        );
	    }

			if(defaults!=undefined){
		    for (var i = 0; i < defaults.length; i++) {
					var option = defaults[i];
					options.unshift(
						<option key={options.length}  value={option.id}>{option.name}</option>
					);
				}
			}
			if(selectedDetail == undefined){
				selectedDetail = '';
			}

	    return (
	    		<select name={name} value={selectedDetail}
	    			onChange={filter}>{options}</select>);
	};

	Common.createDetailSelect = function(name, list, selectedDetail, filter, includeNone, defaults){
	    var options = [];
	    if(list == undefined){
				console.log("Ingen liste");
	    	return;
	    }
	    for (var i = 0; i < list.length; i++) {
	        var option = list[i];
	        options.push(
	            <option key={i} value={option._id}>{option.description}</option>
	        );
	    }
	    if(includeNone){
				options.unshift(
        	<option key='' value=''>Ingen</option>
        );
	    }

			if(defaults!=undefined){
		    for (var i = 0; i < defaults.length; i++) {
					var option = defaults[i];
					options.unshift(
						<option key={options.length} value={option.id}>{option.name}</option>
					);
				}
			}
			if(selectedDetail == undefined){
				selectedDetail = '';
			}
	    return (
	    		<select name={name} value={selectedDetail}
	    			onChange={filter}>{options}</select>);
	};

	Common.createDate = function(dateAsLong) {
		if(!dateAsLong) {
			return "";
		}
		var daten = new Date(parseFloat(dateAsLong));
	    var yyyy = daten.getFullYear().toString();
	    var mm = (daten.getMonth()+1).toString(); // getMonth() is zero-based
	    var dd  = daten.getDate().toString();

	    var dateString =  (dd[1]?dd:"0"+dd[0]) + '.' + (mm[1]?mm:"0"+mm[0]) + '.' +yyyy ;

	    return dateString;
	};

	Common.debug = function(object){
		var output = '';
		for (var property in object) {
		  output += property + ': ' + object[property]+'; ';
		}
		console.log(output);
	};

	findById = function(id, list){
		for (i = 0; i < list.length; i++) {
		    if(list[i]._id == id){
		    	return list[i];
		    }
		}
		return null;
	};

	Common.calculateSum = function(purchaseList){
		var sum = 0;
		if(!purchaseList){
			return 0;
		}
		for(var i = 0; i < purchaseList.length; i++) {
			var p =  purchaseList[i];
			sum += p.amount;
		}
		return sum;
	};

	Common.createSearchDate = function(dateAsLong) {
		if(!dateAsLong) {
			return "";
		}
		var daten = dateAsLong;
    var yyyy = daten.getFullYear().toString();
    var mm = (daten.getMonth()+1).toString(); // getMonth() is zero-based
    var dd  = daten.getDate().toString();

    var dateString =  (dd[1]?dd:"0"+dd[0]) + '.' + (mm[1]?mm:"0"+mm[0]) + '.' +yyyy ;

    return dateString;
	};

	return Common;
});
