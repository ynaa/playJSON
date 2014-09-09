var expDetControllers = angular
		.module(
				"expDetControllers",
				[],
				function($compileProvider) {
					$compileProvider
							.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file|javascript):/);
				});

expDetControllers.controller("ExpenseDetailsController", function($scope,
		$http, $routeParams, $location) {
	$scope.expenseTypes = {};
	//$scope.filterExpType = {};
	$scope.selectedExpDetId = $routeParams.expDetId;
	getExpenseDetails($scope, $http, $scope.selectedExpDetId);
	$scope.newExpDet = {};
	$scope.add = function(newExpDet) {
		var responsePromise = $http.post("/expenseDetails/add", newExpDet, {});
		responsePromise.success(function(dataFromServer, status, headers,
				config) {
			getExpenseDetails($scope, $http, $scope.selectedExpDetId);
			$scope.newExpDet.detName = "";
			$scope.newExpDet.detTags = "";
			$scope.newExpDet.expType = "";
		});
		responsePromise.error(function(data, status, headers, config) {
			alert("Legge til feilet!");
		});
	};
	$scope.filter = function(index) {		
		getExpenseDetails($scope, $http, $scope.filterExpType);
	}
	$scope.edited = function(index) {
		//tags
		var temp = $scope.expDetList[index].searchTags;
		$scope.expDetList[index].searchTags = createJSONList(temp);
		$http.post("/expenseDetails/edit/" + $scope.expDetList[index]._id,
				$scope.expDetList[index], {})
		.success(function(data, stat, heads, cnfg) {
			$scope.expDetList[index].searchTags = temp
		})
		.error(function(data, stat, heads, cnfg) {
			alert("Editering feilet!");
		});
	};
	$scope.deleteExpDetail = function(index) {
		var expDet = $scope.expDetList[index];
		if(confirm("Er du sikker på du vil slette " + expDet.typeName + "?")){
			var responsePromise = $http.delete("/expenseDetails/delete/" + expDet._id, {});
			responsePromise.success(function(dataFromServer, status, headers, config) {
				$scope.expDetList.splice(index, 1);
			});
			responsePromise.error(function(data, status, headers, config) {
				alert("Sletting feilet!");
			});
		}
	 };
});

function getExpenseDetails($scope, $http, selectedExpDetId) {	
	var url = '/expenseDetails/list';
	if (selectedExpDetId) {
		url += '/' + selectedExpDetId
	}
	$http.get(url).success(function(data, status, headers, config) {
		$scope.expDetList = data.result.expDetList;
		$scope.expenseTypes = data.result.expTypesList;
	});
}
function createJSONList(temp){
	try {
		var tags = temp.split(",");
		var json = [];
		for (var i = 0; i < tags.length; i++) {
			json.push(tags[i]);
		}
		return json;
	} catch (e) {
		console.log(e);
		return temp;
	}
}