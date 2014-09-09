var overviewControllers = angular.module("overviewController", [],
	function($compileProvider) {
		$compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file|javascript):/);
});

overviewControllers.controller("OverviewController", function($scope, $http, $filter,
		$routeParams) {
	$scope.yearinterval = {};
	populateOverviewData($scope, $http, $routeParams);
	$scope.keys = function(obj){
	  return obj? Object.keys(obj) : [];
	}
	$scope.createUrlparameters = function(expTypeName, intervalName){
		var params = "expType=" + getExpTypeId($scope, expTypeName);
		var interval = findInterval($scope, intervalName);
		params += "&start=" + interval.start;
		params += "&stop=" + interval.end;
		return params;
	}
	$scope.toggleYears = function(year, toggle) {
		if (isNaN(year) || year.length != 4) {
			return;
		}
		if (!toggle) {
			for ( var prop in $scope.sumByIntervals) {
				if (prop.startsWith(year + " ")) {
					delete $scope.sumByIntervals[prop];
				}
			}
			return;
		}
		var url = '/yearinterval/' + year;
		$http.get(url).success(
			function(data, status, headers, config) {
				var intervals = data.result;
				$scope.yearinterval[year] = intervals;
				var sums = {};
				for (var i = 0; i < intervals.length; i++) {
					var item = intervals[i];
					getByInterval($http, item, function(data, status, headers, config, theItem) {
						theItem.name = theItem.year + " - " + pad(theItem.monthNum, 2) + " " + theItem.month;
						$scope.sumByIntervals[theItem.name] = data.result;
					});
				}
			});
	};
});

function pad(num, size) {
    var s = num+"";
    while (s.length < size) s = "0" + s;
    return s;
}

function populateOverviewData($scope, $http, $routeParams) {
	/*
	var url = '/overview';
	$http.get(url).success(function(data, status, headers, config) {
		$scope.snittene = data.result.snittene;
		$scope.alle = data.result.itervalExpPurchaseList;
	});
*/
	populateExpenseTypes($scope, $http);
	createIntervals($scope, $http);
}

function getByInterval($http, interval, callback) {
	var url = '/interval?start=' + interval.start + '&end=' + interval.end;
	$http.get(url).success(function(data, status, headers, config) {
		callback.call(this, data, status, headers, config, interval);
	});
}

function createIntervals($scope, $http) {
	var url = '/intervals';
	$http.get(url).success(
		function(data, status, headers, config) {
			$scope.intervals = data.result;
			$scope.sumByIntervals = {}
			$scope.averageIntervals = {}
			getByInterval($http, $scope.intervals.lastMonth,
				function(data, status, headers, config) {
					$scope.intervals.lastMonth.name = "Siste måned";
					$scope.averageIntervals[$scope.intervals.lastMonth.name] = data.result;
				});
			getByInterval($http, $scope.intervals.threeMonths,
				function(data, status, headers, config) {
				$scope.intervals.threeMonths.name = "Siste 3 måneder";
				$scope.averageIntervals[$scope.intervals.threeMonths.name] = data.result;
				});
			getByInterval($http, $scope.intervals.allMonths,
				function(data, status, headers, config) {
				$scope.intervals.allMonths.name = "Siden starten";
				$scope.averageIntervals[$scope.intervals.allMonths.name] = data.result;
				});
			for (var i = 0; i < $scope.intervals.yearIntervals.length; i++) {
				var item = $scope.intervals.yearIntervals[i];
				getByInterval($http, item,
					function(data, status, headers, config, theItem) {
					theItem.name = theItem.year;
					$scope.sumByIntervals[theItem.name] = data.result;
				});
			}
		});
}

function getExpTypeId($scope, key){
	for(var i = 0; i < $scope.expenseTypes.length; i++){
		if(key == $scope.expenseTypes[i].typeName){
			return $scope.expenseTypes[i]._id;
		}
	}
	return "";
}
function findInterval($scope, intervalName){
	if(isNaN(intervalName)){
		var interval = $scope.intervals[intervalName];
		if(!interval) {
			var year = intervalName.substr(0, intervalName.indexOf(" "));
			var yInt = $scope.yearinterval[year];
			for(var i = 0; i < yInt.length; i++){
				var interval = yInt[i];
				var tempName = interval.year + " - " + pad(interval.monthNum, 2) + " " + interval.month
				if(intervalName == tempName){
					return interval;
				}
			}

		}
		return interval;
	}
	else {
		for(var i = 0; i < $scope.intervals.yearIntervals.length; i++){
			var interval = $scope.intervals.yearIntervals[i];
			if(intervalName == interval.year){
				return interval;
			}
		}
	}
}

